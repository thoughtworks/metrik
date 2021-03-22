import Word from "../../shared/components/Word/Word";
import React, { useState } from "react";
import { BG_GRAY_DARK, BG_GRAY_LIGHT, GRAY_8 } from "../../shared/constants/styles";
interface PipelineListProps {
	pipelineList: string[];
}
const pipelineListStyle = {
	flexWrap: "wrap" as const,
	justifyContent: "space-between",
};
const restPipelineListStyle = {
	width: "100%",
	maxHeight: "20vh",
	overflow: "scroll",
	padding: "0 10px 10px 10px",
	backgroundColor: BG_GRAY_LIGHT,
	borderRadius: "20px",
	alignContent: "flex-start",
	position: "absolute" as const,
	zIndex: 10,
	marginTop: "10px",
};

const pipelineStyle = {
	padding: "10px 19px",
	borderRadius: "19px",
	backgroundColor: BG_GRAY_DARK,
	display: "inline-block",
	marginTop: "10px",
	whiteSpace: "nowrap" as const,
	overflow: "hidden",
	textOverflow: "ellipsis",
	maxWidth: "100%",
};
const showMoreStyle = {
	width: "1.8rem",
	textAlign: "center" as const,
	backgroundColor: BG_GRAY_LIGHT,
};
const PipelineList = ({ pipelineList }: PipelineListProps) => {
	const DEFAULT_SHOWN_NUMBER = 3;
	const defaultShownList = pipelineList.slice(0, DEFAULT_SHOWN_NUMBER);
	const defaultHiddenList = pipelineList.slice(DEFAULT_SHOWN_NUMBER, pipelineList.length);
	const [isRestListShown, setIsRestLitShown] = useState(false);
	const showRestList = () => setIsRestLitShown(!isRestListShown);
	return (
		<section css={{ position: "relative" }}>
			<div
				css={{
					display: "flex",
					...pipelineListStyle,
				}}>
				{defaultShownList.map((pipeline, index) => (
					<Word css={pipelineStyle} text={pipeline} type={"medium"} key={index} />
				))}
				<Word
					css={{
						...pipelineStyle,
						...showMoreStyle,
						display: pipelineList.length > DEFAULT_SHOWN_NUMBER ? "inline-block" : "none",
					}}
					text={isRestListShown ? "Show Less" : "Show More"}
					type={"medium"}
					onClick={showRestList}
				/>
			</div>
			<div
				css={{
					display: isRestListShown ? "flex" : "none",
					...pipelineListStyle,
					...restPipelineListStyle,
				}}>
				{defaultHiddenList.map((pipeline, index) => (
					<Word css={pipelineStyle} text={pipeline} type={"medium"} key={index} />
				))}
			</div>
		</section>
	);
};
export default PipelineList;
