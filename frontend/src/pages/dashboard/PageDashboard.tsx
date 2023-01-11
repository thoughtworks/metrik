import React, { useEffect, useState } from "react";
import { Col, Row } from "antd";
import { css } from "@emotion/react";
import { useQuery } from "../../hooks/useQuery";
import { MetricsCard } from "./components/MetricsCard";
import { DashboardTopPanel } from "./components/DashboardTopPanel";
import { BACKGROUND_COLOR } from "../../constants/styles";
import { MetricTooltip } from "./components/MetricTooltip";
import { calcMaxValueWithRatio } from "../../utils/calcMaxValueWithRatio/calcMaxValueWithRatio";
import { cleanMetricsInfo } from "../../utils/metricsDataUtils/metricsDataUtils";
import { MetricsInfo, MetricsLevel, MetricsUnit } from "../../models/metrics";
import { DashboardContextProvider, useDashboardContext } from "./context/DashboardContext";

const metricsContainerStyles = css({
	padding: "37px 35px",
	background: BACKGROUND_COLOR,
});

const initialMetricsState: MetricsInfo = {
	summary: {
		level: MetricsLevel.INVALID,
		value: 0,
		endTimestamp: 0,
		startTimestamp: 0,
	},
	details: [],
};

const domainMaximizeRatio = 1.1;

const PageDashboardContent = () => {
	const query = useQuery();
	const projectId = query.get("projectId") || "";

	const [changeFailureRate, setChangeFailureRate] = useState<MetricsInfo>(initialMetricsState);
	const [deploymentFrequency, setDeploymentFrequency] = useState<MetricsInfo>(initialMetricsState);
	const [leadTimeForChange, setLeadTimeForChange] = useState<MetricsInfo>(initialMetricsState);
	const [meanTimeToRestore, setMeanTimeToRestore] = useState<MetricsInfo>(initialMetricsState);

	const { fourKeyMetrics, appliedFormValue, updatingStatus } = useDashboardContext();

	useEffect(() => {
		setChangeFailureRate(cleanMetricsInfo(fourKeyMetrics.changeFailureRate));
		setDeploymentFrequency(cleanMetricsInfo(fourKeyMetrics.deploymentFrequency));
		setLeadTimeForChange(cleanMetricsInfo(fourKeyMetrics.leadTimeForChange));
		setMeanTimeToRestore(cleanMetricsInfo(fourKeyMetrics.meanTimeToRestore));
	}, [fourKeyMetrics]);

	const getSubTitleUnit = (unit: MetricsUnit) => {
		return `Times per ${unit.toLowerCase().replace("ly", "")}`;
	};

	return updatingStatus.inited ? (
		<>
			<DashboardTopPanel projectId={projectId} />
			<div css={metricsContainerStyles}>
				<Row gutter={28}>
					<Col xs={24} sm={24} md={24} lg={12}>
						<MetricsCard
							title="Deployment Frequency (Times)"
							info={<MetricTooltip unit={appliedFormValue.unit} type={"df"} />}
							summary={deploymentFrequency.summary}
							data={deploymentFrequency.details}
							yaxisFormatter={(value: string) => value}
							yAxisLabel="Times"
							loading={updatingStatus.loadingMetricsData}
							subTitleUnit={getSubTitleUnit(appliedFormValue.unit)}
							yAxisDomain={[
								0,
								calcMaxValueWithRatio(deploymentFrequency.details, 20, domainMaximizeRatio),
							]}
						/>
					</Col>

					<Col xs={24} sm={24} md={24} lg={12}>
						<MetricsCard
							title="Average Lead Time for Change (Days)"
							info={<MetricTooltip unit={appliedFormValue.unit} type={"lt"} />}
							summary={leadTimeForChange.summary}
							data={leadTimeForChange.details}
							yaxisFormatter={(value: string) => value}
							yAxisLabel="Days"
							loading={updatingStatus.loadingMetricsData}
							subTitleUnit="Days"
							yAxisDomain={[
								0,
								calcMaxValueWithRatio(leadTimeForChange.details, 1, domainMaximizeRatio),
							]}
						/>
					</Col>

					<Col xs={24} sm={24} md={24} lg={12}>
						<MetricsCard
							title="Mean Time to Restore Service (Hours)"
							info={<MetricTooltip unit={appliedFormValue.unit} type={"mttr"} />}
							summary={meanTimeToRestore.summary}
							data={meanTimeToRestore.details}
							yaxisFormatter={(value: string) => value}
							yAxisLabel="Hours"
							loading={updatingStatus.loadingMetricsData}
							subTitleUnit="Hours"
							yAxisDomain={[
								0,
								calcMaxValueWithRatio(meanTimeToRestore.details, 1, domainMaximizeRatio),
							]}
						/>
					</Col>

					<Col xs={24} sm={24} md={24} lg={12}>
						<MetricsCard
							title="Change Failure Rate"
							info={<MetricTooltip unit={appliedFormValue.unit} type={"cfr"} />}
							summary={changeFailureRate.summary}
							data={changeFailureRate.details}
							yaxisFormatter={(value: string) => value + "%"}
							yAxisLabel="Percentage"
							loading={updatingStatus.loadingMetricsData}
							subTitleUnit="Percentage"
							yAxisDomain={[0, calcMaxValueWithRatio(changeFailureRate.details, 100, 1)]}
						/>
					</Col>
				</Row>
			</div>
		</>
	) : (
		<div></div>
	);
};

export const PageDashboard = () => {
	return (
		<DashboardContextProvider>
			<PageDashboardContent />
		</DashboardContextProvider>
	);
};
