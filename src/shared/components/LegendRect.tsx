import React, { FC } from "react";
import { GREEN_LIGHT, BLUE_5, ORANGE_DARK, RED_DARK, GRAY_1 } from "../constants/styles";
import { Typography } from "antd";

const { Text } = Typography;

enum Colour {
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
	color: GRAY_1,
	opacity: 0.75,
	marginLeft: 8,
	fontSize: 12,
	verticalAlign: "middle",
};

export const LegendRect: FC<{ color: keyof typeof Colour; text?: string }> = ({ color, text }) => (
	<>
		<span
			css={{
				display: "inline-block",
				width: 24,
				height: 12,
				backgroundColor: colors[color],
				verticalAlign: "middle",
			}}
		/>
		{text && <Text style={levelTextStyle}>{text}</Text>}
	</>
);
