import React, { FC } from "react";
import {
	DownloadOutlined,
	LeftOutlined,
	PlusOutlined,
	SettingOutlined,
	UploadOutlined,
} from "@ant-design/icons";
import { css } from "@emotion/react";
import { Button, Typography } from "antd";
import { createPipelineUsingPost, updatePipelineUsingPut } from "../../clients/apis";
import PipelineSettingModal from "../../components/PipelineSettingModal";
import DashboardConfig from "../../components/DashboardConfig";
import PipelineConfig from "./PipelineConfig";
import { usePipelineSetting } from "../../hooks/usePipelineSetting";
import { useModalVisible } from "../../hooks/useModalVisible";

const { Text } = Typography;

const settingStyles = css({
	fontSize: 16,
	padding: "5px 0",
	cursor: "pointer",
});

const settingTextStyles = css({
	marginLeft: 10,
});

export enum PipelineSettingStatus {
	VIEW,
	ADD,
	UPDATE,
}

//TODO: justify ui style according design
//TODO: add error handling in case of api call failed
//TODO: add loading status during fetching api
//TODO: see if there's other way to refine logic code

const PipelineSetting: FC<{ dashboardId: string }> = ({ dashboardId }) => {
	const { visible, handleToggleVisible } = useModalVisible();
	const {
		dashboard,
		status,
		setStatus,
		editPipeline,
		getDashboardDetails,
		onAddPipeline,
		onUpdatePipeline,
		onDeletePipeline,
	} = usePipelineSetting({
		defaultDashboardId: dashboardId,
		shouldUpdateDashboard: visible,
		shouldResetStatus: !visible,
	});

	return (
		<>
			<span css={settingStyles} onClick={handleToggleVisible}>
				<SettingOutlined />
				<Text css={settingTextStyles}>Pipeline Setting</Text>
			</span>
			<PipelineSettingModal
				css={{
					".ant-modal-body": {
						height: status === PipelineSettingStatus.VIEW ? 511 : 600,
						overflowY: "auto",
					},
				}}
				visible={visible}
				handleToggleVisible={handleToggleVisible}
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
								<Button type={"primary"} icon={<PlusOutlined />} onClick={onAddPipeline}>
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
				{status === PipelineSettingStatus.VIEW ? (
					<DashboardConfig
						showDelete={true}
						showAddPipeline={false}
						pipelines={dashboard?.pipelines ?? []}
						updatePipeline={onUpdatePipeline}
						deletePipeline={onDeletePipeline}
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
						onBack={() => setStatus(PipelineSettingStatus.VIEW)}
					/>
				)}
			</PipelineSettingModal>
		</>
	);
};

export default PipelineSetting;
