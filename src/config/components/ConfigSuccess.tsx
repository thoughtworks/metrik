import { Button } from "antd";
import React, { FC, useState } from "react";
import { CheckCircleFilled } from "@ant-design/icons";
import DashboardConfig from "../../components/DashboardConfig";
import { Link } from "react-router-dom";
import {
	DashboardDetailVo,
	getDashboardDetailsUsingGet,
	PipelineVoRes,
	updatePipelineUsingPut,
} from "../../clients/apis";
import PipelineSettingModal from "../../components/PipelineSettingModal";
import PipelineConfig from "../../dashboard/components/PipelineConfig";

const ConfigSuccess: FC<{ defaultDashboard: DashboardDetailVo }> = ({ defaultDashboard }) => {
	const [dashboard, setDashboard] = useState<DashboardDetailVo>(defaultDashboard);
	const [visible, setVisible] = useState(false);
	const [editPipeline, setEditPipeline] = useState<PipelineVoRes>();

	async function getDashboardDetails() {
		const data = await getDashboardDetailsUsingGet(dashboard.id);
		setDashboard(data);
	}

	function handleToggleVisible() {
		setVisible(!visible);
	}

	function handleUpdatePipeline(pipeline: PipelineVoRes) {
		setVisible(true);
		setEditPipeline(pipeline);
	}

	return (
		<div>
			<div css={{ display: "flex", alignItems: "center", marginBottom: 32 }}>
				<CheckCircleFilled css={{ fontSize: 70, color: "#52C41A" }} />
				<div
					css={{
						display: "flex",
						flexDirection: "column",
						alignItems: "flex-start",
						fontSize: 24,
						color: "black",
						margin: "0 30px",
					}}>
					<span>Pipeline successfully created!</span>
					<span
						css={{
							fontSize: 14,
							opacity: 0.5,
						}}>
						The initial configuration is complete，4 Key Metrics will be presented based on the
						following projects
					</span>
				</div>
				<Link to={`/?dashboardId=${dashboard.id}`}>
					<Button type={"primary"} size={"large"}>
						Go to Dashboard
					</Button>
				</Link>
			</div>
			<DashboardConfig pipelines={dashboard.pipelines} updatePipeline={handleUpdatePipeline} />
			<PipelineSettingModal
				visible={visible}
				handleToggleVisible={handleToggleVisible}
				title={"Pipeline"}>
				<PipelineConfig
					dashboardId={dashboard.id}
					updateDashboard={getDashboardDetails}
					css={{ padding: 24 }}
					defaultData={editPipeline}
					onSubmit={updatePipelineUsingPut}
					onBack={handleToggleVisible}
				/>
			</PipelineSettingModal>
		</div>
	);
};

export default ConfigSuccess;
