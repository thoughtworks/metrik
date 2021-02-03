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

interface LineChartProps {
	data: any[];
	yaxisFormatter: (value: string) => string;
	unit: string;
}

export const LineChart: FC<LineChartProps> = ({ data, yaxisFormatter, unit }) => (
	<ResponsiveContainer width="100%" height={300}>
		<RechartsLineChart data={data}>
			<CartesianGrid stroke="#416180" strokeWidth={0.5} strokeOpacity={0.151934} vertical={false} />
			<XAxis dataKey="name" stroke="#416180" strokeWidth={0.5} strokeOpacity={0.45} />
			<YAxis
				tickFormatter={yaxisFormatter}
				axisLine={false}
				label={{ value: unit, angle: -90, position: "insideLeft" }}
			/>
			<Tooltip />
			<Line
				type="monotone"
				dataKey="value"
				stroke="#8C8C8C"
				strokeWidth={2}
				fill="#000000"
				// eslint-disable-next-line @typescript-eslint/ban-ts-comment
				// @ts-ignore
				label={{ position: "top" }}
			/>
		</RechartsLineChart>
	</ResponsiveContainer>
);
