import FullscreenMetricsCard, { FullscreenMetricsCardOptions } from "./FullscreenMetricsCard";
import React from "react";

interface FullscreenDashboardProps {
	projectName: string;
	metricsList: FullscreenMetricsCardOptions[];
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
};

const FullscreenDashboard = ({ projectName, metricsList }: FullscreenDashboardProps) => {
	return (
		<section css={pageStyle}>
			<section css={pageContentStyle}>
				<section css={dataPropsSectionStyle}>
					<div>Logo</div>
					<p>{projectName}</p>
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
