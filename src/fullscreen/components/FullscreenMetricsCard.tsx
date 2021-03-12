import React from "react";
import { MetricsType, MetricsLevel } from "../../shared/__types__/enum";
import AreaChart from "../../shared/components/AreaChart/AreaChart";
import Word from "../../shared/components/Word/Word";
import {
	BLUE_5,
	GRAY_11,
	GRAY_6,
	GREEN_DARK,
	ORANGE_DARK,
	RED_DARK,
} from "../../shared/constants/styles";

export interface ChartData {
	name: string;
	uv: number;
	pv: number;
}
export interface FullscreenMetricsCardOptions extends React.HTMLAttributes<HTMLDivElement> {
	metricsText: MetricsType;
	metricsLevel: MetricsLevel;
	metricsData: number | string;
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
const cardStyle = {
	width: "49%",
	height: 500,
	backgroundColor: GRAY_11,
	color: "white",
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
			<article css={cardStyle}>
				<p>
					<Word text={metricsText} type="large" />
				</p>
				<p>
					<Word
						text={metricsLevel}
						type="medium"
						style={{
							fontFamily: "Futura",
							backgroundColor: metricsLevelIndicationColor,
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
					<Word text={metricsData} type="jumbo" />
				</p>
				<p>
					<Word text={metricsDataLabel} type="small" />
				</p>
				<AreaChart
					data={data}
					dataKey={"pv"}
					width={"100%"}
					height={"30%"}
					strokeColor={metricsLevelIndicationColor}
					strokeWidth={3}
					areaGradientColor={"#5A5A5A"}
					curveType={"monotone"}
				/>
			</article>
		</>
	);
};
export default FullscreenMetricsCard;
