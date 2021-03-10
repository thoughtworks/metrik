import React from "react";
import { Area, AreaChart as ReChartAreaChart, Tooltip } from "recharts";
import { ChartData } from "../../../fullscreen/Fullscreen";

interface AreaChartProps<T> {
	data: T[];
}
const AreaChart = ({ data }: AreaChartProps<ChartData>) => {
	return (
		<>
			<ReChartAreaChart
				width={730}
				height={250}
				data={data}
				margin={{ top: 10, right: 30, left: 0, bottom: 0 }}>
				<defs>
					<linearGradient id="colorPv" x1="0" y1="0" x2="0" y2="1">
						<stop offset="5%" stopColor="#f1db42" stopOpacity={0.8} />
						<stop offset="95%" stopColor="#f1db42" stopOpacity={0} />
					</linearGradient>
				</defs>
				<Tooltip />
				<Area type="monotone" dataKey="pv" stroke="#82ca9d" strokeWidth={3} fill="url(#colorPv)" />
			</ReChartAreaChart>
		</>
	);
};
export default AreaChart;
