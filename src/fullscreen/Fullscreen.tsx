import React from "react";
import FullscreenMetricsCard, {
	ChartData,
	FullscreenMetricsCardOptions,
} from "./components/FullscreenMetricsCard";
import { MetricsLevel, MetricsType } from "../shared/__types__/enum";

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
			metricsData: 32.31,
			metricsLevel: MetricsLevel.ELITE,
			metricsDataLabel: "AVG/Times / Fortnight",
			metricsText: MetricsType.DEPLOYMENT_FREQUENCY,
			data: data,
		},
		{
			metricsData: 31.1,
			metricsLevel: MetricsLevel.LOW,
			metricsDataLabel: "AVG Days",
			metricsText: MetricsType.LEAD_TIME_FOR_CHANGE,
			data: data,
		},
		{
			metricsData: 3.31,
			metricsLevel: MetricsLevel.HIGH,
			metricsDataLabel: "AVG Hours",
			metricsText: MetricsType.MEAN_TIME_TO_RESTORE,
			data: data,
		},
		{
			metricsData: "41.31%",
			metricsLevel: MetricsLevel.MEDIUM,
			metricsDataLabel: "AVG%",
			metricsText: MetricsType.CHANGE_FAILURE_RATE,
			data: data,
		},
	];
	const chartSectionStyle = {
		width: "70%",
		display: "flex",
		flexWrap: "wrap" as const,
		justifyContent: "space-between",
	};
	const metricsCardStyle = {
		height: "48%",
	};
	const pageStyle = {
		width: "100%",
		height: "100%",
		backgroundColor: "#000",
	};
	return (
		<section css={pageStyle}>
			<section css={chartSectionStyle}>
				{metricsList.map((metrics, index) => (
					<FullscreenMetricsCard
						css={metricsCardStyle}
						key={index}
						metricsData={metrics.metricsData}
						metricsDataLabel={metrics.metricsDataLabel}
						metricsLevel={metrics.metricsLevel}
						metricsText={metrics.metricsText}
						data={data}
					/>
				))}
			</section>
		</section>
	);
};
export default FullScreen;
