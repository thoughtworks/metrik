import React from "react";
import FullscreenMetricsCard, { ChartData } from "./components/FullscreenMetricsCard";
import { MetricsLevel, MetricsType } from "../shared/__types__/enum";

const FullScreen = () => {
	const data: ChartData[] = [
		{ name: "Page A", uv: 400, pv: 2400 },
		{
			name: "Page A",
			uv: 400,
			pv: 2400,
		},
		{ name: "Page A", uv: 100, pv: 1000 },
		{ name: "Page B", uv: 200, pv: 2000 },
		{ name: "Page C", uv: 300, pv: 3000 },
	];

	return (
		<FullscreenMetricsCard
			metricsData={32.31}
			metricsDataLabel={"AVG/Times / Fortnight"}
			metricsLevel={MetricsLevel.ELITE}
			metricsText={MetricsType.DEPLOYMENT_FREQUENCY}
			data={data}
		/>
	);
};
export default FullScreen;
