import React, { FC } from "react";
import {
	CartesianGrid,
	Line,
	LineChart as RechartsLineChart,
	ResponsiveContainer,
	Tooltip,
	XAxis,
	YAxis,
} from "recharts";
import { MetricsDataItem } from "../dashboard/PageDashboard";

export interface CustomizeTickProps {
	x?: number;
	y?: number;
	textAnchor?: string;
	index?: number;
	data: MetricsDataItem[];
}

interface LineChartProps {
	data: MetricsDataItem[];
	yaxisFormatter: (value: string) => string;
	unit: string;
	CustomizeTick: FC<CustomizeTickProps>;
}

const domainMaximizeRatio = 1.1;

export const LineChart: FC<LineChartProps> = ({ data, yaxisFormatter, unit, CustomizeTick }) => (
	<ResponsiveContainer width="100%" height={300}>
		<RechartsLineChart
			data={data}
			margin={{
				top: 5,
				right: 30,
				left: 20,
				bottom: 20,
			}}>
			<CartesianGrid stroke="#416180" strokeWidth={0.5} strokeOpacity={0.2} vertical={false} />
			<XAxis
				stroke="#416180"
				strokeWidth={0.5}
				strokeOpacity={0.45}
				padding={{ left: 20, right: 20 }}
				tick={<CustomizeTick data={data} />}
			/>
			<YAxis
				tickFormatter={yaxisFormatter}
				axisLine={false}
				label={{ value: unit, angle: -90, position: "insideLeft" }}
				tickLine={false}
				domain={[0, (dataMax: number) => Math.ceil((dataMax * domainMaximizeRatio) / 10) * 10]}
			/>
			<Tooltip formatter={yaxisFormatter} />
			<Line
				type="monotone"
				dataKey="value"
				stroke="#8C8C8C"
				strokeWidth={2}
				fill="#8C8C8C"
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
);
