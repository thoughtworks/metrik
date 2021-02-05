import React, { useState } from "react";
import { Col, Row } from "antd";
import { css } from "@emotion/react";
import { useQuery } from "../hooks/useQuery";
import { getFourKeyMetricsUsingGet } from "../clients/apis";
import { momentObjToEndTimeStamp, momentObjToStartTimeStamp } from "../utils/timeFormats";
import { MetricsCard, MetricsLevel } from "./components/MetricsCard";
import { DashboardTopPanel, FormValues } from "./components/DashboardTopPanel";

const metricsContainerStyles = css({
	padding: "37px 35px",
	background: "#F0F2F5",
});

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
	const query = useQuery();
	const dashboardId = query.get("dashboardId") || "";

	const [formValues, setFormValues] = useState<FormValues>({} as FormValues);
	const [changeFailureRate, setChangeFailureRate] = useState<MetricsDataState>(initialMetricsState);
	const [deploymentFrequency, setDeploymentFrequency] = useState<MetricsDataState>(
		initialMetricsState
	);
	const [leadTimeForChange, setLeadTimeForChange] = useState<MetricsDataState>(initialMetricsState);
	const [meanTimeToRestore, setMeanTimeToRestore] = useState<MetricsDataState>(initialMetricsState);
	const [loadingChart, setLoadingChart] = useState(false);

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
			<DashboardTopPanel
				onFormFinish={getFourKeyMetrics}
				dashboardId={dashboardId}
				onSyncBuildsSuccess={getFourKeyMetrics}
				onFormValuesChange={(_, values) => setFormValues(values)}
			/>
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
