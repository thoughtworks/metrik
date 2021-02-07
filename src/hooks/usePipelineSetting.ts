import { useEffect, useState } from "react";
import {
	deletePipelineUsingDelete,
	getDashboardDetailsUsingGet,
	DashboardDetailResponse,
	PipelineResponse,
} from "../clients/apis";
import { PipelineSettingStatus } from "../dashboard/components/PipelineSetting";
import { notification } from "antd";
import { useFetch } from "./useFetch";

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
	const [dashboard, setDashboard] = useState<DashboardDetailResponse | undefined>(defaultDashboard);
	const [status, setStatus] = useState(PipelineSettingStatus.VIEW);
	const [editPipeline, setEditPipeline] = useState<PipelineResponse>();
	const { response, error, isLoading, doFetch } = useFetch(() =>
		getDashboardDetailsUsingGet({ dashboardId: defaultDashboardId })
	);

	useEffect(() => {
		if (shouldResetStatus) {
			setStatus(defaultStatus);
		}
	}, [shouldResetStatus]);

	useEffect(() => {
		if (shouldUpdateDashboard) {
			doFetch();
		}
	}, [shouldUpdateDashboard]);

	useEffect(() => {
		if (response) {
			setDashboard(response);
		}
	}, [response]);

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
			notification.error({ message: "not allow to delete this pipeline" });
			return;
		}
		await deletePipelineUsingDelete({ dashboardId: defaultDashboardId, pipelineId });
		await doFetch();
	}

	return {
		isDashboardLoading: isLoading,
		dashboardError: error,
		reloadDashboard: doFetch,
		dashboard,
		status,
		setStatus,
		editPipeline,
		setEditPipeline,
		getDashboardDetails: doFetch,
		onAddPipeline,
		onUpdatePipeline,
		onDeletePipeline,
	};
};
