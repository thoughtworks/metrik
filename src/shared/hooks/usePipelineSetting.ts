import { useEffect, useState } from "react";
import {
	deletePipelineUsingDelete,
	getDashboardDetailsUsingGet,
	DashboardDetailResponse,
	PipelineResponse,
} from "../clients/apis";
import { PipelineSettingStatus } from "../../dashboard/components/PipelineSetting";
import { notification } from "antd";
import { useRequest } from "./useRequest";

export const usePipelineSetting = ({
	defaultDashboardId,
	defaultDashboard,
	shouldUpdateDashboard,
	defaultStatus = PipelineSettingStatus.VIEW,
	shouldResetStatus,
}: {
	defaultDashboardId: string;
	shouldUpdateDashboard: boolean;
	shouldResetStatus: boolean;
	defaultStatus?: PipelineSettingStatus;
	defaultDashboard?: DashboardDetailResponse;
}) => {
	const [status, setStatus] = useState(PipelineSettingStatus.VIEW);
	const [editPipeline, setEditPipeline] = useState<PipelineResponse>();
	const [dashboard, getDashboard, isDashboardLoading, , dashboardError] = useRequest(
		getDashboardDetailsUsingGet,
		defaultDashboard
	);

	function getDashboardDetails() {
		getDashboard({ dashboardId: defaultDashboardId });
	}

	useEffect(() => {
		if (shouldResetStatus) {
			setStatus(defaultStatus);
		}
	}, [shouldResetStatus]);

	useEffect(() => {
		if (shouldUpdateDashboard) {
			getDashboardDetails();
		}
	}, [shouldUpdateDashboard]);

	function onAddPipeline() {
		setStatus(PipelineSettingStatus.ADD);
		setEditPipeline(undefined);
	}

	function onUpdatePipeline(pipeline: PipelineResponse) {
		setStatus(PipelineSettingStatus.UPDATE);
		setEditPipeline(pipeline);
	}

	function checkPipelineAllowedToDelete() {
		return (dashboard?.pipelines.length ?? 0) > 1;
	}

	async function onDeletePipeline(pipelineId: string) {
		if (!checkPipelineAllowedToDelete()) {
			return;
		}
		await deletePipelineUsingDelete({ dashboardId: defaultDashboardId, pipelineId });
		await getDashboardDetails();
	}

	return {
		isDashboardLoading,
		dashboardError,
		dashboard,
		status,
		setStatus,
		editPipeline,
		setEditPipeline,
		getDashboardDetails,
		onAddPipeline,
		onUpdatePipeline,
		onDeletePipeline,
	};
};
