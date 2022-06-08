import React from "react";
import { Progress } from "antd";
import { css } from "@emotion/react";

const tableRowStyles = css({
	"& td:first-of-type": {
		textAlign: "right",
		paddingRight: 12,
	},
	"& td:last-of-type": {
		paddingRight: 20,
	},
});

export interface ProgressUpdateEvt {
	pipelineId: string;
	pipelineName: string;
	progress: number;
	batchSize: number;
	step?: number;
	stepSize?: number;
}

export interface ProgressSummary {
	[key: string]: ProgressUpdateEvt;
}

interface SyncProgressContentProps {
	progressSummary: ProgressSummary;
}

export const SyncProgressContent: React.FC<SyncProgressContentProps> = ({ progressSummary }) => {
	const keys = Object.keys(progressSummary);

	return keys.length === 0 ? (
		<p>Waiting server response...</p>
	) : (
		<table>
			<tbody>
				{Object.keys(progressSummary).map(progressKey => (
					<tr key={progressKey} css={tableRowStyles}>
						<td>{progressSummary[progressKey]?.pipelineName}:</td>
						<td>
							<Progress
								type="line"
								strokeWidth={12}
								size="small"
								steps={50}
								strokeColor="#D63F97"
								percent={Math.floor(
									(progressSummary[progressKey]?.progress /
										progressSummary[progressKey]?.batchSize) *
										100
								)}
								format={() =>
									`${progressSummary[progressKey]?.progress}/${progressSummary[progressKey]?.batchSize}` +
									(progressSummary[progressKey]?.stepSize
										? `(Step ${progressSummary[progressKey]?.step}/${progressSummary[progressKey]?.stepSize})`
										: "")
								}
							/>
						</td>
					</tr>
				))}
			</tbody>
		</table>
	);
};
