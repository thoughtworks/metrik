import FullscreenMetricsCard, { FullscreenMetricsCardOptions } from "./FullscreenMetricsCard";
import React, { KeyboardEvent } from "react";
import Word from "../../shared/components/Word/Word";
import moment from "moment";
import { dateFormatYYYYMMDD } from "../../shared/constants/date-format";
import { SwapRightOutlined } from "@ant-design/icons";
import PipelineList from "./PipelineList";
import MetricsLegend from "./MetricsLegend";
import { GRAY_1 } from "../../shared/constants/styles";
import Logo from "../../shared/components/Logo/Logo";
import { Modal, Popover } from "antd";

interface FullscreenDashboardProps {
	projectName: string;
	metricsList: FullscreenMetricsCardOptions[];
	startTimestamp: number;
	endTimestamp: number;
	pipelineList: string[];
	isFullscreenVisible: boolean;
}
const pageContentStyle = {
	width: "94vw",
	height: "90vh",
	display: "flex",
	justifyContent: "space-between",
};
const chartSectionStyle = {
	width: "70%",
	height: "100%",
	display: "flex",
	flexWrap: "wrap" as const,
	justifyContent: "space-between",
	alignContent: "space-between",
};
const metricsCardStyle = {
	height: "0.42vh",
	border: "1px solid red",
};
const pageStyle = {
	width: "100%",
	height: "100vh",
	backgroundColor: "#000",
	display: "flex",
	justifyContent: "space-around",
	alignItems: "center",
};
const dataPropsSectionStyle = {
	width: "26%",
	height: "100%",
	color: "white",
};

const FullscreenDashboard = ({
	projectName,
	metricsList,
	startTimestamp,
	endTimestamp,
	pipelineList,
	isFullscreenVisible,
}: FullscreenDashboardProps) => {
	const content = (
		<section css={pageStyle}>
			<section css={pageContentStyle}>
				<section css={dataPropsSectionStyle}>
					<Logo />
					<p css={{ margin: "0.3rem 0 0.5rem" }}>
						<Word text={projectName} type={"xxxLarge"} />
					</p>
					<div>
						<p css={{ marginBottom: 0, color: GRAY_1, opacity: 0.5 }}>
							<Word text={"Duration"} type={"large"} />
						</p>
						<p>
							<Word text={moment(startTimestamp).format(dateFormatYYYYMMDD)} type={"large"} />
							<SwapRightOutlined css={{ fontSize: "0.3rem", margin: "0 0.1rem" }} />
							<Word text={moment(endTimestamp).format(dateFormatYYYYMMDD)} type={"large"} />
						</p>
						<div>
							<p css={{ marginBottom: 0, color: GRAY_1, opacity: 0.5 }}>
								<Word type={"large"} text={`Pipelines(${pipelineList.length})`} />
							</p>
							<PipelineList pipelineList={pipelineList} />
						</div>
						<MetricsLegend />
					</div>
				</section>
				<section css={chartSectionStyle}>
					{metricsList.map((metrics, index) => (
						<FullscreenMetricsCard
							css={metricsCardStyle}
							key={index}
							metricsSummaryData={metrics.metricsSummaryData}
							metricsDataLabel={metrics.metricsDataLabel}
							metricsLevel={metrics.metricsLevel}
							metricsText={metrics.metricsText}
							data={metrics.data}
						/>
					))}
				</section>
			</section>
		</section>
	);
	return (
		<Modal
			wrapClassName={"fullscreenDashboardModal"}
			centered
			visible={isFullscreenVisible}
			width={"100vw"}
			keyboard={true}
			footer={null}
			modalRender={() => content}></Modal>
	);
};

export default FullscreenDashboard;
