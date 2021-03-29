import Word from "../../../../../components/Word/Word";
import React, { useState } from "react";
import { BG_GRAY_DARK, BG_GRAY_LIGHT } from "../../../../../constants/styles";
interface PipelineListProps {
	pipelineList: string[];
}
const restPipelineListStyle = {
	width: "100%",
	maxHeight: "20vh",
	overflow: "scroll",
	padding: "10px 10px 6px 10px",
	backgroundColor: BG_GRAY_LIGHT,
	borderRadius: "20px",
	position: "absolute" as const,
	zIndex: 10,
	overflowX: "hidden" as const,
};

const pipelineStyle = {
	padding: "2px 19px",
	borderRadius: "19px",
	backgroundColor: BG_GRAY_DARK,
	display: "inline-block",
	marginTop: "2px",
	marginRight: "10px",
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
			<div>
				{defaultShownList.map((pipeline, index) => (
					<Word css={pipelineStyle} text={pipeline} type={"mediumSuperLight"} key={index} />
				))}
			</div>
			<div>
				<Word
					css={{
						...pipelineStyle,
						...showMoreStyle,
						display: pipelineList.length > DEFAULT_SHOWN_NUMBER ? "inline-block" : "none",
					}}
					text={isRestListShown ? "Show Less" : "Show More"}
					type={"mediumSuperLight"}
					onClick={showRestList}
				/>
			</div>
			<div
				css={{
					display: isRestListShown ? "block" : "none",
					...restPipelineListStyle,
				}}>
				{defaultHiddenList.map((pipeline, index) => (
					<Word css={pipelineStyle} text={pipeline} type={"mediumSuperLight"} key={index} />
				))}
			</div>
		</section>
	);
};
export default PipelineList;
