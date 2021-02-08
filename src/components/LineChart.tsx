import React, { FC, useState } from "react";
import {
	Brush,
	CartesianGrid,
	Line,
	LineChart as RechartsLineChart,
	ResponsiveContainer,
	XAxis,
	YAxis,
} from "recharts";
import { Metrics } from "../clients/apis";
import { GRAY_7, GRAY_8 } from "../constants/styles";

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

export const LineChart: FC<LineChartProps> = ({ data, yaxisFormatter, unit, CustomizeTick }) => {
	const [initDomain, setInitDomain] = useState(false);
	const [yAxisMax, setYAxisMax] = useState(0);

	return (
		<ResponsiveContainer width="100%" height={300}>
			<RechartsLineChart
				css={{
					".recharts-brush-texts": {
						fontSize: 0,
					},
					".recharts-brush > rect": {
						stroke: "none",
						strokeLinecap: "round",
					},
				}}
				data={data}
				margin={{
					top: 5,
					right: 30,
					left: 20,
					bottom: 20,
				}}>
				<CartesianGrid stroke="#416180" strokeWidth={0.5} strokeOpacity={0.2} vertical={false} />
				<XAxis
					dataKey="startTimestamp"
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
					domain={
						!initDomain
							? [
									0,
									(dataMax: number) => {
										setInitDomain(true);
										setYAxisMax(Math.ceil((dataMax * domainMaximizeRatio) / 10) * 10);
										return dataMax === 0 ? 1 : Math.ceil((dataMax * domainMaximizeRatio) / 10) * 10;
									},
							  ]
							: [0, yAxisMax]
					}
				/>
				<Line
					type="monotone"
					dataKey="value"
					stroke={GRAY_7}
					strokeWidth={2}
					fill={GRAY_7}
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

				{data.length > 6 && (
					<Brush
						height={8}
						stroke={GRAY_8}
						startIndex={Math.ceil(data.length / 2)}
						endIndex={data.length - 1}
						travellerWidth={0}
						dataKey="startTimestamp"
					/>
				)}
			</RechartsLineChart>
		</ResponsiveContainer>
	);
};
