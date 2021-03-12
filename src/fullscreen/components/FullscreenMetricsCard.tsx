import React from "react";
import { MetricsType, MetricsLevel } from "../../shared/__types__/enum";
import AreaChart from "../../shared/components/AreaChart/AreaChart";

export interface ChartData {
	name: string;
	uv: number;
	pv: number;
}
interface FullscreenMetricsCardOptions {
	metricsText: MetricsType;
	metricsLevel: MetricsLevel;
	metricsData: number;
	metricsDataLabel: string;
	data: ChartData[];
}

const FullscreenMetricsCard = ({
	metricsData,
	metricsDataLabel,
	metricsLevel,
	metricsText,
	data,
}: FullscreenMetricsCardOptions) => {
	return (
		<>
			<article>
				<p>{metricsText}</p>
				<p>{metricsLevel}</p>
				<p>{metricsData}</p>
				<p>{metricsDataLabel}</p>
				<AreaChart
					data={data}
					dataKey={"pv"}
					width={730}
					height={250}
					strokeColor={"#82ca9d"}
					strokeWidth={3}
					areaGradientColor={"#f1db42"}
					curveType={"monotone"}
				/>
			</article>
		</>
	);
};
export default FullscreenMetricsCard;
