import { createRequest } from "./createRequest";
import { MetricsLevel } from "../__types__/enum";

export const getFourKeyMetricsUsingPost = createRequest<
	{
		metricsQuery: MetricsQueryRequest;
	},
	FourKeyMetrics
>(({ metricsQuery }) => ({
	method: "POST",
	url: `/api/pipeline/metrics`,
	data: metricsQuery,
	headers: { "Content-Type": "application/json" },
}));

export interface MetricsQueryRequest {
	pipelineStages: PipelineStageRequest[];
	unit: MetricsUnit;
	startTime: number;
	endTime: number;
}

export interface PipelineStageRequest {
	pipelineId: string;
	stage: string;
}

export enum MetricsUnit {
	FORTNIGHTLY = "Fortnightly",
	MONTHLY = "Monthly",
}

export interface FourKeyMetrics {
	changeFailureRate: MetricsInfo;
	deploymentFrequency: MetricsInfo;
	leadTimeForChange: MetricsInfo;
	meanTimeToRestore: MetricsInfo;
}

export interface MetricsInfo {
	details: Metrics[];
	summary: MetricsSummary;
}

export interface Metrics {
	value: number | "NaN" | undefined;
	endTimestamp: number;
	startTimestamp: number;
}
export interface MetricsSummary extends Metrics {
	level: MetricsLevel;
}
export interface ValidMetric extends Metrics {
	value: number;
}
