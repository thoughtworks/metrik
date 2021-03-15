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
	height: "42.5vh",
	backgroundColor: GRAY_11,
	color: "white",
	position: "relative" as const,
};
const dataDisplayStyle = {
	padding: "0.48rem",
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
				<section css={dataDisplayStyle}>
					<p>
						<Word text={metricsText} type="xxLarge" />
					</p>
					<p css={{ margin: "0.3rem 0" }}>
						<Word
							text={metricsLevel}
							type="xLarge"
							style={{
								fontFamily: "Futura",
								backgroundColor: metricsLevelIndicationColor,
								borderRadius: "4px",
								color: "white",
								width: "2rem",
								height: "0.56rem",
								display: "inline-flex",
								justifyContent: "center",
								alignItems: "center",
							}}
						/>
					</p>
					<p css={{ marginBottom: "0.1rem" }}>
						<Word text={metricsData} type="jumbo" />
					</p>
					<p css={{ marginBottom: "0" }}>
						<Word text={metricsDataLabel} type="medium" />
					</p>
				</section>
				<AreaChart
					css={{ position: "absolute" as const, bottom: 0 }}
					data={data}
					dataKey={"pv"}
					width={"100%"}
					height={"17%"}
					strokeColor={metricsLevelIndicationColor}
					strokeWidth={5}
					areaGradientColor={"#5A5A5A"}
					curveType={"monotone"}
				/>
			</article>
		</>
	);
};
export default FullscreenMetricsCard;
