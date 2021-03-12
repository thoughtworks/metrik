import React from "react";
import { MetricsType, MetricsLevel } from "../../shared/__types__/enum";
import AreaChart from "../../shared/components/AreaChart/AreaChart";
import Word from "../../shared/components/Word/Word";
import { BLUE_5, GRAY_6, GREEN_DARK, ORANGE_DARK, RED_DARK } from "../../shared/constants/styles";

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
interface MetricsLevelBaseData {
	color: string;
}

type MetricsLevelBaseConfig = {
	[key in MetricsLevel]: MetricsLevelBaseData;
};

const MetricsLevelConfig: MetricsLevelBaseConfig = {
	[MetricsLevel.ELITE]: {
		color: GREEN_DARK,
	},
	[MetricsLevel.HIGH]: {
		color: BLUE_5,
	},
	[MetricsLevel.MEDIUM]: {
		color: ORANGE_DARK,
	},
	[MetricsLevel.LOW]: {
		color: RED_DARK,
	},
	[MetricsLevel.INVALID]: {
		color: GRAY_6,
	},
};

const FullscreenMetricsCard = ({
	metricsData,
	metricsDataLabel,
	metricsLevel,
	metricsText,
	data,
}: FullscreenMetricsCardOptions) => {
	const metricsLevelIndicationColor = MetricsLevelConfig[metricsLevel].color;
	return (
		<>
			<article>
				<p>
					<Word text={metricsText} type="large" />
				</p>
				<p>
					<Word
						text={metricsLevel}
						type="medium"
						style={{
							fontFamily: "Futura",
							background: metricsLevelIndicationColor,
							borderRadius: "4px",
							color: "white",
							width: "98px",
							height: "30px",
							display: "inline-flex",
							justifyContent: "center",
							alignItems: "center",
						}}
					/>
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
					strokeColor={metricsLevelIndicationColor}
					strokeWidth={3}
					areaGradientColor={"#f1db42"}
					curveType={"monotone"}
				/>
			</article>
		</>
	);
};
export default FullscreenMetricsCard;
