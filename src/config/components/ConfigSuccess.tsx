import { Button } from "antd";
import React, { FC } from "react";
import { CheckCircleFilled } from "@ant-design/icons";
import DashboardConfig from "../../components/DashboardConfig";
import { Link } from "react-router-dom";
import { DashboardDetailVo, PipelineVoRes } from "../../clients/apis";

const dataSource: PipelineVoRes[] = [
	{
		id: "1",
		name: "4km-DEV",
		type: "JENKINS",
		url: "http://18.138.19.85:8001/job/4km-desk-check/",
		username: "",
		credential: "",
	},
	{
		id: "2",
		name: "4km-UAT",
		type: "JENKINS",
		url: "http://18.138.19.85:8001/job/4km-desk-check/",
		username: "",
		credential: "",
	},

	{
		id: "3",
		name: "4km-PROD",
		type: "JENKINS",
		url: "http://18.138.19.85:8001/job/4km-desk-check/",
		username: "",
		credential: "",
	},
];

const ConfigSuccess: FC<{ dashboard: DashboardDetailVo }> = ({ dashboard }) => {
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
				<Link to={`/?dashboardId=${dashboard.id}}`}>
					<Button type={"primary"} size={"large"}>
						Go to Dashboard
					</Button>
				</Link>
			</div>
			<DashboardConfig pipelines={dashboard.pipelines} />
		</div>
	);
};

export default ConfigSuccess;
