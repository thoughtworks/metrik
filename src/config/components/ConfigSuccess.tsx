import { Button, Modal } from "antd";
import React, { FC } from "react";
import { CheckCircleFilled } from "@ant-design/icons";
import DashboardConfig from "../../shared/components/DashboardConfig";
import { Link } from "react-router-dom";
import {
	createPipelineUsingPost,
	PipelineResponse,
	updatePipelineUsingPut,
	DashboardDetailResponse,
} from "../../shared/clients/apis";
import PipelineConfig from "../../dashboard/components/PipelineConfig";
import { PipelineSettingStatus } from "../../dashboard/components/PipelineSetting";
import { useModalVisible } from "../../shared/hooks/useModalVisible";
import { usePipelineSetting } from "../../shared/hooks/usePipelineSetting";
import { GREEN_LIGHT } from "../../shared/constants/styles";

const ConfigSuccess: FC<{ defaultDashboard: DashboardDetailResponse }> = ({ defaultDashboard }) => {
	const { visible, handleToggleVisible } = useModalVisible();
	const {
		dashboard,
		editPipeline,
		status,
		getDashboardDetails,
		onAddPipeline,
		onUpdatePipeline,
	} = usePipelineSetting({
		defaultDashboardId: defaultDashboard.id,
		defaultDashboard: defaultDashboard,
		defaultStatus: PipelineSettingStatus.ADD,
		shouldUpdateDashboard: false,
		shouldResetStatus: !visible,
	});

	function handleUpdatePipeline(pipeline: PipelineResponse) {
		onUpdatePipeline(pipeline);
		handleToggleVisible();
	}

	function handleAddPipeline() {
		onAddPipeline();
		handleToggleVisible();
	}

	return (
		<div>
			<div css={{ display: "flex", alignItems: "center", marginBottom: 32 }}>
				<CheckCircleFilled css={{ fontSize: 70, color: GREEN_LIGHT }} />
				<div
					css={{
						display: "flex",
						flexDirection: "column",
						alignItems: "flex-start",
						fontSize: 24,
						color: "black",
						margin: "0 30px",
					}}>
					<span>Project successfully created!</span>
					<span
						css={{
							fontSize: 14,
							opacity: 0.5,
						}}>
						Congratulations! Your project has been successfully created. The 4 key metrics will be
						displayed based on the following pipeline data.
					</span>
				</div>
				<Button type={"primary"} size={"large"} onClick={() => location.reload()}>
					Go to Dashboard
				</Button>
			</div>
			<DashboardConfig
				pipelines={dashboard!.pipelines}
				updatePipeline={handleUpdatePipeline}
				addPipeline={handleAddPipeline}
			/>
			<Modal
				maskClosable={false}
				bodyStyle={{ padding: 0, height: 600, overflowY: "auto" }}
				width={896}
				centered={true}
				destroyOnClose={true}
				closable={false}
				visible={visible}
				onCancel={handleToggleVisible}
				title={"Pipeline"}
				footer={null}>
				<PipelineConfig
					dashboardId={defaultDashboard.id}
					updateDashboard={getDashboardDetails}
					css={{ padding: 24, height: "100%" }}
					defaultData={editPipeline}
					onSubmit={
						status === PipelineSettingStatus.ADD ? createPipelineUsingPost : updatePipelineUsingPut
					}
					onBack={handleToggleVisible}
				/>
			</Modal>
		</div>
	);
};

export default ConfigSuccess;
