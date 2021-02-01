import { createRequest } from "./createRequest";

export const createDashboardUsingPost = createRequest<
	{
		requestBody: DashboardRequest;
	},
	Dashboard
>("createDashboardUsingPost", ({ requestBody }) => ({
	url: `/api/dashboard`,
	method: "POST",
	data: requestBody,
	headers: { "Content-Type": "application/json" },
}));

export const createPipelineUsingPost = createRequest<
	{
		dashboardId: string;
		requestBody: PipelineRequest;
	},
	Dashboard
>("createPipelineUsingPost", ({ dashboardId, requestBody }) => ({
	url: `/api/dashboard/${dashboardId}/pipeline`,
	method: "POST",
	data: requestBody,
	headers: { "Content-Type": "application/json" },
}));

export const deleteDashboardUsingDelete = createRequest<{
	dashboardId: string;
}>("deleteDashboardUsingDelete", ({ dashboardId }) => ({
	url: `/api/dashboard/${dashboardId}`,
	method: "DELETE",
}));

export const deletePipelineUsingDelete = createRequest<{
	dashboardId: string;
	pipelineId: string;
}>("deletePipelineUsingDelete", ({ dashboardId, pipelineId }) => ({
	url: `/api/dashboard/${dashboardId}/pipeline/${pipelineId}`,
	method: "DELETE",
}));

export const getDashboardsUsingGet = createRequest<undefined, Dashboard[]>(
	"getDashboardsUsingGet",
	() => ({
		url: `/api/dashboard`,
		method: "GET",
	})
);

export const getDeploymentCountUsingGet = createRequest<
	{
		dashboardId: string;
		endTimestamp: number;
		pipelineId: string;
		startTimestamp: number;
		targetStage: string;
	},
	DeploymentFrequencyResponse
>(
	"getDeploymentCountUsingGet",
	({ dashboardId, endTimestamp, pipelineId, startTimestamp, targetStage }) => ({
		url: `/api/deployment-frequency`,
		method: "GET",
		params: {
			dashboardId,
			endTimestamp,
			pipelineId,
			startTimestamp,
			targetStage,
		},
	})
);

export const getFourKeyMetricsUsingGet = createRequest<
	{
		endTime: number;
		pipelineId: string;
		startTime: number;
		targetStage: string;
		unit: keyof typeof GetFourKeyMetricsUsingGetUnit;
	},
	FourKeyMetricsResponse
>("getFourKeyMetricsUsingGet", ({ endTime, pipelineId, startTime, targetStage, unit }) => ({
	url: `/api/pipeline/metrics`,
	method: "GET",
	params: {
		endTime,
		pipelineId,
		startTime,
		targetStage,
		unit,
	},
}));

export const getLastSynchronizationUsingGet = createRequest<
	{
		dashboardId: string;
	},
	SynchronizationRecordResponse
>("getLastSynchronizationUsingGet", ({ dashboardId }) => ({
	url: `/api/dashboard/${dashboardId}/synchronization`,
	method: "GET",
}));

export const getPipelineStagesUsingGet = createRequest<
	{
		dashboardId: string;
	},
	PipelineStagesResponse[]
>("getPipelineStagesUsingGet", ({ dashboardId }) => ({
	url: `/api/dashboard/${dashboardId}/stage`,
	method: "GET",
}));

export const getPipelineUsingGet = createRequest<
	{
		dashboardId: string;
		pipelineId: string;
	},
	Pipeline
>("getPipelineUsingGet", ({ dashboardId, pipelineId }) => ({
	url: `/api/dashboard/${dashboardId}/pipeline/${pipelineId}`,
	method: "GET",
}));

export const pullBuildsUsingPost = createRequest<
	{
		dashboardId: string;
		pipelineId: string;
	},
	Build[]
>("pullBuildsUsingPost", ({ dashboardId, pipelineId }) => ({
	url: `/api/dashboard/${dashboardId}/pipeline/${pipelineId}/builds`,
	method: "POST",
	headers: { "Content-Type": "application/json" },
}));

export const updateBuildsUsingPost = createRequest<
	{
		dashboardId: string;
	},
	SynchronizationRecordResponse
>("updateBuildsUsingPost", ({ dashboardId }) => ({
	url: `/api/dashboard/${dashboardId}/synchronization`,
	method: "POST",
	headers: { "Content-Type": "application/json" },
}));

