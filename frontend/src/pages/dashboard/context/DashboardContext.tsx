import {
	getLastSynchronizationUsingGet,
	getProjectDetailsUsingGet,
	Project,
	updateProjectNameUsingPut,
} from "../../../clients/projectApis";
import React, { createContext, FC, useContext, useEffect, useState } from "react";
import { useRequest } from "../../../hooks/useRequest";
import { getPipelineStagesUsingGet, PipelineStages } from "../../../clients/pipelineApis";
import { Option } from "../../../components/MultipleCascadeSelect";
import { ProgressSummary } from "../components/SyncProgressContent";
import { useQuery } from "../../../hooks/useQuery";
import { MetricsInfo, MetricsLevel, MetricsUnit } from "../../../models/metrics";
import { isEmpty } from "lodash";
import { FormValues, Pipeline } from "../components/DashboardTopPanel";
import { FourKeyMetrics, getFourKeyMetricsUsingPost } from "../../../clients/metricsApis";
import { getDurationTimestamps } from "../../../utils/timeFormats/timeFormats";
import moment from "moment/moment";
import { dateFormatYYYYMMDD } from "../../../constants/date-format";

const initialMetricsState: MetricsInfo = {
	summary: {
		level: MetricsLevel.INVALID,
		value: 0,
		endTimestamp: 0,
		startTimestamp: 0,
	},
	details: [],
};

const transformPipelineStages = (data: typeof getPipelineStagesUsingGet.TResp = []) =>
	data.map((v: PipelineStages) => ({
		label: v.pipelineName,
		value: v.pipelineId,
		children: v.stages.map((stageName: string) => ({
			label: stageName,
			value: stageName,
		})),
	}));

interface UpdatingStatus {
	inited: boolean;
	syncInProgress: boolean;
	pipelineStagesLoading: boolean;
	loadingMetricsData: boolean;
	progressSummary: ProgressSummary;
}

interface DashboardState {
	updatingStatus: UpdatingStatus;

	project: Project;
	synchronization: Pick<Project, "synchronizationTimestamp">;
	pipelineOptions: Option[];

	appliedFormValue: FormValues;
	fourKeyMetrics: FourKeyMetrics;

	updateProjectName: (name: string) => Promise<void>;
	syncBuildsWithProgress: () => void;
	syncFourKeyMetrics: (formValues: FormValues) => void;
}

const DashboardContext = createContext<DashboardState>({} as DashboardState);

const generateDefaultValueFromQuery = (query: URLSearchParams, pipelineOptions: Option[]) => {
	const fromDate = moment(query.get("to"), dateFormatYYYYMMDD).isValid()
		? moment(query.get("to"), dateFormatYYYYMMDD).startOf("day")
		: moment(new Date(), dateFormatYYYYMMDD).startOf("day");
	const toDate = moment(query.get("from"), dateFormatYYYYMMDD).isValid()
		? moment(query.get("from"), dateFormatYYYYMMDD).endOf("day")
		: moment(fromDate.toDate(), dateFormatYYYYMMDD).endOf("day").subtract(4, "month");

	const pipelines: Pipeline[] = [];
	try {
		const paramPipelines = JSON.parse(query.get("pipeline") || "[]") as Pipeline[];
		for (const now of paramPipelines) {
			pipelines.push({ value: now.value, childValue: now.childValue });
		}
	} catch (ignore) {
		console.error("pipeline param in url is not valid");
	}
	if (isEmpty(pipelines) && !isEmpty(pipelineOptions)) {
		pipelines.push({
			value: pipelineOptions[0]?.value,
			childValue: (pipelineOptions[0]?.children ?? [])[0]?.label,
		});
	}

	return {
		duration: [fromDate, toDate],
		unit: query.get("unit") === MetricsUnit.MONTHLY ? MetricsUnit.MONTHLY : MetricsUnit.FORTNIGHTLY,
		pipelines: pipelines,
	} as FormValues;
};

