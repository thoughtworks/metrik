import { Button, Modal, Result, Spin } from "antd";
import React, { FC, useEffect } from "react";
import { CheckCircleFilled } from "@ant-design/icons";
import ProjectConfig from "../../shared/components/ProjectConfig";
import {
	createPipelineUsingPost,
	PipelineResponse,
	updatePipelineUsingPut,
} from "../../shared/clients/apis";
import PipelineConfig from "../../dashboard/components/PipelineConfig";
import { PipelineSettingStatus } from "../../dashboard/components/PipelineSetting";
import { useModalVisible } from "../../shared/hooks/useModalVisible";
import { usePipelineSetting } from "../../shared/hooks/usePipelineSetting";
import { GREEN_LIGHT } from "../../shared/constants/styles";

const ConfigSuccess: FC<{ projectId: string }> = ({ projectId }) => {
	const { visible, handleToggleVisible } = useModalVisible();
	const {
		project,
		isProjectLoading,
		projectError,
		editPipeline,
		status,
		getProjectDetails,
		onAddPipeline,
		onUpdatePipeline,
	} = usePipelineSetting({
		defaultProjectId: projectId,
		defaultStatus: PipelineSettingStatus.ADD,
		shouldUpdateProject: false,
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

	useEffect(() => {
		getProjectDetails();
	}, []);

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

			{isProjectLoading ? (
				<Spin
					size="large"
					css={{
						display: "flex",
						justifyContent: "center",
						alignItems: "center",
						height: "100%",
					}}
				/>
			) : projectError ? (
				<Result
					css={{
						display: "flex",
						flexDirection: "column",
						justifyContent: "center",
						alignItems: "center",
						height: "100%",
					}}
					status="warning"
					title="Fail to connect to pipeline API"
					subTitle={"Please click the button below, reload the API"}
					extra={
						<Button type="primary" key="console" onClick={getProjectDetails}>
							Reload
						</Button>
					}
				/>
			) : (
				project && (
					<ProjectConfig
						pipelines={project.pipelines}
						updatePipeline={handleUpdatePipeline}
						addPipeline={handleAddPipeline}
					/>
				)
			)}
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
					projectId={projectId}
					updateProject={getProjectDetails}
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
