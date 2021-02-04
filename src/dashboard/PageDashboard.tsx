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
import { MetricsCard } from "./components/MetricsCard";

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

export const PageDashboard = () => {
	const [syncing, setSyncing] = useState(false);
	const query = useQuery();
	const dashboardId = query.get("dashboardId") || "";
	const [lastModifyDateTime, setLastModifyDateTime] = useState("");
	const [pipelineStages, setPipelineStages] = useState<Option[]>([]);
	const [dashboardName, setDashboardName] = useState("");

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

	const onFinish = (values: FormValues) => {
		// TODO: will pass multiple stages and pipelines after backend api ready
		getFourKeyMetricsUsingGet({
			endTime: momentObjToEndTimeStamp(values.duration[0]),
			startTime: momentObjToStartTimeStamp(values.duration[1]),
			pipelineId: values.pipelines[0].value,
			targetStage: values.pipelines[0].childValue,
			unit: values.unit,
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
						onFinish={onFinish}>
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
							summary={{
								level: "LOW",
								average: "0.21",
							}}
							data={[
								{
									name: "Page A",
									value: 30,
								},
								{
									name: "Page B",
									value: 30,
								},
								{
									name: "Page C",
									value: 40,
								},
								{
									name: "Page D",
									value: 30,
								},
								{
									name: "Page E",
									value: 20,
								},
								{
									name: "Page F",
									value: 30,
								},
								{
									name: "Page G",
									value: 10,
								},
							]}
							yaxisFormatter={(value: string) => value}
							unit="Times"
						/>
					</Col>

					<Col xs={24} sm={24} md={24} lg={12}>
						<MetricsCard
							title="Mean Lead Time for Change (Days)"
							summary={{
								level: "ELITE",
								average: "0.21",
							}}
							data={[
								{
									name: "Page A",
									value: 30,
								},
								{
									name: "Page B",
									value: 30,
								},
								{
									name: "Page C",
									value: 40,
								},
								{
									name: "Page D",
									value: 30,
								},
								{
									name: "Page E",
									value: 20,
								},
								{
									name: "Page F",
									value: 30,
								},
								{
									name: "Page G",
									value: 10,
								},
							]}
							yaxisFormatter={(value: string) => value}
							unit="Days"
						/>
					</Col>

					<Col xs={24} sm={24} md={24} lg={12}>
						<MetricsCard
							title="Mean Time to Restore Service (Hours)"
							summary={{
								level: "MEDIUM",
								average: "0.21",
							}}
							data={[
								{
									name: "Page A",
									value: 30,
								},
								{
									name: "Page B",
									value: 30,
								},
								{
									name: "Page C",
									value: 40,
								},
								{
									name: "Page D",
									value: 30,
								},
								{
									name: "Page E",
									value: 20,
								},
								{
									name: "Page F",
									value: 30,
								},
								{
									name: "Page G",
									value: 10,
								},
							]}
							yaxisFormatter={(value: string) => value}
							unit="Hours"
						/>
					</Col>

					<Col xs={24} sm={24} md={24} lg={12}>
						<MetricsCard
							title="Change Failure Rate"
							summary={{
								level: "NA",
								average: "--",
							}}
							data={[
								{
									name: "Page A",
									value: 10,
								},
								{
									name: "Page B",
									value: 30,
								},
								{
									name: "Page C",
								},
								{
									name: "Page D",
									value: 30,
								},
								{
									name: "Page E",
									value: 20,
								},
								{
									name: "Page F",
									value: 0,
								},
								{
									name: "Page G",
									value: 10,
								},
							]}
							yaxisFormatter={(value: string) => value + "%"}
							unit="Percentage"
						/>
					</Col>
				</Row>
			</div>
		</>
	);
};
