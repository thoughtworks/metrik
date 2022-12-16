import { EditableText } from "../../../components/EditableText";
import { Button, Col, DatePicker, Dropdown, Form, Popover, Row, Select, Typography } from "antd";
import { DownOutlined, FullscreenOutlined, SyncOutlined } from "@ant-design/icons";
import PipelineSetting from "./PipelineSetting";
import React, { FC, KeyboardEvent, useEffect, useState } from "react";
import { css } from "@emotion/react";
import { PRIMARY_COLOR, SECONDARY_COLOR } from "../../../constants/styles";
import { useRequest } from "../../../hooks/useRequest";
import moment from "moment";
import { dateFormatYYYYMMDD } from "../../../constants/date-format";
import { MultipleCascadeSelect } from "../../../components/MultipleCascadeSelect";
import { isEmpty, isEqual } from "lodash";
import {
	formatLastUpdateTime,
	getDurationTimestamps,
} from "../../../utils/timeFormats/timeFormats";
import { usePrevious } from "../../../hooks/usePrevious";
import HintIcon from "../../../components/HintIcon";
import { FourKeyMetrics } from "../../../clients/metricsApis";
import { getPipelineStagesUsingGet, PipelineStages } from "../../../clients/pipelineApis";
import {
	getLastSynchronizationUsingGet,
	getProjectDetailsUsingGet,
	updateProjectNameUsingPut,
} from "../../../clients/projectApis";
import FullscreenDashboard from "./Fullscreen/components/FullscreenDashboard";
import { mapMetricsList, mapPipelines } from "../utils/fullScreenDataProcess";
import { MetricsUnit } from "../../../models/metrics";
import { ProgressSummary, SyncProgressContent } from "./SyncProgressContent";

const { Text } = Typography;
const { RangePicker } = DatePicker;

const containerStyles = css({
	padding: "29px 32px",
});

const headerStyles = css({
	display: "flex",
	alignItems: "center",
	justifyContent: "space-between",
});

const fullScreenStyles = css({
	backgroundColor: SECONDARY_COLOR,
	fontSize: 16,
	borderRadius: 4,
	padding: 10,
	marginLeft: 24,
	cursor: "pointer",
});

const fullScreenIconStyles = css({
	color: PRIMARY_COLOR,
});

const fullScreenTextStyles = css({ marginLeft: 10, color: PRIMARY_COLOR });

const pipelineSettingStyles = css({
	display: "inline-block",
	padding: "4px 24px 4px 0",
});

const autoSyncOverlayStyles = css({
	padding: "10px",
	backgroundColor: "white",
	"&:hover": { backgroundColor: "grey" },
});

const transformPipelineStages = (data: typeof getPipelineStagesUsingGet.TResp = []) =>
	data.map((v: PipelineStages) => ({
		label: v.pipelineName,
		value: v.pipelineId,
		children: v.stages.map((stageName: string) => ({
			label: stageName,
			value: stageName,
		})),
	}));

export interface Pipeline {
	value: string;
	childValue: string;
}

export interface FormValues {
	duration: [moment.Moment, moment.Moment];
	pipelines: Pipeline[];
	unit: MetricsUnit;
}

interface DashboardTopPanelProps {
	projectId: string;
	onApply: (formValues: FormValues) => void;
	metricsResponse: FourKeyMetrics;
}

interface AutoSyncOption {
	period: number | null;
	text: string;
}

const INPUT_FIELD_EXPLANATIONS = {
	TIME_RANGE:
		"The start and end date of the pipeline data sampling where four key metrics are analysed.",
	SAMPLING_INTERVAL: "The data sampling interval for each displayed number.",
	PIPELINE_STAGE:
		"The configured pipeline(s) and their stages will be listed here.The four key metrics data will be analysed based on the selected ones only.",
};

const INPUT_FIELD_LABELS = {
	TIME_RANGE: "Time Range",
	SAMPLING_INTERVAL: "Sampling Interval",
	PIPELINE_STAGE: "Pipeline/Stage",
};

