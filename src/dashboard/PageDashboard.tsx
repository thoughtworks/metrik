import React, { useState } from "react";
import { Col, Row } from "antd";
import { css } from "@emotion/react";
import { useQuery } from "../hooks/useQuery";
import { getFourKeyMetricsUsingGet, MetricsInfo, MetricsLevel } from "../clients/apis";
import { momentObjToEndTimeStamp, momentObjToStartTimeStamp } from "../utils/timeFormats";
import { MetricsCard } from "./components/MetricsCard";
import { DashboardTopPanel, FormValues } from "./components/DashboardTopPanel";

const metricsContainerStyles = css({
	padding: "37px 35px",
	background: "#F0F2F5",
});

const initialMetricsState: MetricsInfo = {
	summary: {
		endTimestamp: 0,
		level: MetricsLevel.INVALID,
		startTimestamp: 0,
		value: NaN,
	},
	details: [],
};

export const PageDashboard = () => {
	const query = useQuery();
	const dashboardId = query.get("dashboardId") || "";

	const [formValues, setFormValues] = useState<FormValues>({} as FormValues);
	const [changeFailureRate, setChangeFailureRate] = useState<MetricsInfo>(initialMetricsState);
	const [deploymentFrequency, setDeploymentFrequency] = useState<MetricsInfo>(initialMetricsState);
	const [leadTimeForChange, setLeadTimeForChange] = useState<MetricsInfo>(initialMetricsState);
	const [meanTimeToRestore, setMeanTimeToRestore] = useState<MetricsInfo>(initialMetricsState);
	const [loadingChart, setLoadingChart] = useState(false);

	const getFourKeyMetrics = () => {
		setLoadingChart(true);
		// TODO: will pass multiple stages and pipelines after backend api ready
		getFourKeyMetricsUsingGet({
			endTime: momentObjToEndTimeStamp(formValues.duration[0]),
			startTime: momentObjToStartTimeStamp(formValues.duration[1]),
			pipelineId: formValues.pipelines[0].value,
			targetStage: formValues.pipelines[0].childValue,
			unit: formValues.unit,
		})
			.then(response => {
				setChangeFailureRate(response.changeFailureRate);
				setDeploymentFrequency(response.deploymentFrequency);
				setLeadTimeForChange(response.leadTimeForChange);
				setMeanTimeToRestore(response.meanTimeToRestore);
			})
			.finally(() => {
				setLoadingChart(false);
			});
	};

	return (
		<>
			<DashboardTopPanel
				onFormFinish={getFourKeyMetrics}
				dashboardId={dashboardId}
				onSyncBuildsSuccess={getFourKeyMetrics}
				onFormValuesChange={(_, values) => setFormValues(values)}
			/>
			<div css={metricsContainerStyles}>
				<Row gutter={28}>
					<Col xs={24} sm={24} md={24} lg={12}>
						<MetricsCard
							title="Deployment Frequency (Times)"
							summary={deploymentFrequency.summary}
							data={deploymentFrequency.details}
							yaxisFormatter={(value: string) => value}
							unit="Times"
							loading={loadingChart}
						/>
					</Col>

					<Col xs={24} sm={24} md={24} lg={12}>
						<MetricsCard
							title="Mean Lead Time for Change (Days)"
							summary={leadTimeForChange.summary}
							data={leadTimeForChange.details}
							yaxisFormatter={(value: string) => value}
							unit="Days"
							loading={loadingChart}
						/>
					</Col>

					<Col xs={24} sm={24} md={24} lg={12}>
						<MetricsCard
							title="Mean Time to Restore Service (Hours)"
							summary={meanTimeToRestore.summary}
							data={meanTimeToRestore.details}
							yaxisFormatter={(value: string) => value}
							unit="Hours"
							loading={loadingChart}
						/>
					</Col>

					<Col xs={24} sm={24} md={24} lg={12}>
						<MetricsCard
							title="Change Failure Rate"
							summary={changeFailureRate.summary}
							data={changeFailureRate.details}
							yaxisFormatter={(value: string) => value + "%"}
							unit="Percentage"
							loading={loadingChart}
						/>
					</Col>
				</Row>
			</div>
		</>
	);
};
