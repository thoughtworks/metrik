import React from "react";
import { ChartData, FullscreenMetricsCardOptions } from "./components/FullscreenMetricsCard";
import { MetricsLevel, MetricsType } from "../shared/__types__/enum";
import FullscreenDashboard from "./components/FullscreenDashboard";

const FullScreen = () => {
	const data: ChartData[] = [
		{ name: "Page A", uv: 400, pv: 0 },
		{ name: "Page A", uv: 100, pv: 0 },
		{ name: "Page B", uv: 200, pv: 0 },
		{ name: "Page C", uv: 300, pv: 31 },
		{ name: "Page C", uv: 300, pv: 10 },
		{ name: "Page C", uv: 300, pv: 0 },
	];

	const metricsList: FullscreenMetricsCardOptions[] = [
		{
			metricsSummaryData: 32.31,
			metricsLevel: MetricsLevel.ELITE,
			metricsDataLabel: "AVG/Times / Fortnight",
			metricsText: MetricsType.DEPLOYMENT_FREQUENCY,
			data: data,
		},
		{
			metricsSummaryData: 31.1,
			metricsLevel: MetricsLevel.LOW,
			metricsDataLabel: "AVG Days",
			metricsText: MetricsType.LEAD_TIME_FOR_CHANGE,
			data: data,
		},
		{
			metricsSummaryData: 3.31,
			metricsLevel: MetricsLevel.HIGH,
			metricsDataLabel: "AVG Hours",
			metricsText: MetricsType.MEAN_TIME_TO_RESTORE,
			data: data,
		},
		{
			metricsSummaryData: "41.31%",
			metricsLevel: MetricsLevel.MEDIUM,
			metricsDataLabel: "AVG%",
			metricsText: MetricsType.CHANGE_FAILURE_RATE,
			data: data,
		},
	];

	const projectName = "My Project";
	const pipelineList = [
		"2km: 0km-dev",
		"2km: 1km-12324324355656-dev-mengqiu-hehe",
		"2km: 2km-dev",
		"2km: 3km-dev-rheuerrrrr",
		"2km: 4km",
		"2km: 5km",
		"2km: 6km",
		"2km: 7km",
	];
	return (
		<FullscreenDashboard
			projectName={projectName}
			metricsList={metricsList}
			startTimestamp={1615974249118}
			endTimestamp={1615974249118}
			pipelineList={pipelineList}
		/>
	);
};
export default FullScreen;
