import React, { FC, ReactNode } from "react";
import { css } from "@emotion/react";
import { CustomizeTickProps, LineChart } from "../../../components/LineChart";

import { GRAY_1, GRAY_4 } from "../../../constants/styles";
import { durationFormatter } from "../../../utils/timeFormats/timeFormats";
import { LoadingSpinner } from "../../../components/LoadingSpinner";
import { find } from "lodash";
import { AxisDomain } from "recharts/types/util/types";
import Word from "../../../components/Word/Word";
import { MetricsLevelConfig } from "./Fullscreen/components/FullscreenMetricsCard";
import { Metrics, MetricsLevel, MetricsSummary } from "../../../models/metrics";

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
	const { startTime, endTime } = durationFormatter(payload.value, currentTickItem.endTimestamp);

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
	summary: MetricsSummary;
	data: Metrics[];
	yaxisFormatter: (value: string) => string;
	yAxisLabel: string;
	loading: boolean;
	subTitleUnit: string;
	info: ReactNode;
	yAxisDomain?: AxisDomain;
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
	yAxisDomain,
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
						<Word
							text={summary.level === MetricsLevel.INVALID ? "NA" : summary.level}
							type="small"
							style={{
								fontFamily: "Oswald-Regular",
								backgroundColor: MetricsLevelConfig[summary.level].color,
								borderRadius: "4px",
								color: "white",
								width: 45,
								height: 45,
								fontSize: 15,
								display: "inline-flex",
								justifyContent: "center",
								alignItems: "center",
							}}
						/>

						<div css={metricsValueStyles(summary.level as MetricsLevel)}>
							{summary.level === MetricsLevel.INVALID
								? "--"
								: yaxisFormatter((summary.value || "").toString())}
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
						yAxisDomain={yAxisDomain}
					/>
				</>
			)}
		</div>
	);
};
