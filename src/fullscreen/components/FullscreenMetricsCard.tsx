import React from "react";
import { MetricsLevel } from "../../shared/clients/metricsApis";
import { Metrics } from "../../shared/__types__/enum";
import AreaChart, { AreaChartProps } from "../../shared/components/AreaChart/AreaChart";

interface FullscreenMetricsCardOptions {
	metricsText: Metrics;
	metricsLevel: MetricsLevel;
	metricsData: number;
	metricsDataLabel: string;
	areaChartProps: AreaChartProps;
}

const FullscreenMetricsCard = ({
	metricsData,
	metricsDataLabel,
	metricsLevel,
	metricsText,
	areaChartProps,
}: FullscreenMetricsCardOptions) => {
	return (
		<>
			<article>
				<p>{metricsText}</p>
				<p>{metricsLevel}</p>
				<p>{metricsData}</p>
				<p>{metricsDataLabel}</p>
				<AreaChart {...areaChartProps} />
			</article>
		</>
	);
};
export default FullscreenMetricsCard;
