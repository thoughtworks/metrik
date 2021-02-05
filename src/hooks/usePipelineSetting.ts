import { useEffect, useState } from "react";
import {
	DashboardDetailVo,
	deletePipelineUsingDelete,
	getDashboardDetailsUsingGet,
	PipelineVoRes,
} from "../clients/apis";
import { PipelineSettingStatus } from "../dashboard/components/PipelineSetting";
import { notification } from "antd";

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
	defaultDashboard?: DashboardDetailVo;
}) => {
	const [dashboard, setDashboard] = useState<DashboardDetailVo | undefined>(defaultDashboard);
	const [status, setStatus] = useState(PipelineSettingStatus.VIEW);
	const [editPipeline, setEditPipeline] = useState<PipelineVoRes>();

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

	async function getDashboardDetails() {
		const data = await getDashboardDetailsUsingGet(defaultDashboardId);
		setDashboard(data);
	}

	function onAddPipeline() {
		setStatus(PipelineSettingStatus.ADD);
		setEditPipeline(undefined);
	}

	function onUpdatePipeline(pipeline: PipelineVoRes) {
		setStatus(PipelineSettingStatus.UPDATE);
		setEditPipeline(pipeline);
	}

	function checkPipelineAllowedToDelete() {
		return (dashboard?.pipelines.length ?? 0) > 1;
	}

	async function onDeletePipeline(pipelineId: string) {
		if (!checkPipelineAllowedToDelete()) {
			notification.error({ message: "not allow to delete this pipeline" });
			return;
		}
		await deletePipelineUsingDelete({ dashboardId: defaultDashboardId, pipelineId });
		await getDashboardDetails();
	}

	return {
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
