import React, { FC, useEffect, useRef, useState } from "react";
import {
	CartesianGrid,
	Line,
	LineChart as RechartsLineChart,
	ResponsiveContainer,
	XAxis,
	YAxis,
} from "recharts";
import { Metrics } from "../clients/apis";
import { GRAY_1, GRAY_7 } from "../constants/styles";
import { css } from "@emotion/react";
import { map, throttle } from "lodash";

export interface CustomizeTickProps {
	x?: number;
	y?: number;
	textAnchor?: string;
	index?: number;
	data: Metrics[];
	payload?: any;
}

interface LineChartProps {
	data: Metrics[];
	yaxisFormatter: (value: string) => string;
	unit: string;
	CustomizeTick: FC<CustomizeTickProps>;
}

const domainMaximizeRatio = 1.1;
const lineUnit = 100;
const yAxisWidth = 80;
const fullDisplayAmount = 9;
const yAxisStyles = css({
	width: 80,
	height: 300,
	position: "absolute",
	backgroundColor: "#ffffff",
	zIndex: 1000,
});
const heightStyles = css({
	height: 300,
});

export const LineChart: FC<LineChartProps> = ({ data, yaxisFormatter, unit, CustomizeTick }) => {
	const dataMax = Math.max(...map(data, item => item.value).filter(item => !isNaN(item)));
	const yMaxValue = dataMax === 0 ? 1 : Math.ceil((dataMax * domainMaximizeRatio) / 10) * 10;
	const ref = useRef<HTMLDivElement>(null);
	const [chartWidth, setChartWidth] = useState<string | number>("100%");
	const scrollWidth = data.length ? lineUnit * (data.length - 1) + yAxisWidth : 0;
	const realChartWidth = document
		?.getElementsByClassName("real-chart")
		?.item(0)
		?.getAttribute("width");

	useEffect(() => {
		if (ref.current) {
			setChartWidth(data.length > fullDisplayAmount ? ref.current?.offsetWidth : "100%");
			window.addEventListener(
				"resize",
				throttle(() => setChartWidth(ref.current?.offsetWidth || "100%"), 500)
			);
		}
	}, [ref.current, chartWidth]);

	return (
		<div ref={ref}>
			<div css={yAxisStyles}>
				<ResponsiveContainer width="100%" height="80%">
					<RechartsLineChart
						margin={{
							top: 5,
							right: 30,
							left: 20,
							bottom: 20,
						}}>
						<YAxis
							tickFormatter={yaxisFormatter}
							axisLine={false}
							label={{ value: unit, angle: -90, position: "insideLeft" }}
							tickLine={false}
							domain={[0, yMaxValue]}
						/>
					</RechartsLineChart>
				</ResponsiveContainer>
			</div>

			<div
				css={css`
					width: ${chartWidth};
					position: relative;
					overflow-x: auto;
				`}>
				<div css={heightStyles}>
					<ResponsiveContainer
						width={scrollWidth > chartWidth ? scrollWidth : chartWidth}
						className="real-chart">
						<RechartsLineChart
							data={data}
							margin={{
								top: 5,
								right: 30,
								left: 20,
								bottom: 20,
							}}>
							<CartesianGrid
								stroke="#416180"
								strokeWidth={0.5}
								strokeOpacity={0.2}
								vertical={false}
							/>
							<XAxis
								dataKey="startTimestamp"
								interval={
									realChartWidth === null ||
									realChartWidth === undefined ||
									Number(realChartWidth) <= 699
										? "preserveEnd"
										: 0
								}
								stroke="#416180"
								strokeWidth={0.5}
								strokeOpacity={0.45}
								height={50}
								padding={{ left: 20, right: 20 }}
								tick={<CustomizeTick data={data} />}
							/>
							<YAxis
								tickFormatter={yaxisFormatter}
								axisLine={false}
								label={{ value: unit, angle: -90, position: "insideLeft" }}
								tickLine={false}
								domain={[0, yMaxValue]}
							/>
							<Line
								type="monotone"
								dataKey="value"
								stroke={GRAY_7}
								strokeWidth={2}
								fill={GRAY_1}
								isAnimationActive={false}
								// eslint-disable-next-line @typescript-eslint/ban-ts-comment
								// @ts-ignore
								label={{
									position: "top",
									formatter: yaxisFormatter,
									fontSize: 12,
									style: { transform: "translateY(-5px)" },
								}}
							/>
						</RechartsLineChart>
					</ResponsiveContainer>
				</div>
			</div>
		</div>
	);
};
