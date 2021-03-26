import React from "react";
import { GREEN_LIGHT, BLUE_5, ORANGE_DARK, RED_DARK } from "../constants/styles";
import Word, { WordType } from "./Word/Word";
export enum Colour {
	green = "green",
	blue = "blue",
	orange = "orange",
	red = "red",
}

const colors = {
	green: GREEN_LIGHT,
	blue: BLUE_5,
	orange: ORANGE_DARK,
	red: RED_DARK,
};

const levelTextStyle = {
	color: "#FFF",
	marginLeft: 8,
	verticalAlign: "middle",
};
interface LegendRectProps extends React.HTMLAttributes<HTMLSpanElement> {
	color: Colour;
	text?: string;
	rectangleWidth?: number | string;
	rectangleHeight?: number | string;
	wordType?: WordType;
}
export const LegendRect = ({
	color,
	text,
	rectangleWidth = 24,
	rectangleHeight = 12,
	wordType = "small",
	...restProps
}: LegendRectProps) => (
	<span {...restProps}>
		<span
			css={{
				display: "inline-block",
				width: rectangleWidth,
				height: rectangleHeight,
				backgroundColor: colors[color],
				verticalAlign: "middle",
			}}
		/>
		{text && <Word css={levelTextStyle} type={wordType} text={text} />}
	</span>
);
