import React from "react";
import { Area, AreaChart as ReChartAreaChart, ResponsiveContainer, Tooltip } from "recharts";
import { CurveType } from "recharts/types/shape/Curve";
import { AREA_GRADIENT_DEFAULT_COLOR } from "../../constants/styles";

interface AreaChartProps<T, K> extends React.HTMLAttributes<HTMLDivElement> {
	data: T[];
	dataKey: Exclude<K, symbol>;
	width: number | string;
	height?: number | string;
	strokeColor?: string;
	strokeWidth?: number;
	areaGradientColor?: string;
	curveType?: CurveType;
}
const AreaChart = <T, K extends keyof T>({
	data,
	dataKey,
	width = 700,
	height = 350,
	strokeColor = "#000000",
	strokeWidth = 1,
	areaGradientColor = AREA_GRADIENT_DEFAULT_COLOR,
	curveType = "monotone",
	...restProps
}: AreaChartProps<T, K>) => {
	return (
		<>
			<ResponsiveContainer width={width} height={height} {...restProps}>
				<ReChartAreaChart data={data}>
					<defs>
						<linearGradient id="areaColor" x1="0" y1="0%" x2="0" y2="100%">
							<stop offset="0%" stopColor={areaGradientColor} stopOpacity={1} />
							<stop offset="100%" stopColor={"RGBA(90, 90, 90, 1)"} stopOpacity={0} />
						</linearGradient>
					</defs>
					<Area
						connectNulls
						type={curveType}
						dataKey={dataKey}
						stroke={strokeColor}
						strokeWidth={strokeWidth}
						fill="url(#areaColor)"
					/>
				</ReChartAreaChart>
			</ResponsiveContainer>
		</>
	);
};
export default AreaChart;
