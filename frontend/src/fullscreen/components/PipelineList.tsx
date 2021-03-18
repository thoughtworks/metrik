import Word from "../../shared/components/Word/Word";
import React from "react";
import { GRAY_8 } from "../../shared/constants/styles";
interface PipelineListProps {
	pipelineList: string[];
}
const pipelineListStyle = {
	display: "flex",
	flexWrap: "wrap" as const,
	justifyContent: "space-between",
};
const pipelineStyle = {
	padding: "10px 19px",
	borderRadius: "19px",
	backgroundColor: GRAY_8,
	display: "inline-block",
	marginTop: "10px",
	whiteSpace: "nowrap" as const,
	overflow: "hidden",
	textOverflow: "ellipsis",
	maxWidth: "100%",
};
const PipelineList = ({ pipelineList }: PipelineListProps) => {
	const DEFAULT_SHOWN_NUMBER = 3;
	const defaultShownList = pipelineList.slice(0, DEFAULT_SHOWN_NUMBER);
	const defaultHiddenList = pipelineList.slice(DEFAULT_SHOWN_NUMBER, pipelineList.length);
	return (
		<section>
			<div>
				{defaultShownList.map((pipeline, index) => (
					<Word css={pipelineStyle} text={pipeline} type={"medium"} key={index} />
				))}
			</div>
			<div css={pipelineListStyle}>
				{defaultHiddenList.map((pipeline, index) => (
					<Word css={pipelineStyle} text={pipeline} type={"medium"} key={index} />
				))}
			</div>
		</section>
	);
};
export default PipelineList;
