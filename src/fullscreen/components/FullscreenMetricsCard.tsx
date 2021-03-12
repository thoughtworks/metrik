import React from "react";
import { MetricsType, MetricsLevel } from "../../shared/__types__/enum";
import AreaChart from "../../shared/components/AreaChart/AreaChart";
import Word from "../../shared/components/Word/Word";

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
				<p>
					<Word text={metricsText} type="large" />
				</p>
				<p>
					<Word text={metricsLevel} type="medium" />
				</p>
				<p>
					<Word text={metricsData} type="medium" />
				</p>
				<p>
					<Word text={metricsDataLabel} type="small" />
				</p>
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