export const updateDashboardNameUsingPut = createRequest<
	{
		dashboardId: string;
		requestBody: string;
	},
	Dashboard
>("updateDashboardNameUsingPut", ({ dashboardId, requestBody }) => ({
	url: `/api/dashboard/${dashboardId}`,
	method: "PUT",
	data: requestBody,
	headers: { "Content-Type": "application/json" },
}));

export const updatePipelineUsingPut = createRequest<
	{
		dashboardId: string;
		pipelineId: string;
		requestBody: PipelineRequest;
	},
	Pipeline
>("updatePipelineUsingPut", ({ dashboardId, pipelineId, requestBody }) => ({
	url: `/api/dashboard/${dashboardId}/pipeline/${pipelineId}`,
	method: "PUT",
	data: requestBody,
	headers: { "Content-Type": "application/json" },
}));

export const verifyPipelineUsingPost = createRequest<{
	requestBody: PipelineVerificationRequest;
}>("verifyPipelineUsingPost", ({ requestBody }) => ({
	url: `/api/pipeline/verify`,
	method: "POST",
	data: requestBody,
	headers: { "Content-Type": "application/json" },
}));

export interface Build {
	changeSets: Commit[];
	duration: number;
	number: number;
	pipelineId: string;
	result?: keyof typeof BuildResult;
	stages: Stage[];
	timestamp: number;
	url: string;
}

export enum BuildResult {
	"ABORTED" = "ABORTED",
	"CYCLE" = "CYCLE",
	"FAILURE" = "FAILURE",
	"NOT_BUILT" = "NOT_BUILT",
	"SUCCESS" = "SUCCESS",
	"UNSTABLE" = "UNSTABLE",
}

export interface Commit {
	commitId: string;
	date: string;
	msg: string;
	timestamp: number;
}

export interface Dashboard {
	id: string;
	name: string;
	pipelines: Pipeline[];
	synchronizationTimestamp?: number;
}

export interface DashboardRequest {
	dashboardName: string;
	pipelineRequest: PipelineRequest;
}

export interface DeploymentFrequencyResponse {
	deploymentCount: number;
}

export interface FourKeyMetricsResponse {
	changeFailureRate: MetricsInfo;
	leadTimeForChange: MetricsInfo;
	meanTimeToRestore: MetricsInfo;
}

export enum GetFourKeyMetricsUsingGetUnit {
	"Fortnightly" = "Fortnightly",
	"Monthly" = "Monthly",
}

export interface Metrics {
	endTimestamp: number;
	level?: keyof typeof MetricsLevel;
	startTimestamp: number;
	value: number;
}

export interface MetricsInfo {
	details: Metrics[];
	summary: Metrics;
}

export enum MetricsLevel {
	"ELITE" = "ELITE",
	"HIGH" = "HIGH",
	"INVALID" = "INVALID",
	"LOW" = "LOW",
	"MEDIUM" = "MEDIUM",
}

export interface Pipeline {
	credential: string;
	id: string;
	name: string;
	type: keyof typeof PipelineType;
	url: string;
	username: string;
}

export interface PipelineRequest {
	credential: string;
	name: string;
	type: keyof typeof PipelineRequestType;
	url: string;
	username: string;
}

export enum PipelineRequestType {
	"BAMBOO" = "BAMBOO",
	"JENKINS" = "JENKINS",
}

export interface PipelineStagesResponse {
	pipelineName: string;
	stages: string[];
}

export enum PipelineType {
	"BAMBOO" = "BAMBOO",
	"JENKINS" = "JENKINS",
}

export interface PipelineVerificationRequest {
	credential: string;
	type: keyof typeof PipelineVerificationRequestType;
	url: string;
	username: string;
}

export enum PipelineVerificationRequestType {
	"BAMBOO" = "BAMBOO",
	"JENKINS" = "JENKINS",
}

export interface Stage {
	durationMillis: number;
	name: string;
	pauseDurationMillis: number;
	stageDoneTime: number;
	startTimeMillis: number;
	status: keyof typeof StageStatus;
}

export enum StageStatus {
	"ABORTED" = "ABORTED",
	"FAILED" = "FAILED",
	"IN_PROGRESS" = "IN_PROGRESS",
	"SUCCESS" = "SUCCESS",
}

export interface SynchronizationRecordResponse {
	synchronizationTimestamp?: number;
}
