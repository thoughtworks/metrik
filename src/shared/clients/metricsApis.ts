import { createRequest } from "./createRequest";

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
	summary: Metrics;
}

export interface Metrics {
	level: MetricsLevel;
	value?: number | "NaN";
	endTimestamp: number;
	startTimestamp: number;
}

export interface ValidMetric extends Metrics {
	value: number;
}

export enum MetricsLevel {
	ELITE = "ELITE",
	HIGH = "HIGH",
	INVALID = "INVALID",
	LOW = "LOW",
	MEDIUM = "MEDIUM",
}