const AUTO_SYNC_OPTIONS: AutoSyncOption[] = [
	{ period: null, text: "off" },
	{ period: 5, text: "5s" },
	{ period: 10, text: "10s" },
	{ period: 30, text: "30s" },
	{ period: 60, text: "1m" },
	{ period: 5 * 60, text: "5m" },
	{ period: 30 * 60, text: "30m" },
	{ period: 60 * 60, text: "1h" },
	{ period: 3 * 60 * 60, text: "3h" },
	{ period: 24 * 60 * 60, text: "1day" },
];

export const DashboardTopPanel: FC<DashboardTopPanelProps> = ({
	projectId,
	onApply,
	metricsResponse,
}) => {
	const [isFullscreenVisible, setIsFullscreenVisible] = useState(false);
	const [autoSyncPeriod, setAutoSyncPeriod] = useState<AutoSyncOption>(AUTO_SYNC_OPTIONS[0]);
	useEffect(() => {
		const btn = document.getElementById("sync");
		setInterval(() => {
			setIsFullscreenVisible(true);
			btn?.click();
			const waitBar = document.getElementById("waitBar");
			if (waitBar) {
				waitBar.style.display = "none";
			}
		}, 1800000);
	}, []);
	const defaultValues = {
		duration: [
			moment(new Date(), dateFormatYYYYMMDD).startOf("day"),
			moment(new Date(), dateFormatYYYYMMDD).endOf("day").subtract(4, "month"),
		] as any,
		unit: "Fortnightly",
		pipelines: [],
	} as FormValues;
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
	const pipelineStages = transformPipelineStages(pipelineStagesResp);

	const [progressSummary, setProgressSummary] = useState<ProgressSummary>({});
	const [syncInProgress, setSyncInProgress] = useState(false);
	const updating = syncInProgress || getPipelineStagesLoading;

	const updateProjectName = async (name: string) => {
		await updateProjectNameRequest({
			projectId: projectId,
			projectName: name,
		});
		getProject();
	};

	const getLastSyncTime = async () => {
		const resp = await getLastSynchronizationRequest({ projectId });
		if (!resp?.synchronizationTimestamp) {
			syncBuildsWithProgress();
		}
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
			getPipelineStages();
		});
	};

	const getProject = () => getProjectRequest({ projectId });

	const getPipelineStages = () => getPipelineStagesRequest({ projectId });

	useEffect(() => {
		getLastSyncTime();
		getPipelineStages();
		getProject();
	}, []);

	const [formValues, setFormValues] = useState<FormValues>(defaultValues);
	const prevPipelines = usePrevious(formValues.pipelines);
	const options = pipelineStages
		? pipelineStages.filter(v => !isEmpty(v.value) && !isEmpty(v?.children))
		: [];
	const prevOptions = usePrevious(options);

	useEffect(() => {
		if (isEmpty(formValues.pipelines)) {
			return;
		}

		if (
			(isEmpty(prevPipelines) && !isEmpty(formValues.pipelines)) ||
			(!isEmpty(options) && !isEqual(prevOptions, options))
		) {
			onApply && onApply(formValues);
		}
	}, [formValues.pipelines, options]);

	const lastUpdateTime = formatLastUpdateTime(synchronization?.synchronizationTimestamp);
	const hideFullscreen = (event: KeyboardEvent<HTMLElement>) => {
		if (event.key === "Escape") {
			setIsFullscreenVisible(false);
			const waitBar = document.getElementById("waitBar");
			if (waitBar) {
				waitBar.style.display = "block";
			}
		}
	};
	const showFullscreen = () => {
		setIsFullscreenVisible(true);
	};
	const durationTimestamps = getDurationTimestamps(formValues.duration);

	const autoSyncOverlay = () => {
		return (
			<div>
				{AUTO_SYNC_OPTIONS.map(o => {
					return (
						<div key={o.period} css={autoSyncOverlayStyles} onClick={() => setAutoSyncPeriod(o)}>
							{o.text}
						</div>
					);
				})}
			</div>
		);
	};

	return (
		<section css={containerStyles} onKeyUp={event => hideFullscreen(event)}>
			<div css={headerStyles}>
				<div>
					{project?.name && (
						<EditableText defaultValue={project?.name ?? ""} onEditDone={updateProjectName} />
					)}
					{lastUpdateTime && (
						<Text type={"secondary"} style={{ display: "inline-block", marginTop: 16 }}>
							Last updated : {lastUpdateTime}
						</Text>
					)}
				</div>
				<div>
					<div css={pipelineSettingStyles}>
						<PipelineSetting
							projectId={projectId}
							syncBuild={syncBuildsWithProgress}
							syncing={updating}
						/>
					</div>
					<Popover
						id="waitBar"
						visible={syncInProgress}
						placement="bottomRight"
						content={<SyncProgressContent progressSummary={progressSummary} />}
						trigger="click">
						<Button
							id="sync"
							type="primary"
							icon={<SyncOutlined />}
							loading={syncInProgress}
							onClick={syncBuildsWithProgress}>
							{syncInProgress ? "Synchronizing" : "Sync Data"}
						</Button>
						<Dropdown trigger={["click"]} overlay={autoSyncOverlay()}>
							<Button type="primary" onClick={e => e.preventDefault()}>
								{autoSyncPeriod.period && autoSyncPeriod.text}
								<DownOutlined />
							</Button>
						</Dropdown>
					</Popover>
					<span css={fullScreenStyles} onClick={showFullscreen}>
						<FullscreenOutlined css={fullScreenIconStyles} />
						<Text css={fullScreenTextStyles}>Full Screen</Text>
					</span>
				</div>
			</div>
			<div css={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
				<Form
					layout={"vertical"}
					css={{ marginTop: 16 }}
					initialValues={defaultValues}
					onFinish={() => {
						if (isEmpty(formValues.pipelines)) {
							return;
						}
						onApply && onApply(formValues);
					}}
					onValuesChange={(_, values) => setFormValues(values)}>
					<Row wrap={false} gutter={16} align={"bottom"}>
						<Col css={{ minWidth: 260 }}>
							<Form.Item
								label={
									<HintIcon
										text={INPUT_FIELD_LABELS.TIME_RANGE}
										tooltip={INPUT_FIELD_EXPLANATIONS.TIME_RANGE}
									/>
								}
								name="duration">
								<RangePicker format={dateFormatYYYYMMDD} clearIcon={false} disabled={updating} />
							</Form.Item>
						</Col>
						<Col>
							<Form.Item
								label={
									<HintIcon
										text={INPUT_FIELD_LABELS.SAMPLING_INTERVAL}
										tooltip={INPUT_FIELD_EXPLANATIONS.SAMPLING_INTERVAL}
									/>
								}
								name="unit">
								<Select disabled={updating}>
									<Select.Option value="Fortnightly">Fortnightly</Select.Option>
									<Select.Option value="Monthly">Monthly</Select.Option>
								</Select>
							</Form.Item>
						</Col>
						<Col>
							<Form.Item
								label={
									<HintIcon
										text={INPUT_FIELD_LABELS.PIPELINE_STAGE}
										tooltip={INPUT_FIELD_EXPLANATIONS.PIPELINE_STAGE}
									/>
								}
								name="pipelines">
								<MultipleCascadeSelect
									disabled={updating}
									options={options}
									defaultValues={
										!isEmpty(options[0]?.children)
											? [
													{
														value: formValues.pipelines[0]?.value || options[0]?.value,
														childValue:
															formValues.pipelines[0]?.childValue ||
															(options[0]?.children ?? [])[0]?.label,
													},
											  ]
											: []
									}
								/>
							</Form.Item>
						</Col>
						<Col style={{ textAlign: "right" }}>
							<Form.Item css={{ marginTop: 40 }}>
								<Button htmlType="submit" disabled={updating || isEmpty(options)}>
									Apply
								</Button>
							</Form.Item>
						</Col>
					</Row>
				</Form>
			</div>
			<FullscreenDashboard
				projectName={project?.name}
				metricsList={mapMetricsList(metricsResponse, formValues.unit)}
				startTimestamp={durationTimestamps.startTimestamp!}
				endTimestamp={durationTimestamps.endTimestamp!}
				pipelineList={mapPipelines(options, formValues.pipelines)}
				isFullscreenVisible={isFullscreenVisible}
			/>
		</section>
	);
};
