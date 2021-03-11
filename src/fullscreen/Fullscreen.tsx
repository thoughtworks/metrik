import React from "react";
import AreaChart from "../shared/components/AreaChart/AreaChart";
export interface ChartData {
	name: string;
	uv: number;
	pv: number;
}
const FullScreen = () => {
	const data: ChartData[] = [
		{ name: "Page A", uv: 400, pv: 2400 },
		{
			name: "Page A",
			uv: 400,
			pv: 2400,
		},
		{ name: "Page A", uv: 100, pv: 1000 },
		{ name: "Page B", uv: 200, pv: 2000 },
		{ name: "Page C", uv: 300, pv: 3000 },
	];
	return (
		<AreaChart
			data={data}
			dataKey={"pv"}
			width={730}
			height={250}
			strokeColor={"#82ca9d"}
			strokeWidth={3}
			areaGradientColor={"#f1db42"}
			curveType={"monotone"}
		/>
	);
};
export default FullScreen;
