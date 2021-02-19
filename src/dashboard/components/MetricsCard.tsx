import React, { FC, ReactNode } from "react";
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
import InvalidIndicator1X from "../../assets/metricsLevelIndicators/StatusIndicator_Invalid.png";
import InvalidIndicator2X from "../../assets/metricsLevelIndicators/StatusIndicator_Invalid@2x.png";
import InvalidIndicator3X from "../../assets/metricsLevelIndicators/StatusIndicator_Invalid@3x.png";
import {
	BLUE_5,
	GRAY_1,
	GRAY_4,
	GRAY_6,
	GREEN_DARK,
	ORANGE_DARK,
	RED_DARK,
} from "../../constants/styles";
import { formatTickTime } from "../../utils/timeFormats";
import { LoadingSpinner } from "../../components/LoadingSpinner";
import { Metrics, MetricsLevel } from "../../clients/apis";
import { find } from "lodash";

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
	[MetricsLevel.INVALID]: {
		color: GRAY_6,
		indicator1X: InvalidIndicator1X,
		indicator2X: InvalidIndicator2X,
		indicator3X: InvalidIndicator3X,
	},
};

const containerStyles = css({
	backgroundColor: GRAY_1,
	border: `1px solid ${GRAY_4}`,
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

const CustomizeTick: FC<CustomizeTickProps> = ({ x, y, textAnchor, data, payload, index = 0 }) => {
	const currentTickItem = find(data, item => item.startTimestamp === payload.value);
	if (currentTickItem === undefined) {
		return <></>;
	}
	const { startTime, endTime } = formatTickTime(payload.value, currentTickItem.endTimestamp);

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
				{startTime}
			</tspan>
			<tspan x={x} dy="1.25em">
				- {endTime}
			</tspan>
		</text>
	);
};

interface MetricsCardProps {
	title: string;
	summary: Metrics;
	data: Metrics[];
	yaxisFormatter: (value: string) => string;
	yAxisLabel: string;
	loading: boolean;
	subTitleUnit: string;
	info: ReactNode;
}

export const MetricsCard: FC<MetricsCardProps> = ({
	title,
	summary,
	data,
	yaxisFormatter,
	yAxisLabel,
	loading,
	subTitleUnit,
	info,
}) => {
	return (
		<div css={containerStyles}>
			{loading ? (
				<div css={spinContainerStyles}>
					<LoadingSpinner />
				</div>
			) : (
				<>
					<div css={titleStyles}>
						<span>{title}</span>
						{info}
					</div>
					<div css={subtitleStyles}>
						<img
							alt={title}
							css={metricsIndicatorStyles}
							srcSet={`
						${MetricsLevelConfig[summary.level as MetricsLevel].indicator1X} 1x, 
						${MetricsLevelConfig[summary.level as MetricsLevel].indicator2X} 2x,
						${MetricsLevelConfig[summary.level as MetricsLevel].indicator3X} 3x
					`}
						/>
						<div css={metricsValueStyles(summary.level as MetricsLevel)}>
							{summary.level === MetricsLevel.INVALID
								? "--"
								: yaxisFormatter(summary.value.toString())}
						</div>
						<div css={metricsUnitStyles}>
							<div>AVG.</div>
							<div>{subTitleUnit}</div>
						</div>
					</div>
					<LineChart
						data={data}
						yaxisFormatter={yaxisFormatter}
						unit={yAxisLabel}
						CustomizeTick={CustomizeTick}
					/>
				</>
			)}
		</div>
	);
};
