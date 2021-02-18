import React, { FC } from "react";
import { GREEN_LIGHT, BLUE_5, ORANGE_DARK, RED_DARK } from "../constants/styles";

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

export const LegendRect: FC<{ color: keyof typeof Colour }> = ({ color }) => (
	<div css={{ width: 24, height: 12, backgroundColor: colors[color] }} />
);
