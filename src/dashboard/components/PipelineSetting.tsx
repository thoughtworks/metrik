import React, { FC, useEffect, useState } from "react";
import {
	DownloadOutlined,
	LeftOutlined,
	PlusOutlined,
	SettingOutlined,
	UploadOutlined,
} from "@ant-design/icons";
import { css } from "@emotion/react";
import { Button, Modal, notification, Typography } from "antd";
import DashboardConfig from "../../components/DashboardConfig";
import PipelineConfig from "./PipelineConfig";
import {
	createPipelineUsingPost,
	DashboardDetailVo,
	deletePipelineUsingDelete,
	getDashboardDetailsUsingGet,
	PipelineVoRes,
	updatePipelineUsingPut,
} from "../../clients/apis";

const settingStyles = css({
	fontSize: 16,
	padding: "5px 0",
	cursor: "pointer",
});

const settingTextStyles = css({
	marginLeft: 10,
});

enum PipelineSettingStatus {
	VIEW,
	ADD,
	UPDATE,
}

const { Text } = Typography;
const PipelineSetting: FC<{ dashboardId: string }> = ({ dashboardId }) => {
	const [visible, setVisible] = useState(true);
	const [status, setStatus] = useState(PipelineSettingStatus.VIEW);
	const [dashboard, setDashboard] = useState<DashboardDetailVo>();
	const [editPipeline, setEditPipeline] = useState<PipelineVoRes>();

	useEffect(() => {
		if (!visible) {
			setStatus(PipelineSettingStatus.VIEW);
			setEditPipeline(undefined);
		}
	}, [visible]);

	useEffect(() => {
		if (visible) {
			getDashboardDetails();
		}
	}, [visible]);

	async function getDashboardDetails() {
		const data = await getDashboardDetailsUsingGet(dashboardId);
		setDashboard(data);
	}

	function handleToggleVisible() {
		setVisible(!visible);
	}

	function handlePipelineSettingStatusSwitch(status: PipelineSettingStatus) {
		setStatus(status);
	}

	function handleUpdatePipeline(pipeline: PipelineVoRes) {
		setEditPipeline(pipeline);
		setStatus(PipelineSettingStatus.UPDATE);
	}

	function checkPipelineAllowedToDelete() {
		return (dashboard?.pipelines.length ?? 0) > 1;
	}

	async function handleDeletePipeline(pipelineId: string) {
		if (!checkPipelineAllowedToDelete()) {
			notification.error({ message: "not allow to delete this pipeline" });
			return;
		}
		await deletePipelineUsingDelete({ dashboardId, pipelineId });
		await getDashboardDetails();
	}

	return (
		<>
			<span css={settingStyles} onClick={handleToggleVisible}>
				<SettingOutlined />
				<Text css={settingTextStyles}>Pipeline Setting</Text>
			</span>
			<Modal
				centered={true}
				css={{
					".ant-modal-body": {
						height: status === PipelineSettingStatus.VIEW ? 500 : 589,
						overflowY: "auto",
					},
				}}
				bodyStyle={{
					padding: 0,
				}}
				width={896}
				title={
					status === PipelineSettingStatus.VIEW ? (
						<div
							css={{
								fontSize: 16,
								color: "rgba(0, 0, 0, 0.85)",
								display: "flex",
								alignItems: "center",
							}}>
							<span css={{ flexGrow: 1 }}>Pipeline Setting</span>
							<div
								css={{
									button: {
										margin: "0 4px",
									},
								}}>
								<Button icon={<DownloadOutlined />} disabled={true}>
									Export All
								</Button>
								<Button icon={<UploadOutlined />} disabled={true}>
									Import
								</Button>
								<Button
									type={"primary"}
									icon={<PlusOutlined />}
									onClick={() => handlePipelineSettingStatusSwitch(PipelineSettingStatus.ADD)}>
									Add Pipeline
								</Button>
							</div>
						</div>
					) : (
						<div
							css={{
								fontSize: 16,
								color: "rgba(0, 0, 0, 0.85)",
								display: "flex",
								alignItems: "center",
							}}>
							<Button
								icon={<LeftOutlined />}
								css={{ marginRight: 16 }}
								onClick={() => handlePipelineSettingStatusSwitch(PipelineSettingStatus.VIEW)}
							/>
							<span css={{ flexGrow: 1 }}>Pipeline Setting</span>
						</div>
					)
				}
				footer={
					status === PipelineSettingStatus.VIEW ? (
						<Button size={"large"} css={{ margin: 8 }}>
							Close
						</Button>
					) : null
				}
				closable={false}
				visible={visible}
				onCancel={handleToggleVisible}>
				{status === PipelineSettingStatus.VIEW ? (
					<DashboardConfig
						showDelete={true}
						showAddPipeline={false}
						pipelines={dashboard?.pipelines ?? []}
						updatePipeline={handleUpdatePipeline}
						deletePipeline={handleDeletePipeline}
					/>
				) : (
					<PipelineConfig
						dashboardId={dashboardId}
						updateDashboard={getDashboardDetails}
						css={{ padding: 24 }}
						defaultData={editPipeline}
						onSubmit={
							status === PipelineSettingStatus.ADD
								? createPipelineUsingPost
								: updatePipelineUsingPut
						}
						onBack={() => handlePipelineSettingStatusSwitch(PipelineSettingStatus.VIEW)}
					/>
				)}
			</Modal>
		</>
	);
};

export default PipelineSetting;
