import React from "react";
export type WordType =
	| "jumbo"
	| "xxxLarge"
	| "xxxLargeRegular"
	| "xxLargeBold"
	| "xxLargeLight"
	| "xLarge"
	| "large"
	| "largeSuperLight"
	| "medium"
	| "mediumSuperLight"
	| "small"
	| "xSmall";
interface WordProps extends React.HTMLAttributes<HTMLSpanElement> {
	text: string | number | undefined;
	type: WordType;
}

const styleMap = {
	jumbo: {
		fontSize: "0.8rem",
		fontWeight: 700,
		lineHeight: "0.8rem",
	},
	xxxLarge: {
		fontSize: "0.64rem",
		fontWeight: 700,
	},
	xxxLargeRegular: {
		fontSize: "0.64rem",
		fontWeight: 400,
	},
	xxLargeBold: {
		fontSize: "0.48rem",
		fontWeight: 700,
		lineHeight: "0.48rem",
	},
	xxLargeLight: {
		fontSize: "0.48rem",
		fontWeight: 300,
		lineHeight: "0.48rem",
	},
	xLarge: {
		fontSize: "0.4rem",
		fontWeight: 400,
		lineHeight: "0.22rem",
	},
	large: {
		fontSize: "0.32rem",
		fontWeight: 400,
	},
	largeSuperLight: {
		fontSize: "0.32rem",
		fontWeight: 200,
	},
	medium: {
		fontSize: "0.24rem",
		fontWeight: 400,
		lineHeight: "0.24rem",
	},
	mediumSuperLight: {
		fontSize: "0.24rem",
		fontWeight: 200,
	},
	small: {
		fontSize: "0.14rem",
		fontWeight: 400,
	},
	xSmall: {
		fontSize: "0.12rem",
		fontWeight: 700,
	},
};

const Word = ({ text, type, ...restProps }: WordProps) => {
	return (
		<span css={styleMap[type]} {...restProps}>
			{text}
		</span>
	);
};
export default Word;
