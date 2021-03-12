import { Button, Modal, Result, Spin } from "antd";
import React, { FC, useEffect } from "react";
import { CheckCircleFilled } from "@ant-design/icons";
import ProjectConfig from "../../shared/components/ProjectConfig";
import { PipelineSettingStatus } from "../../dashboard/components/PipelineSetting";
import { useModalVisible } from "../../shared/hooks/useModalVisible";
import { usePipelineSetting } from "../../shared/hooks/usePipelineSetting";
import { GREEN_LIGHT } from "../../shared/constants/styles";
import {
	createPipelineUsingPost,
	Pipeline,
	updatePipelineUsingPut,
} from "../../shared/clients/pipelineApis";
import PipelineSetup, { FormValues } from "../../shared/components/PipelineSetup/PipelineSetup";

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

	function handleUpdatePipeline(pipeline: Pipeline) {
		onUpdatePipeline(pipeline);
		handleToggleVisible();
	}

	function handleAddPipeline() {
		onAddPipeline();
		handleToggleVisible();
	}

	const onSubmit = async (values: FormValues) => {
		const handleFn =
			status === PipelineSettingStatus.ADD ? createPipelineUsingPost : updatePipelineUsingPut;
		await handleFn({
			projectId: projectId,
			pipeline: { ...values, id: editPipeline?.id as string },
		});
		await getProjectDetails();
		handleToggleVisible();
	};

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
					<>
						<ProjectConfig
							pipelines={project.pipelines}
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
							<PipelineSetup
								onText={status === PipelineSettingStatus.ADD ? "Create" : "Update"}
								pipeline={editPipeline}
								onSubmit={onSubmit}
								onBack={handleToggleVisible}
							/>
						</Modal>
					</>
				)
			)}
		</div>
	);
};
export default ConfigSuccess;
