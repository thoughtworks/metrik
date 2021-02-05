import React, { FC } from "react";
import { css } from "@emotion/react";
import { CustomizeTickProps, LineChart } from "../../components/LineChart";
import EliteIndicator1X from "../../assets/metricsLevelIndicators/StatusIndicator_Elite.png";
import EliteIndicator2X from "../../assets/metricsLevelIndicators/StatusIndicator_Elite@2x.png";
import EliteIndicator3X from "../../assets/metricsLevelIndicators/StatusIndicator_Elite@3x.png";
import HighIndicator1X from "../../assets/metricsLevelIndicators/StatusIndicator_High.png";
import HighIndicator2X from "../../assets/metricsLevelIndicators/StatusIndicator_High@2x.png";
import HighIndicator3X from "../../assets/metricsLevelIndicators/StatusIndicator_High@3x.png";
import LowIndicator1X from "../../assets/metricsLevelIndicators/StatusIndicator_Low.png";
import LowIndicator2X from "../../assets/metricsLevelIndicators/StatusIndicator_Low@2x.png";
import LowIndicator3X from "../../assets/metricsLevelIndicators/StatusIndicator_Low@3x.png";
import MediumIndicator1X from "../../assets/metricsLevelIndicators/StatusIndicator_Medium.png";
import MediumIndicator2X from "../../assets/metricsLevelIndicators/StatusIndicator_Medium@2x.png";
import MediumIndicator3X from "../../assets/metricsLevelIndicators/StatusIndicator_Medium@3x.png";
import NAIndicator1X from "../../assets/metricsLevelIndicators/StatusIndicator_NA.png";
import NAIndicator2X from "../../assets/metricsLevelIndicators/StatusIndicator_NA@2x.png";
import NAIndicator3X from "../../assets/metricsLevelIndicators/StatusIndicator_NA@3x.png";
import { BLUE_5, GRAY_6, GREEN_DARK, ORANGE_DARK, RED_DARK } from "../../constants/styles";
import { MetricsDataItem } from "../PageDashboard";
import { formatTickTime } from "../../utils/timeFormats";
import { LoadingSpinner } from "../../components/LoadingSpinner";

export enum MetricsLevel {
	ELITE,
	HIGH,
	MEDIUM,
	LOW,
	NA,
}

interface MetricsLevelConfigInterface {
	color: string;
	indicator1X: string;
	indicator2X: string;
	indicator3X: string;
}

type MetricsLevelInterface = {
	[key in MetricsLevel]: MetricsLevelConfigInterface;
};

const MetricsLevelConfig: MetricsLevelInterface = {
	[MetricsLevel.ELITE]: {
		color: GREEN_DARK,
		indicator1X: EliteIndicator1X,
		indicator2X: EliteIndicator2X,
		indicator3X: EliteIndicator3X,
	},
	[MetricsLevel.HIGH]: {
		color: BLUE_5,
		indicator1X: HighIndicator1X,
		indicator2X: HighIndicator2X,
		indicator3X: HighIndicator3X,
	},
	[MetricsLevel.MEDIUM]: {
		color: ORANGE_DARK,
		indicator1X: MediumIndicator1X,
		indicator2X: MediumIndicator2X,
		indicator3X: MediumIndicator3X,
	},
	[MetricsLevel.LOW]: {
		color: RED_DARK,
		indicator1X: LowIndicator1X,
		indicator2X: LowIndicator2X,
		indicator3X: LowIndicator3X,
	},
	[MetricsLevel.NA]: {
		color: GRAY_6,
		indicator1X: NAIndicator1X,
		indicator2X: NAIndicator2X,
		indicator3X: NAIndicator3X,
	},
};

const containerStyles = css({
	backgroundColor: "#FFFFFF",
	border: "1px solid #F0F0F0",
	padding: "32px 24px",
	marginBottom: "24px",
	height: "486px",
});
const titleStyles = css({
	marginBottom: "17px",
});
const subtitleStyles = css({
	display: "flex",
	alignItems: "center",
	marginBottom: "38px",
	"> *": {
		marginRight: "8px",
	},
});
const metricsIndicatorStyles = css({
	height: "45px",
	width: "45px",
});
const metricsValueStyles = (level: MetricsLevel) =>
	css({
		color: MetricsLevelConfig[level].color,
		fontSize: "40px",
		lineHeight: "47px",
	});
const metricsUnitStyles = css({
	color: "rgba(0, 0, 0, 0.5)",
	fontSize: "12px",
	lineHeight: "20px",
});
const spinContainerStyles = css({
	width: "100%",
	height: "100%",
	display: "flex",
	justifyContent: "center",
	alignItems: "center",
});

const CustomizeTick: FC<CustomizeTickProps> = ({ x, y, textAnchor, data, index = 0 }) => {
	return (
		<text
			x={x}
			y={y}
			dy={16}
			fill="#2C3542"
			fillOpacity={0.75}
			fontSize={12}
			textAnchor={textAnchor}>
			<tspan x={x} dy="1.5em">
				{formatTickTime(data[index]?.startTimestamp)}
			</tspan>
			<tspan x={x} dy="1.25em">
				- {formatTickTime(data[index]?.endTimestamp)}
			</tspan>
		</text>
	);
};

interface MetricsCardProps {
	title: string;
	summary: MetricsDataItem;
	data: MetricsDataItem[];
	yaxisFormatter: (value: string) => string;
	unit: string;
	loading: boolean;
}

export const MetricsCard: FC<MetricsCardProps> = ({
	title,
	summary,
	data,
	yaxisFormatter,
	unit,
	loading,
}) => {
	return (
		<div css={containerStyles}>
			{loading ? (
				<div css={spinContainerStyles}>
					<LoadingSpinner />
				</div>
			) : (
				<>
					<div css={titleStyles}>{title}</div>
					<div css={subtitleStyles}>
						<img
							alt={title}
							css={metricsIndicatorStyles}
							srcSet={`
						${MetricsLevelConfig[summary.level].indicator1X} 1x, 
						${MetricsLevelConfig[summary.level].indicator2X} 2x,
						${MetricsLevelConfig[summary.level].indicator3X} 3x
					`}
						/>
						<div css={metricsValueStyles(summary.level)}>
							{yaxisFormatter(summary.value.toString())}
						</div>
						<div css={metricsUnitStyles}>
							<div>AVG.</div>
							<div>{unit}</div>
						</div>
					</div>
					<LineChart
						data={data}
						yaxisFormatter={yaxisFormatter}
						unit={unit}
						CustomizeTick={CustomizeTick}
					/>
				</>
			)}
		</div>
	);
};
