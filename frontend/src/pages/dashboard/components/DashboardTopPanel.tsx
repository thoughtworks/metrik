import { EditableText } from "../../../components/EditableText";
import { Button, Col, DatePicker, Dropdown, Form, Popover, Row, Select, Typography } from "antd";
import { DownOutlined, FullscreenOutlined, SyncOutlined } from "@ant-design/icons";
import PipelineSetting from "./PipelineSetting";
import React, { FC, KeyboardEvent, useEffect, useState } from "react";
import { css } from "@emotion/react";
import { PRIMARY_COLOR, SECONDARY_COLOR } from "../../../constants/styles";
import moment from "moment";
import { dateFormatYYYYMMDD } from "../../../constants/date-format";
import { MultipleCascadeSelect } from "../../../components/MultipleCascadeSelect";
import { isEmpty } from "lodash";
import {
	formatLastUpdateTime,
	getDurationTimestamps,
} from "../../../utils/timeFormats/timeFormats";
import HintIcon from "../../../components/HintIcon";
import FullscreenDashboard from "./Fullscreen/components/FullscreenDashboard";
import { mapMetricsList, mapPipelines } from "../utils/fullScreenDataProcess";
import { MetricsUnit } from "../../../models/metrics";
import { SyncProgressContent } from "./SyncProgressContent";
import { useQuery } from "../../../hooks/useQuery";
import { useHistory } from "react-router-dom";
import { useDashboardContext } from "../context/DashboardContext";

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
	boxShadow: "2px 2px 3px black",
	padding: "10px",
	backgroundColor: "white",
	"&:hover": { backgroundColor: "grey" },
});

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
	{ period: 1, text: "1s" },
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

export const DashboardTopPanel: FC<DashboardTopPanelProps> = ({ projectId }) => {
	const history = useHistory();
	const query = useQuery();
	const {
		updatingStatus,
		project,
		synchronization,
		pipelineOptions,
		fourKeyMetrics,
		appliedFormValue,
		syncBuildsWithProgress,
		updateProjectName,
		syncFourKeyMetrics,
	} = useDashboardContext();

	const [formValues, setFormValues] = useState<FormValues>(appliedFormValue);
	const durationTimestamps = getDurationTimestamps(formValues.duration);
	const lastUpdateTime = formatLastUpdateTime(synchronization?.synchronizationTimestamp);

	const syncFormValuesToUrl = (formValues: FormValues) => {
		if (!isEmpty(formValues.pipelines)) {
			query.set("pipeline", JSON.stringify(formValues.pipelines));
		}
		if (formValues.unit === MetricsUnit.FORTNIGHTLY || formValues.unit === MetricsUnit.MONTHLY) {
			query.set("unit", formValues.unit);
		}
		if (formValues.duration[0]) {
			query.set("to", formValues.duration[0].format("YYYY-MM-DD"));
		}
		if (formValues.duration[1]) {
			query.set("from", formValues.duration[1].format("YYYY-MM-DD"));
		}
		history.push("/project?" + query.toString());
	};

	const onApply = (formValues: FormValues) => {
		syncFormValuesToUrl(formValues);
		syncFourKeyMetrics(formValues);
	};

	useEffect(() => {
		formValues.pipelines = formValues.pipelines.filter(pipeline =>
			pipelineOptions.some(
				option =>
					option.value === pipeline.value &&
					option.children?.some(childOption => childOption.value === pipeline.childValue)
			)
		);
		setFormValues(formValues);
		onApply(formValues);
	}, [pipelineOptions]);

	// Auto sync related logic
	const [autoSyncPeriod, setAutoSyncPeriod] = useState<AutoSyncOption>(AUTO_SYNC_OPTIONS[0]);
	const [autoSyncJob, setAutoSyncJob] = useState<NodeJS.Timeout | null>(null);
	useEffect(() => {
		if (autoSyncJob) {
			clearInterval(autoSyncJob);
		}
		if (autoSyncPeriod.period) {
			setAutoSyncJob(
				setInterval(() => {
					syncBuildsWithProgress();
				}, autoSyncPeriod.period * 1000)
			);
		}
	}, [autoSyncPeriod]);

	// Full screen related logic
	const [isFullscreenVisible, setIsFullscreenVisible] = useState(false);
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

	const updating = () => updatingStatus.syncInProgress || updatingStatus.pipelineStagesLoading;

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
							syncing={updating()}
						/>
					</div>
					<Popover
						id="waitBar"
						visible={updatingStatus.syncInProgress}
						placement="bottomRight"
						content={<SyncProgressContent progressSummary={updatingStatus.progressSummary} />}
						trigger="click">
						<Button
							id="sync"
							type="primary"
							style={{ borderRadius: "2px 0px 0px 2px" }}
							icon={<SyncOutlined />}
							loading={updatingStatus.syncInProgress}
							onClick={syncBuildsWithProgress}>
							{updatingStatus.syncInProgress ? "Synchronizing" : "Sync Data"}
						</Button>
						<Dropdown trigger={["click"]} overlay={autoSyncOverlay()}>
							<Button
								type="primary"
								onClick={e => e.preventDefault()}
								style={{ borderRadius: "0px 2px 2px 0px" }}>
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
					initialValues={appliedFormValue}
					onFinish={() => {
						if (isEmpty(formValues.pipelines)) {
							return;
						}
						onApply && onApply(formValues);
					}}
					onValuesChange={(_, values) => {
						if (isEmpty(values.pipelines)) {
							return;
						}
						setFormValues(values);
					}}>
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
								<RangePicker format={dateFormatYYYYMMDD} clearIcon={false} disabled={updating()} />
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
								<Select disabled={updating()}>
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
									disabled={updating()}
									options={pipelineOptions}
									defaultValues={
										!isEmpty(pipelineOptions[0]?.children) ? appliedFormValue.pipelines : []
									}
								/>
							</Form.Item>
						</Col>
						<Col style={{ textAlign: "right" }}>
							<Form.Item css={{ marginTop: 40 }}>
								<Button htmlType="submit" disabled={updating() || isEmpty(pipelineOptions)}>
									Apply
								</Button>
							</Form.Item>
						</Col>
					</Row>
				</Form>
			</div>
			<FullscreenDashboard
				projectName={project?.name}
				metricsList={mapMetricsList(fourKeyMetrics, formValues.unit)}
				startTimestamp={durationTimestamps.startTimestamp!}
				endTimestamp={durationTimestamps.endTimestamp!}
				pipelineList={mapPipelines(pipelineOptions, formValues.pipelines)}
				isFullscreenVisible={isFullscreenVisible}
			/>
		</section>
	);
};
