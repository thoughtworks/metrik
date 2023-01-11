import {
	getLastSynchronizationUsingGet,
	getProjectDetailsUsingGet,
	Project,
	updateProjectNameUsingPut,
} from "../../../clients/projectApis";
import React, { FC, createContext, useContext, useEffect, useState } from "react";
import { useRequest } from "../../../hooks/useRequest";
import { getPipelineStagesUsingGet, PipelineStages } from "../../../clients/pipelineApis";
import { Option } from "../../../components/MultipleCascadeSelect";
import { ProgressSummary } from "../components/SyncProgressContent";
import { useQuery } from "../../../hooks/useQuery";
import { MetricsInfo } from "../../../models/metrics";
import { isEmpty } from "lodash";

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
	syncInProgress: boolean;
	pipelineStagesLoading: boolean;
	progressSummary: ProgressSummary;
}

interface MetricsData {
	changeFailureRate: MetricsInfo;
	deploymentFrequency: MetricsInfo;
	leadTimeForChange: MetricsInfo;
	meanTimeToRestore: MetricsInfo;
}

interface DashboardState {
	updatingStatus: UpdatingStatus;

	project: Project;
	synchronization: Pick<Project, "synchronizationTimestamp">;
	pipelineOptions: Option[];

	updateProjectName: (name: string) => Promise<void>;
	syncBuildsWithProgress: () => void;
}

const DashboardContext = createContext<DashboardState>({} as DashboardState);

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

	const [pipelineOptions, setPipelineOptions] = useState<Option[]>([]);
	const [progressSummary, setProgressSummary] = useState<ProgressSummary>({});
	const [syncInProgress, setSyncInProgress] = useState(false);

	const getProject = () => getProjectRequest({ projectId });
	const getPipelineStages = () => getPipelineStagesRequest({ projectId });
	const getLastSyncTime = async () => {
		const resp = await getLastSynchronizationRequest({ projectId });
		if (!resp?.synchronizationTimestamp) {
			syncBuildsWithProgress();
		}
	};

	useEffect(() => {
		Promise.all([getLastSyncTime(), getPipelineStages(), getProject()]);
	}, []);

	useEffect(() => {
		const pipelineStages = transformPipelineStages(pipelineStagesResp);
		const options = pipelineStages
			? pipelineStages.filter(v => !isEmpty(v.value) && !isEmpty(v?.children))
			: [];
		setPipelineOptions(options);
	}, [pipelineStagesResp]);

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
			getPipelineStages();
		});
	};

	const updateProjectName = async (name: string) => {
		await updateProjectNameRequest({
			projectId: projectId,
			projectName: name,
		});
		getProject();
	};

	return (
		<DashboardContext.Provider
			value={{
				updatingStatus: {
					syncInProgress,
					pipelineStagesLoading: getPipelineStagesLoading,
					progressSummary,
				},
				project,
				synchronization,
				pipelineOptions,
				updateProjectName,
				syncBuildsWithProgress,
			}}>
			<>{props.children}</>
		</DashboardContext.Provider>
	);
};

export const useDashboardContext = () => useContext(DashboardContext);
