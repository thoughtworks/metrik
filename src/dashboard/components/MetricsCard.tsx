import React, { FC } from "react";
import { css } from "@emotion/react";
import { LineChart } from "../../components/LineChart";
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

interface MetricsLevelConfig {
	color: string;
	indicator1X: string;
	indicator2X: string;
	indicator3X: string;
}

interface MetricsLevelInterface {
	[level: string]: MetricsLevelConfig;
}

const MetricsLevel: MetricsLevelInterface = {
	ELITE: {
		color: "#49AA19",
		indicator1X: EliteIndicator1X,
		indicator2X: EliteIndicator2X,
		indicator3X: EliteIndicator3X,
	},
	HIGH: {
		color: "#1890FF",
		indicator1X: HighIndicator1X,
		indicator2X: HighIndicator2X,
		indicator3X: HighIndicator3X,
	},
	MEDIUM: {
		color: "#FAAD14",
		indicator1X: MediumIndicator1X,
		indicator2X: MediumIndicator2X,
		indicator3X: MediumIndicator3X,
	},
	LOW: {
		color: "#A61D24",
		indicator1X: LowIndicator1X,
		indicator2X: LowIndicator2X,
		indicator3X: LowIndicator3X,
	},
	NA: {
		color: "#BFBFBF",
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
const metricsValueStyles = (level: string) =>
	css({
		color: MetricsLevel[level].color,
		fontSize: "40px",
		lineHeight: "47px",
	});
const metricsUnitStyles = css({
	color: "rgba(0, 0, 0, 0.5)",
	fontSize: "12px",
	lineHeight: "20px",
});

interface Summary {
	level: string;
	average: string;
}

interface MetricsCardProps {
	title: string;
	summary: Summary;
	data: any[];
	yaxisFormatter: (value: string) => string;
	unit: string;
}

export const MetricsCard: FC<MetricsCardProps> = ({
	title,
	summary,
	data,
	yaxisFormatter,
	unit,
}) => {
	return (
		<div css={containerStyles}>
			<div css={titleStyles}>{title}</div>
			<div css={subtitleStyles}>
				<img
					css={metricsIndicatorStyles}
					srcSet={`
						${MetricsLevel[summary.level].indicator1X} 1x, 
						${MetricsLevel[summary.level].indicator2X} 2x,
						${MetricsLevel[summary.level].indicator3X} 3x
					`}
				/>
				<div css={metricsValueStyles(summary.level)}>{summary.average}</div>
				<div css={metricsUnitStyles}>
					<div>AVG.</div>
					<div>{unit}</div>
				</div>
			</div>
			<LineChart data={data} yaxisFormatter={yaxisFormatter} unit={unit} />
		</div>
	);
};
