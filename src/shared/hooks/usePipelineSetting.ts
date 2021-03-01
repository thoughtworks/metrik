import { useEffect, useState } from "react";
import {
	deletePipelineUsingDelete,
	getProjectDetailsUsingGet,
	PipelineResponse,
	ProjectDetailResponse,
} from "../clients/apis";
import { PipelineSettingStatus } from "../../dashboard/components/PipelineSetting";
import { useRequest } from "./useRequest";

export const usePipelineSetting = ({
	defaultProjectId,
	defaultProject,
	shouldUpdateProject,
	defaultStatus = PipelineSettingStatus.VIEW,
	shouldResetStatus,
}: {
	defaultProjectId: string;
	shouldUpdateProject: boolean;
	shouldResetStatus: boolean;
	defaultStatus?: PipelineSettingStatus;
	defaultProject?: ProjectDetailResponse;
}) => {
	const [status, setStatus] = useState(PipelineSettingStatus.VIEW);
	const [editPipeline, setEditPipeline] = useState<PipelineResponse>();
	const [project, getProject, isProjectLoading, , projectError] = useRequest(
		getProjectDetailsUsingGet,
		defaultProject
	);

	function getProjectDetails() {
		getProject({ projectId: defaultProjectId });
	}

	useEffect(() => {
		if (shouldResetStatus) {
			setStatus(defaultStatus);
		}
	}, [shouldResetStatus]);

	useEffect(() => {
		if (shouldUpdateProject) {
			getProjectDetails();
		}
	}, [shouldUpdateProject]);

	function onAddPipeline() {
		setStatus(PipelineSettingStatus.ADD);
		setEditPipeline(undefined);
	}

	function onUpdatePipeline(pipeline: PipelineResponse) {
		setStatus(PipelineSettingStatus.UPDATE);
		setEditPipeline(pipeline);
	}

	function checkPipelineAllowedToDelete() {
		return (project?.pipelines.length ?? 0) > 1;
	}

	async function onDeletePipeline(pipelineId: string) {
		if (!checkPipelineAllowedToDelete()) {
			return;
		}
		await deletePipelineUsingDelete({ projectId: defaultProjectId, pipelineId });
		await getProjectDetails();
	}

	return {
		isProjectLoading,
		projectError,
		project,
		status,
		setStatus,
		editPipeline,
		setEditPipeline,
		getProjectDetails,
		onAddPipeline,
		onUpdatePipeline,
		onDeletePipeline,
	};
};
