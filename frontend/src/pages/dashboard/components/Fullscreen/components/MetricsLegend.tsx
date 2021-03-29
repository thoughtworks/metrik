import React from "react";
import { Colour, LegendRect } from "../../../../../shared/components/LegendRect";
import { MetricsLevel } from "../../../../../shared/__types__/enum";
import Word from "../../../../../shared/components/Word/Word";
import { GRAY_1 } from "../../../../../shared/constants/styles";

interface Legend {
	text: MetricsLevel;
	color: Colour;
}

const MetricsLegend = () => {
	const legendList: Legend[] = [
		{
			color: Colour.green,
			text: MetricsLevel.ELITE,
		},
		{
			color: Colour.blue,
			text: MetricsLevel.HIGH,
		},
		{
			color: Colour.orange,
			text: MetricsLevel.MEDIUM,
		},
		{
			color: Colour.red,
			text: MetricsLevel.LOW,
		},
	];
	const legendRectStyle = {
		display: "block",
		marginBottom: "0.4vh",
	};
	return (
		<div>
			<p css={{ margin: "0.2rem 0 0 0", color: GRAY_1, opacity: 0.5 }}>
				<Word type="large" text={"Metrics Legend"} />
			</p>
			{legendList.map(({ color, text }, index) => (
				<LegendRect
					color={color}
					text={text}
					key={index}
					rectangleWidth={"0.3rem"}
					rectangleHeight={"0.3rem"}
					wordType={"small"}
					css={legendRectStyle}
				/>
			))}
		</div>
	);
};
export default MetricsLegend;
