import React from "react";
interface WordProps extends React.HTMLAttributes<HTMLSpanElement> {
	text: string | number;
	type: "jumbo" | "xxxLarge" | "xxLarge" | "xLarge" | "large" | "medium" | "small";
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
	xxLarge: {
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
	medium: {
		fontSize: "0.24rem",
		fontWeight: 400,
		lineHeight: "0.22rem",
	},
	small: {
		fontSize: "0.14rem",
		fontWeight: 400,
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
