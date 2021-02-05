import React, { useEffect, useState } from "react";
import { FullscreenOutlined, SyncOutlined } from "@ant-design/icons";
import { Button, Col, DatePicker, Form, Row, Select, Typography } from "antd";
import { PRIMARY_COLOR, SECONDARY_COLOR } from "../constants/styles";
import { css } from "@emotion/react";
import moment from "moment";
import { dateFormatYYYYMMDD } from "../constants/date-format";
import { MultipleCascadeSelect, Option } from "../components/MultipleCascadeSelect";
import { EditableText } from "../components/EditableText";
import { useQuery } from "../hooks/useQuery";
import {
	getDashboardUsingGet,
	getFourKeyMetricsUsingGet,
	getLastSynchronizationUsingGet,
	getPipelineStagesUsingGet,
	PipelineStagesResponse,
	updateBuildsUsingPost,
	updateDashboardNameUsingPut,
} from "../clients/apis";
import PipelineSetting from "./components/PipelineSetting";
import {
	formatLastUpdateTime,
	momentObjToEndTimeStamp,
	momentObjToStartTimeStamp,
} from "../utils/timeFormats";
import { isEmpty } from "lodash";
import { MetricsCard, MetricsLevel } from "./components/MetricsCard";

const { Text } = Typography;
const { RangePicker } = DatePicker;

const containerStyles = css({
	padding: "29px 32px",
});

