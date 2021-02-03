import React, { FC, useState } from "react";
import {
	DownloadOutlined,
	LeftOutlined,
	PlusOutlined,
	SettingOutlined,
	UploadOutlined,
} from "@ant-design/icons";
import { css } from "@emotion/react";
import { Button, Modal, Typography } from "antd";
import DashboardConfig from "../../components/DashboardConfig";
import PipelineConfig from "./PipelineConfig";

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
const PipelineSetting: FC = () => {
	const [visible, setVisible] = useState(true);
	const [status, setStatus] = useState(PipelineSettingStatus.VIEW);

	function handleToggleVisible() {
		setVisible(!visible);
	}

	function handleSwitchToAddStatus() {
		setStatus(PipelineSettingStatus.ADD);
	}

	return (
		<>
			<span css={settingStyles} onClick={handleToggleVisible}>
				<SettingOutlined />
				<Text css={settingTextStyles}>Pipeline Setting</Text>
			</span>
			<Modal
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
								<Button type={"primary"} icon={<PlusOutlined />} onClick={handleSwitchToAddStatus}>
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
							<Button icon={<LeftOutlined />} css={{ marginRight: 16 }} />
							<span css={{ flexGrow: 1 }}>Pipeline Setting</span>
						</div>
					)
				}
				footer={
					status === PipelineSettingStatus.VIEW ? (
						<Button size={"large"} css={{ margin: 14 }}>
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
						pipelines={[
							{
								id: "601a448aafe56915934375e7",
								name: "string",
								username: "string",
								credential: "string",
								url: "string",
								type: "BAMBOO",
							},
						]}
					/>
				) : (
					<PipelineConfig
						defaultData={{
							id: "601a448aafe56915934375e7",
							name: "string",
							username: "string",
							credential: "string",
							url: "string",
							type: "BAMBOO",
						}}
						onCancel={handleToggleVisible}
					/>
				)}
			</Modal>
		</>
	);
};

export default PipelineSetting;
