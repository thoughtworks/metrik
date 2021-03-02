import React, { FC, useRef } from "react";
import {
	DownloadOutlined,
	LeftOutlined,
	PlusOutlined,
	SettingOutlined,
	UploadOutlined,
} from "@ant-design/icons";
import { css } from "@emotion/react";
import { Button, Modal, Result, Spin, Typography } from "antd";
import { createPipelineUsingPost, updatePipelineUsingPut } from "../../shared/clients/apis";
import ProjectConfig from "../../shared/components/ProjectConfig";
import PipelineConfig from "./PipelineConfig";
import { usePipelineSetting } from "../../shared/hooks/usePipelineSetting";
import { useModalVisible } from "../../shared/hooks/useModalVisible";

const settingStyles = css({
	fontSize: 16,
	padding: "5px 0",
	cursor: "pointer",
});

const modalHeaderTextStyles = css({
	fontSize: 16,
	color: "rgba(0, 0, 0, 0.85)",
	display: "flex",
	alignItems: "center",
});

export enum PipelineSettingStatus {
	VIEW,
	ADD,
	UPDATE,
}

const PipelineSetting: FC<{ dashboardId: string; syncBuild: () => void }> = ({
	dashboardId,
	syncBuild,
}) => {
	const { visible, handleToggleVisible } = useModalVisible();
	const {
		isProjectLoading,
		projectError,
		project,
		status,
		setStatus,
		editPipeline,
		getProjectDetails,
		onAddPipeline,
		onUpdatePipeline,
		onDeletePipeline,
	} = usePipelineSetting({
		defaultProjectId: dashboardId,
		shouldUpdateProject: visible,
		shouldResetStatus: !visible,
	});

	const triggerSyncBuild = useRef(false);

	function onAfterClose() {
		if (triggerSyncBuild.current) {
			triggerSyncBuild.current = false;
			syncBuild();
		}
	}

	return (
		<>
			<Button css={settingStyles} onClick={handleToggleVisible} type={"link"}>
				<SettingOutlined />
				Pipeline Settings
			</Button>
			<Modal
				visible={visible}
				onCancel={handleToggleVisible}
				afterClose={onAfterClose}
				centered={true}
				destroyOnClose={true}
				closable={false}
				maskClosable={false}
				bodyStyle={{
					padding: 0,
					height: status === PipelineSettingStatus.VIEW ? 511 : 600,
					overflowY: "auto",
				}}
				width={896}
				title={
					status === PipelineSettingStatus.VIEW ? (
						<div css={modalHeaderTextStyles}>
							<span css={{ flexGrow: 1 }}>Pipeline Settings</span>
							<div css={{ button: { margin: "0 4px" } }}>
								<Button icon={<DownloadOutlined />} disabled={true}>
									Export All
								</Button>
								<Button icon={<UploadOutlined />} disabled={true}>
									Import
								</Button>
								<Button
									type={"primary"}
									icon={<PlusOutlined />}
									onClick={onAddPipeline}
									disabled={isProjectLoading}>
									Add Pipeline
								</Button>
							</div>
						</div>
					) : (
						<div css={modalHeaderTextStyles}>
							<Button
								icon={<LeftOutlined />}
								css={{ marginRight: 16 }}
								onClick={() => setStatus(PipelineSettingStatus.VIEW)}
							/>
							<span css={{ flexGrow: 1 }}>Pipeline Setting</span>
						</div>
					)
				}
				footer={
					status === PipelineSettingStatus.VIEW ? (
						<Button size={"large"} css={{ margin: 14 }} onClick={handleToggleVisible}>
							Close
						</Button>
					) : null
				}>
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
				) : status === PipelineSettingStatus.VIEW ? (
					<ProjectConfig
						showDelete={true}
						showAddPipeline={false}
						pipelines={project?.pipelines ?? []}
						updatePipeline={onUpdatePipeline}
						deletePipeline={pipelineId => {
							triggerSyncBuild.current = true;
							return onDeletePipeline(pipelineId);
						}}
					/>
				) : (
					<PipelineConfig
						projectId={dashboardId}
						updateProject={getProjectDetails}
						css={{ padding: 24, height: "100%" }}
						defaultData={editPipeline}
						onSuccess={() => (triggerSyncBuild.current = true)}
						onSubmit={
							status === PipelineSettingStatus.ADD
								? createPipelineUsingPost
								: updatePipelineUsingPut
						}
						onBack={() => setStatus(PipelineSettingStatus.VIEW)}
					/>
				)}
			</Modal>
		</>
	);
};

export default PipelineSetting;
