import FullscreenMetricsCard, { FullscreenMetricsCardOptions } from "./FullscreenMetricsCard";
import React from "react";
import Word from "../../shared/components/Word/Word";
import moment from "moment";
import { dateFormatYYYYMMDD } from "../../shared/constants/date-format";
import { SwapRightOutlined } from "@ant-design/icons";
import PipelineList from "./PipelineList";
import MetricsLegend from "./MetricsLegend";

interface FullscreenDashboardProps {
	projectName: string;
	metricsList: FullscreenMetricsCardOptions[];
	startTimestamp: number;
	endTimestamp: number;
	pipelineList: string[];
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
	border: "1px solid pink",
	color: "white",
};

const FullscreenDashboard = ({
	projectName,
	metricsList,
	startTimestamp,
	endTimestamp,
	pipelineList,
}: FullscreenDashboardProps) => {
	return (
		<section css={pageStyle}>
			<section css={pageContentStyle}>
				<section css={dataPropsSectionStyle}>
					<div>Logo</div>
					<p>
						<Word text={projectName} type={"xxxLarge"} />
					</p>
					<div>
						<p css={{ marginBottom: 0 }}>
							<Word text={"Duration"} type={"large"} />
						</p>
						<p>
							<Word text={moment(startTimestamp).format(dateFormatYYYYMMDD)} type={"large"} />
							<SwapRightOutlined css={{ fontSize: "0.3rem", margin: "0 0.1rem" }} />
							<Word text={moment(endTimestamp).format(dateFormatYYYYMMDD)} type={"large"} />
						</p>
						<div>
							<p css={{ marginBottom: 0 }}>
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
};

export default FullscreenDashboard;
