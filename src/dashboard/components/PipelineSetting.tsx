import React, { FC } from "react";
import {
	DownloadOutlined,
	LeftOutlined,
	PlusOutlined,
	SettingOutlined,
	UploadOutlined,
} from "@ant-design/icons";
import { css } from "@emotion/react";
import { Button, Modal, Result, Spin, Typography } from "antd";
import { createPipelineUsingPost, updatePipelineUsingPut } from "../../clients/apis";
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

//TODO: justify ui style according design
//TODO: add error handling in case of api call failed
//TODO: add loading status during fetching api
//TODO: see if there's other way to refine logic code

const PipelineSetting: FC<{ dashboardId: string }> = ({ dashboardId }) => {
	const { visible, handleToggleVisible } = useModalVisible();
	const {
		isDashboardLoading,
		dashboardError,
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
			<Modal
				visible={visible}
				onCancel={handleToggleVisible}
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
							<span css={{ flexGrow: 1 }}>Pipeline Setting</span>
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
									disabled={isDashboardLoading}>
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
				{isDashboardLoading ? (
					<Spin
						size="large"
						css={{
							display: "flex",
							justifyContent: "center",
							alignItems: "center",
							height: "100%",
						}}
					/>
				) : dashboardError ? (
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
							<Button type="primary" key="console" onClick={getDashboardDetails}>
								Reload
							</Button>
						}
					/>
				) : status === PipelineSettingStatus.VIEW ? (
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
						css={{ padding: 24, height: "100%" }}
						defaultData={editPipeline}
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
