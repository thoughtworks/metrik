import React, { FC } from "react";
import { css } from "@emotion/react";
import { LineChart } from "../components/LineChart";

enum MetricsLevelColor {
	LOW = "#A61D24",
	ELITE = "#49AA19",
	HIGH = "#1890FF",
	MEDIUM = "#FAAD14",
	INVALID = "#BFBFBF",
}

type MetricsLevelKey = keyof typeof MetricsLevelColor;

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
const metricsLevelStyles = (level: MetricsLevelKey) =>
	css({
		color: "#FFFFFF",
		fontSize: "15px",
		lineHeight: "45px",
		fontFamily: "Futura",
		fontWeight: 500,
		width: "45px",
		textAlign: "center",
		backgroundColor: MetricsLevelColor[level],
		borderRadius: "4px",
	});
const metricsValueStyles = (level: MetricsLevelKey) =>
	css({
		color: MetricsLevelColor[level],
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
	average: number;
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
				<div css={metricsLevelStyles(summary.level as MetricsLevelKey)}>{summary.level}</div>
				<div css={metricsValueStyles(summary.level as MetricsLevelKey)}>{summary.average}</div>
				<div css={metricsUnitStyles}>
					<div>AVG.</div>
					<div>{unit}</div>
				</div>
			</div>
			<LineChart data={data} yaxisFormatter={yaxisFormatter} unit={unit} />
		</div>
	);
};