export const DashboardContextProvider: FC = props => {
	const query = useQuery();
	const projectId = query.get("projectId") || "";

	const [project, getProjectRequest] = useRequest(getProjectDetailsUsingGet);
	const [, updateProjectNameRequest] = useRequest(updateProjectNameUsingPut);
	const [synchronization, getLastSynchronizationRequest] = useRequest(
		getLastSynchronizationUsingGet
	);
	const [
		pipelineStagesResp,
		getPipelineStagesRequest,
		getPipelineStagesLoading,
		setPipelineStages,
	] = useRequest(getPipelineStagesUsingGet);

	const [inited, setInited] = useState(false);

	const [pipelineOptions, setPipelineOptions] = useState<Option[]>([]);
	const [progressSummary, setProgressSummary] = useState<ProgressSummary>({});
	const [syncInProgress, setSyncInProgress] = useState(false);

	const [metricsLoading, setMetricsLoading] = useState(false);
	const [appliedFormValue, setAppliedFormValue] = useState<FormValues>({} as FormValues);
	const [fourKeyMetrics, setFourKeyMetrics] = useState<FourKeyMetrics>({
		changeFailureRate: initialMetricsState,
		deploymentFrequency: initialMetricsState,
		leadTimeForChange: initialMetricsState,
		meanTimeToRestore: initialMetricsState,
	});

	useEffect(() => {
		Promise.all([
			getPipelineStagesRequest({ projectId }),
			getLastSyncTime(),
			getProjectRequest({ projectId }),
		]).then(values => {
			const pipelineStages = transformPipelineStages(values[0]);
			const options = pipelineStages
				? pipelineStages.filter(v => !isEmpty(v.value) && !isEmpty(v?.children))
				: [];
			const defaultFormValue = generateDefaultValueFromQuery(query, options);
			setPipelineOptions(options);
			syncFourKeyMetrics(defaultFormValue);
			setInited(true);
		});
	}, []);

	useEffect(() => {
		const pipelineStages = transformPipelineStages(pipelineStagesResp);
		const options = pipelineStages
			? pipelineStages.filter(v => !isEmpty(v.value) && !isEmpty(v?.children))
			: [];
		if (!isEmpty(options)) {
			setPipelineOptions(options);
		}
	}, [pipelineStagesResp]);

	const getLastSyncTime = async () => {
		const resp = await getLastSynchronizationRequest({ projectId });
		if (!resp?.synchronizationTimestamp) {
			syncBuildsWithProgress();
		}
	};

	const syncFourKeyMetrics = (formValues: FormValues) => {
		setMetricsLoading(true);
		setAppliedFormValue(formValues);

		const durationTimestamps = getDurationTimestamps(formValues.duration);
		getFourKeyMetricsUsingPost({
			metricsQuery: {
				startTime: durationTimestamps.startTimestamp!,
				endTime: durationTimestamps.endTimestamp!,
				pipelineStages: (formValues.pipelines || []).map(i => ({
					pipelineId: i.value,
					stage: i.childValue,
				})),
				unit: formValues.unit,
			},
		})
			.then(response => {
				setFourKeyMetrics(response);
			})
			.finally(() => {
				setMetricsLoading(false);
			});
	};

	const syncBuildsWithProgress = () => {
		setSyncInProgress(true);

		const eventsource = new EventSource(`/api/project/${projectId}/sse-sync`);
		eventsource.addEventListener("PROGRESS_UPDATE_EVENT", e => {
			// eslint-disable-next-line @typescript-eslint/ban-ts-comment
			// @ts-ignore
			const progressUpdate: ProgressUpdateEvt = JSON.parse(e.data);
			setProgressSummary(prevState => ({
				...prevState,
				[progressUpdate.pipelineId]: progressUpdate,
			}));
		});
		eventsource.addEventListener("COMPLETE_STREAM_EVENT", () => {
			eventsource.close();
			setSyncInProgress(false);
			setProgressSummary({});

			getLastSyncTime();
			setPipelineStages([]);
			getPipelineStagesRequest({ projectId });
		});
	};

	const updateProjectName = async (name: string) => {
		await updateProjectNameRequest({
			projectId: projectId,
			projectName: name,
		});
		await getProjectRequest({ projectId });
	};

	return (
		<DashboardContext.Provider
			value={{
				updatingStatus: {
					inited,
					syncInProgress,
					pipelineStagesLoading: getPipelineStagesLoading,
					progressSummary,
					loadingMetricsData: metricsLoading,
				},
				project,
				synchronization,
				pipelineOptions,
				appliedFormValue,
				fourKeyMetrics,
				updateProjectName,
				syncBuildsWithProgress,
				syncFourKeyMetrics,
			}}>
			<>{props.children}</>
		</DashboardContext.Provider>
	);
};

export const useDashboardContext = () => useContext(DashboardContext);
