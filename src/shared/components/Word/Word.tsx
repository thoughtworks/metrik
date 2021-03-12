import React from "react";
interface WordProps extends React.HTMLAttributes<HTMLSpanElement> {
	text: string | number;
	type: "jumbo" | "large" | "medium" | "small";
}

const styleMap = {
	jumbo: {
		fontSize: "48px",
		fontWeight: 700,
	},
	large: {
		fontSize: "24px",
		fontWeight: 400,
	},
	medium: {
		fontSize: "16px",
		fontWeight: 400,
	},
	small: {
		fontSize: "12px",
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
