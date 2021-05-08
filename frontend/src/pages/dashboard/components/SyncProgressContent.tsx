import React from "react";

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
			<p>Progress:</p>
			{Object.keys(progressSummary).map(progressKey => (
				<p key={progressKey}>
					{progressSummary[progressKey]?.pipelineName}: {progressSummary[progressKey]?.progress}/
					{progressSummary[progressKey]?.batchSize}
				</p>
			))}
		</>
	);
};
