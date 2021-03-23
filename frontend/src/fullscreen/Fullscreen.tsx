import React, { useState, KeyboardEvent } from "react";
import { ChartData, FullscreenMetricsCardOptions } from "./components/FullscreenMetricsCard";
import { MetricsLevel, MetricsType } from "../shared/__types__/enum";
import FullscreenDashboard from "./components/FullscreenDashboard";
import { Metrics } from "../shared/clients/metricsApis";

const FullScreen = () => {
	const [isFullscreenVisible, setIsPopoverVisible] = useState(false);
	const changeFailureDataBaseValue = 10;
	const meanTimeToRestoreBaseValue = 5;
	const leadTimeForChangeBaseValue = 0.2;
	const deploymentFrequencyBaseValue = 5;

	const meanTimeToRestore: Metrics[] = [
		{
			value: undefined,
			startTimestamp: 1605974400000,
			endTimestamp: 1606751999999,
		},
		{
			value: undefined,
			startTimestamp: 1606752000000,
			endTimestamp: 1607961599999,
		},
		{ value: undefined, startTimestamp: 1607961600000, endTimestamp: 1609171199999 },
		{ value: undefined, startTimestamp: 1609171200000, endTimestamp: 1610380799999 },
		{
			value: 90 + meanTimeToRestoreBaseValue,
			startTimestamp: 1610380800000,
			endTimestamp: 1611590399999,
		},
		{
			value: 100.0 + meanTimeToRestoreBaseValue,
			startTimestamp: 1611590400000,
			endTimestamp: 1612799999999,
		},
		{
			value: 100.0 + meanTimeToRestoreBaseValue,
			startTimestamp: 1612800000000,
			endTimestamp: 1614009599999,
		},
		{
			value: 90 + meanTimeToRestoreBaseValue,
			startTimestamp: 1614009600000,
			endTimestamp: 1615219199999,
		},
		{
			value: 90 + meanTimeToRestoreBaseValue,
			startTimestamp: 1615219200000,
			endTimestamp: 1616428799000,
		},
	];
	const changeFailureData = [
		{
			value: undefined,
			startTimestamp: 1605974400000,
			endTimestamp: 1606751999999,
		},
		{
			value: undefined,
			startTimestamp: 1606752000000,
			endTimestamp: 1607961599999,
		},
		{ value: undefined, startTimestamp: 1607961600000, endTimestamp: 1609171199999 },
		{ value: undefined, startTimestamp: 1609171200000, endTimestamp: 1610380799999 },
		{
			value: 0.4 + changeFailureDataBaseValue,
			startTimestamp: 1611590400000,
			endTimestamp: 1612799999999,
		},
		{
			value: 23.27 + changeFailureDataBaseValue,
			startTimestamp: 1612800000000,
			endTimestamp: 1614009599999,
		},
	];
	const leadTimeForChangeData = [
		{
			value: undefined,
			startTimestamp: 1611590400000,
			endTimestamp: 1612799999999,
		},
		{
			value: undefined,
			startTimestamp: 1611590400000,
			endTimestamp: 1612799999999,
		},
		{
			value: 0.4 + leadTimeForChangeBaseValue,
			startTimestamp: 1611590400000,
			endTimestamp: 1612799999999,
		},
		{
			value: undefined,
			startTimestamp: 1611590400000,
			endTimestamp: 1612799999999,
		},
		{
			value: undefined,
			startTimestamp: 1611590400000,
			endTimestamp: 1612799999999,
		},
		{
			value: 0.2 + leadTimeForChangeBaseValue,
			startTimestamp: 1612800000000,
			endTimestamp: 1614009599999,
		},
	];
	const deploymentFrequencyData = [
		{
			value: 10 + deploymentFrequencyBaseValue,
			startTimestamp: 1611590400000,
			endTimestamp: 1612799999999,
		},
		{
			value: undefined,
			startTimestamp: 1611590400000,
			endTimestamp: 1612799999999,
		},
		{
			value: 0.1 + deploymentFrequencyBaseValue,
			startTimestamp: 1611590400000,
			endTimestamp: 1612799999999,
		},
		{
			value: undefined,
			startTimestamp: 1611590400000,
			endTimestamp: 1612799999999,
		},
		{
			value: 0.1 + deploymentFrequencyBaseValue,
			startTimestamp: 1611590400000,
			endTimestamp: 1612799999999,
		},
		{
			value: 0.1 + deploymentFrequencyBaseValue,
			startTimestamp: 1612800000000,
			endTimestamp: 1614009599999,
		},
	];
	const metricsList: FullscreenMetricsCardOptions[] = [
		{
			metricsSummaryData: 32.31,
			metricsLevel: MetricsLevel.ELITE,
			metricsDataLabel: "AVG/Times / Fortnight",
			metricsText: MetricsType.DEPLOYMENT_FREQUENCY,
			data: deploymentFrequencyData,
		},
		{
			metricsSummaryData: 31.1,
			metricsLevel: MetricsLevel.LOW,
			metricsDataLabel: "AVG Days",
			metricsText: MetricsType.LEAD_TIME_FOR_CHANGE,
			data: leadTimeForChangeData,
		},
		{
			metricsSummaryData: 3.31,
			metricsLevel: MetricsLevel.HIGH,
			metricsDataLabel: "AVG Hours",
			metricsText: MetricsType.MEAN_TIME_TO_RESTORE,
			data: meanTimeToRestore,
		},
		{
			metricsSummaryData: 0.43,
			metricsLevel: MetricsLevel.MEDIUM,
			metricsDataLabel: "AVG%",
			metricsText: MetricsType.CHANGE_FAILURE_RATE,
			data: changeFailureData,
		},
	];

	const projectName = "My Project";
	const pipelineList = [
		"2km: 0km-dev",
		"2km: 1km-cdfdf",
		"2km: 2km-dev",
		"2km: 3km-dev-rheuerrrrr",
		"2km: 4km",
		"2km: 4km",
		"2km: 4km",
		"2km: 4km",
	];
	const showFullscreen = () => {
		setIsPopoverVisible(true);
	};
	const hideFullscreen = (event: KeyboardEvent<HTMLElement>) => {
		if (event.key === "Escape") {
			setIsPopoverVisible(false);
		}
	};
	return (
		<section onKeyUp={event => hideFullscreen(event)}>
			<button onClick={showFullscreen}>click me</button>
			<FullscreenDashboard
				projectName={projectName}
				metricsList={metricsList}
				startTimestamp={1615974249118}
				endTimestamp={1615974249118}
				pipelineList={pipelineList}
				isFullscreenVisible={isFullscreenVisible}
			/>
		</section>
	);
};
export default FullScreen;
