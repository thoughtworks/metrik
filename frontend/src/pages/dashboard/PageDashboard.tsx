import React, { useState } from "react";
import { Col, Row } from "antd";
import { css } from "@emotion/react";
import { useQuery } from "../../hooks/useQuery";
import { getDurationTimestamps } from "../../utils/timeFormats/timeFormats";
import { MetricsCard } from "./components/MetricsCard";
import { DashboardTopPanel, FormValues } from "./components/DashboardTopPanel";
import { BACKGROUND_COLOR } from "../../constants/styles";
import { MetricTooltip } from "./components/MetricTooltip";
import { calcMaxValueWithRatio } from "../../utils/calcMaxValueWithRatio/calcMaxValueWithRatio";
import { cleanMetricsInfo } from "../../utils/metricsDataUtils/metricsDataUtils";
import { FourKeyMetrics, getFourKeyMetricsUsingPost } from "../../clients/metricsApis";
import { MetricsInfo, MetricsLevel, MetricsUnit } from "../../models/metrics";
import { DashboardContextProvider } from "./context/DashboardContext";

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

export const PageDashboard = () => {
	const query = useQuery();
	const projectId = query.get("projectId") || "";

	const [appliedUnit, setAppliedUnit] = useState<MetricsUnit>(MetricsUnit.FORTNIGHTLY);
	const [changeFailureRate, setChangeFailureRate] = useState<MetricsInfo>(initialMetricsState);
	const [deploymentFrequency, setDeploymentFrequency] = useState<MetricsInfo>(initialMetricsState);
	const [leadTimeForChange, setLeadTimeForChange] = useState<MetricsInfo>(initialMetricsState);
	const [meanTimeToRestore, setMeanTimeToRestore] = useState<MetricsInfo>(initialMetricsState);
	const [loadingChart, setLoadingChart] = useState(false);
	const defaultMetricsData = {
		summary: {
			value: undefined,
			level: MetricsLevel.INVALID,
			endTimestamp: 0,
			startTimestamp: 0,
		},
		details: [
			{
				value: undefined,
				endTimestamp: 0,
				startTimestamp: 0,
			},
		],
	};
	const [metricsResponse, setMetricsResponse] = useState<FourKeyMetrics>({
		changeFailureRate: defaultMetricsData,
		deploymentFrequency: defaultMetricsData,
		leadTimeForChange: defaultMetricsData,
		meanTimeToRestore: defaultMetricsData,
	});

	const getFourKeyMetrics = (formValues: FormValues) => {
		console.log(formValues);
		setLoadingChart(true);
		setAppliedUnit(formValues.unit);

		const durationTimestamps = getDurationTimestamps(formValues.duration);
		getFourKeyMetricsUsingPost({
			metricsQuery: {
				startTime: durationTimestamps.startTimestamp!,
				endTime: durationTimestamps.endTimestamp!,
				pipelineStages: (formValues.pipelines || []).map(i => ({
					pipelineId: i.value,
					stage: i.childValue,
				})),
				unit: formValues.unit,
			},
		})
			.then(response => {
				setMetricsResponse(response);
				setChangeFailureRate(cleanMetricsInfo(response.changeFailureRate));
				setDeploymentFrequency(cleanMetricsInfo(response.deploymentFrequency));
				setLeadTimeForChange(cleanMetricsInfo(response.leadTimeForChange));
				setMeanTimeToRestore(cleanMetricsInfo(response.meanTimeToRestore));
			})
			.finally(() => {
				setLoadingChart(false);
			});
	};

	const getSubTitleUnit = (unit: MetricsUnit) => {
		return `Times per ${unit.toLowerCase().replace("ly", "")}`;
	};

	return (
		<DashboardContextProvider>
			<DashboardTopPanel
				onApply={getFourKeyMetrics}
				projectId={projectId}
				metricsResponse={metricsResponse}
			/>
			<div css={metricsContainerStyles}>
				<Row gutter={28}>
					<Col xs={24} sm={24} md={24} lg={12}>
						<MetricsCard
							title="Deployment Frequency (Times)"
							info={<MetricTooltip unit={appliedUnit} type={"df"} />}
							summary={deploymentFrequency.summary}
							data={deploymentFrequency.details}
							yaxisFormatter={(value: string) => value}
							yAxisLabel="Times"
							loading={loadingChart}
							subTitleUnit={getSubTitleUnit(appliedUnit)}
							yAxisDomain={[
								0,
								calcMaxValueWithRatio(deploymentFrequency.details, 20, domainMaximizeRatio),
							]}
						/>
					</Col>

					<Col xs={24} sm={24} md={24} lg={12}>
						<MetricsCard
							title="Average Lead Time for Change (Days)"
							info={<MetricTooltip unit={appliedUnit} type={"lt"} />}
							summary={leadTimeForChange.summary}
							data={leadTimeForChange.details}
							yaxisFormatter={(value: string) => value}
							yAxisLabel="Days"
							loading={loadingChart}
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
							info={<MetricTooltip unit={appliedUnit} type={"mttr"} />}
							summary={meanTimeToRestore.summary}
							data={meanTimeToRestore.details}
							yaxisFormatter={(value: string) => value}
							yAxisLabel="Hours"
							loading={loadingChart}
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
							info={<MetricTooltip unit={appliedUnit} type={"cfr"} />}
							summary={changeFailureRate.summary}
							data={changeFailureRate.details}
							yaxisFormatter={(value: string) => value + "%"}
							yAxisLabel="Percentage"
							loading={loadingChart}
							subTitleUnit="Percentage"
							yAxisDomain={[0, calcMaxValueWithRatio(changeFailureRate.details, 100, 1)]}
						/>
					</Col>
				</Row>
			</div>
		</DashboardContextProvider>
	);
};
