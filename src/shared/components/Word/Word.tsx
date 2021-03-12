import React from "react";
interface WordProps extends React.HTMLAttributes<HTMLSpanElement> {
	text: string | number;
	type: "large" | "medium" | "small";
}

const styleMap = {
	large: {
		fontSize: "24px",
	},
	medium: {
		fontSize: "16px",
	},
	small: {
		fontSize: "12px",
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