const dividerStyles = css({
	display: "inline-block",
	borderRight: `1px solid ${SECONDARY_COLOR}`,
	padding: "4px 24px 4px 0",
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

const headerStyles = css({
	display: "flex",
	alignItems: "center",
	justifyContent: "space-between",
});

const fullScreenTextStyles = css({ marginLeft: 10, color: PRIMARY_COLOR });
const metricsContainerStyles = css({
	padding: "37px 35px",
	background: "#F0F2F5",
});

interface FormValues {
	duration: [moment.Moment, moment.Moment];
	pipelines: Array<{ value: string; childValue: string }>;
	unit: "Fortnightly" | "Monthly";
}

export interface MetricsDataItem {
	level: MetricsLevel;
	startTimestamp: number;
	endTimestamp: number;
	value: number | string;
}

interface MetricsDataState {
	summary: MetricsDataItem;
	details: MetricsDataItem[];
}

const initialMetricsState: MetricsDataState = {
	summary: {
		endTimestamp: 0,
		level: MetricsLevel.NA,
		startTimestamp: 0,
		value: "",
	},
	details: [],
};

export const PageDashboard = () => {
	const [syncing, setSyncing] = useState(false);
	const query = useQuery();
	const dashboardId = query.get("dashboardId") || "";
	const [lastModifyDateTime, setLastModifyDateTime] = useState("");
	const [pipelineStages, setPipelineStages] = useState<Option[]>([]);
	const [dashboardName, setDashboardName] = useState("");
	const [formValues, setFormValues] = useState<FormValues>({} as FormValues);
	const [changeFailureRate, setChangeFailureRate] = useState<MetricsDataState>(initialMetricsState);
	const [deploymentFrequency, setDeploymentFrequency] = useState<MetricsDataState>(
		initialMetricsState
	);
	const [leadTimeForChange, setLeadTimeForChange] = useState<MetricsDataState>(initialMetricsState);
	const [meanTimeToRestore, setMeanTimeToRestore] = useState<MetricsDataState>(initialMetricsState);
	const [loadingChart, setLoadingChart] = useState(false);

	const syncBuilds = () => {
		setSyncing(true);
		updateBuildsUsingPost({
			dashboardId,
		})
			.then(() => {
				getLastSyncTime();
				getPipelineStages();
			})
			.finally(() => {
				setSyncing(false);
			});
	};

	const updateDashboardName = (name: string) =>
		updateDashboardNameUsingPut({
			dashboardId,
			requestBody: name,
		}).then(() => {
			getDashboard();
		});

	const getPipelineStages = () => {
		getPipelineStagesUsingGet({ dashboardId }).then(resp => {
			setPipelineStages(
				resp.map((v: PipelineStagesResponse) => ({
					label: v.pipelineName,
					value: v.pipelineId,
					children: v.stages.map((stageName: string) => ({
						label: stageName,
						value: stageName,
					})),
				}))
			);
		});
	};

	const getDashboard = () => {
		getDashboardUsingGet({ dashboardId }).then(resp => {
			setDashboardName(resp.name);
		});
	};

	const getLastSyncTime = () => {
		getLastSynchronizationUsingGet({ dashboardId }).then(resp => {
			setLastModifyDateTime(formatLastUpdateTime(resp?.synchronizationTimestamp));
			if (!resp?.synchronizationTimestamp) {
				syncBuilds();
			}
		});
	};

	useEffect(() => {
		getLastSyncTime();
		getDashboard();
		getPipelineStages();
	}, []);

	const getFourKeyMetrics = () => {
		setLoadingChart(true);
		// TODO: will pass multiple stages and pipelines after backend api ready
		getFourKeyMetricsUsingGet({
			endTime: momentObjToEndTimeStamp(formValues.duration[0]),
			startTime: momentObjToStartTimeStamp(formValues.duration[1]),
			pipelineId: formValues.pipelines[0].value,
			targetStage: formValues.pipelines[0].childValue,
			unit: formValues.unit,
		}).finally(() => {
			const mockData: MetricsDataState = {
				details: [
					{
						endTimestamp: 1613260800000,
						level: MetricsLevel.ELITE,
						startTimestamp: 1612137600000,
						value: 20,
					},
					{
						endTimestamp: 1614384000000,
						level: MetricsLevel.ELITE,
						startTimestamp: 1613260800000,
						value: 21,
					},
					{
						endTimestamp: 1615507200000,
						level: MetricsLevel.ELITE,
						startTimestamp: 1614384000000,
						value: 10,
					},
					{
						endTimestamp: 1616630400000,
						level: MetricsLevel.ELITE,
						startTimestamp: 1615507200000,
						value: 15,
					},
					{
						endTimestamp: 1613260800000,
						level: MetricsLevel.ELITE,
						startTimestamp: 1612137600000,
						value: 20,
					},
					{
						endTimestamp: 1614384000000,
						level: MetricsLevel.ELITE,
						startTimestamp: 1613260800000,
						value: 21,
					},
					{
						endTimestamp: 1615507200000,
						level: MetricsLevel.ELITE,
						startTimestamp: 1614384000000,
						value: 10,
					},
					{
						endTimestamp: 1616630400000,
						level: MetricsLevel.ELITE,
						startTimestamp: 1615507200000,
						value: 15,
					},
				],
				summary: {
					endTimestamp: 0,
					level: MetricsLevel.ELITE,
					startTimestamp: 0,
					value: 20.5,
				},
			};
			setChangeFailureRate(mockData);
			setDeploymentFrequency(mockData);
			setLeadTimeForChange(mockData);
			setMeanTimeToRestore(mockData);
			setLoadingChart(false);
		});
	};

	return (
		<>
			<div css={containerStyles}>
				<div css={headerStyles}>
					<div>
						{dashboardName && (
							<EditableText defaultValue={dashboardName} onEditDone={updateDashboardName} />
						)}
						<Text type={"secondary"}>The latest available data end at : {lastModifyDateTime}</Text>
						<Button type="link" icon={<SyncOutlined />} loading={syncing} onClick={syncBuilds}>
							{syncing ? "Synchronizing...." : "Sync Data"}
						</Button>
					</div>
					<div>
						<span css={dividerStyles}>
							<PipelineSetting dashboardId={dashboardId} />
						</span>
						<span css={fullScreenStyles}>
							<FullscreenOutlined css={fullScreenIconStyles} />
							<Text css={fullScreenTextStyles}>Full Screen</Text>
						</span>
					</div>
				</div>
				<div css={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
					<Form
						layout={"vertical"}
						css={{ marginTop: 16, width: "50%" }}
						initialValues={{
							duration: [
								moment(new Date(), dateFormatYYYYMMDD).startOf("day"),
								moment(new Date(), dateFormatYYYYMMDD).endOf("day").subtract(4, "month"),
							],
							unit: "Fortnightly",
						}}
						onFinish={getFourKeyMetrics}
						onValuesChange={(_, values) => setFormValues(values)}>
						<Row wrap={false} gutter={12}>
							<Col>
								<Form.Item label="Duration" name="duration">
									<RangePicker format={dateFormatYYYYMMDD} clearIcon={false} />
								</Form.Item>
							</Col>
							<Col span={10}>
								<Form.Item label="Pipelines" name="pipelines">
									<MultipleCascadeSelect
										options={isEmpty(pipelineStages[0]?.children) ? [] : pipelineStages}
										defaultValues={
											!isEmpty(pipelineStages[0]?.children)
												? [
														{
															value: pipelineStages[0]?.value,
															childValue: (pipelineStages[0]?.children ?? [])[0]?.label,
														},
												  ]
												: []
										}
									/>
								</Form.Item>
							</Col>
							<Col span={4}>
								<Form.Item label="Unit" name="unit">
									<Select>
										<Select.Option value="Fortnightly">Fortnightly</Select.Option>
										<Select.Option value="Monthly">Monthly</Select.Option>
									</Select>
								</Form.Item>
							</Col>
							<Col style={{ textAlign: "right" }}>
								<Form.Item label=" ">
									<Button htmlType="submit" disabled={syncing}>
										Apply
									</Button>
								</Form.Item>
							</Col>
						</Row>
					</Form>
				</div>
			</div>

			<div css={metricsContainerStyles}>
				<Row gutter={28}>
					<Col xs={24} sm={24} md={24} lg={12}>
						<MetricsCard
							title="Deployment Frequency (Times)"
							summary={deploymentFrequency.summary}
							data={deploymentFrequency.details}
							yaxisFormatter={(value: string) => value}
							unit="Times"
							loading={loadingChart}
						/>
					</Col>

					<Col xs={24} sm={24} md={24} lg={12}>
						<MetricsCard
							title="Mean Lead Time for Change (Days)"
							summary={leadTimeForChange.summary}
							data={leadTimeForChange.details}
							yaxisFormatter={(value: string) => value}
							unit="Days"
							loading={loadingChart}
						/>
					</Col>

					<Col xs={24} sm={24} md={24} lg={12}>
						<MetricsCard
							title="Mean Time to Restore Service (Hours)"
							summary={meanTimeToRestore.summary}
							data={meanTimeToRestore.details}
							yaxisFormatter={(value: string) => value}
							unit="Hours"
							loading={loadingChart}
						/>
					</Col>

					<Col xs={24} sm={24} md={24} lg={12}>
						<MetricsCard
							title="Change Failure Rate"
							summary={changeFailureRate.summary}
							data={changeFailureRate.details}
							yaxisFormatter={(value: string) => value + "%"}
							unit="Percentage"
							loading={loadingChart}
						/>
					</Col>
				</Row>
			</div>
		</>
	);
};
