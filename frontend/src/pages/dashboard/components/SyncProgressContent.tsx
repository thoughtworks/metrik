import React from "react";
import { Progress } from "antd";

export interface ProgressUpdateEvt {
	pipelineId: string;
	pipelineName: string;
	progress: number;
	batchSize: number;
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
		<>
			{Object.keys(progressSummary).map(progressKey => (
				<tr key={progressKey}>
					<td>{progressSummary[progressKey]?.pipelineName}:</td>
					<td>
						<Progress
							type="line"
							strokeWidth={16}
							size="small"
							steps={50}
							// size="default"
							// strokeLinecap="round"
							// strokeColor={{
							// 	from: "#D63F97",
							// 	to: "#298CE8",
							// }}
							percent={Math.floor(
								(progressSummary[progressKey]?.progress / progressSummary[progressKey]?.batchSize) *
									100
							)}
							format={() =>
								`${progressSummary[progressKey]?.progress}/${progressSummary[progressKey]?.batchSize}`
							}
						/>
					</td>
				</tr>
			))}
		</>
	);
};
