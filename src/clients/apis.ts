import { createRequest } from "./createRequest";

export const createDashboardAndPipelineUsingPost = createRequest<
	{
		requestBody: DashboardRequest;
	},
	DashboardDetailVo
>("createDashboardAndPipelineUsingPost", ({ requestBody }) => ({
	url: `/api/dashboard`,
	method: "POST",
	data: requestBody,
	headers: { "Content-Type": "application/json" },
}));

export const createPipelineUsingPost = createRequest<
	{
		dashboardId: string;
		requestBody: PipelineVoReq;
	},
	PipelineVoRes
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

export const getDashboardUsingGet = createRequest<
	{
		dashboardId: string;
	},
	DashboardVo
>("getDashboardUsingGet", ({ dashboardId }) => ({
	url: `/api/dashboard/${dashboardId}`,
	method: "GET",
}));

export const getDashboardsUsingGet = createRequest<undefined, DashboardVo[]>(
	"getDashboardsUsingGet",
	() => ({
		url: `/api/dashboard`,
		method: "GET",
	})
);

export const getDashboardDetailsUsingGet = createRequest<string, DashboardDetailVo>(
	"getDashboardDetailsUsingGet",
	(dashboardId: string) => ({
		url: `/api/dashboard/${dashboardId}`,
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
	method: "POST",
	data: {
		endTime,
		pipelineStages: [
			{
				pipelineId,
				stage: targetStage,
			},
		],
		startTime,
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
	url: `/api/dashboard/${dashboardId}/pipelines-stages`,
	method: "GET",
}));

export const getPipelineUsingGet = createRequest<
	{
		dashboardId: string;
		pipelineId: string;
	},
	PipelineVoRes
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

export const testUsingGet = createRequest<undefined, string>("testUsingGet", () => ({
	url: `//test`,
	method: "GET",
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
	DashboardVo
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
		requestBody: PipelineVoReq;
	},
	PipelineVoRes
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

export interface DashboardDetailVo {
	id: string;
	name: string;
	pipelines: PipelineVoRes[];
	synchronizationTimestamp?: number;
}

export interface DashboardRequest {
	dashboardName: string;
	pipeline: PipelineVoReq;
}

export interface DashboardVo {
	id: string;
	name: string;
	synchronizationTimestamp?: number;
}

export interface DeploymentFrequencyResponse {
	deploymentCount: number;
}

export interface FourKeyMetricsResponse {
	changeFailureRate: MetricsInfo;
	deploymentFrequency: MetricsInfo;
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
	ELITE = "ELITE",
	HIGH = "HIGH",
	MEDIUM = "MEDIUM",
	LOW = "LOW",
	INVALID = "INVALID",
}

export interface PipelineStagesResponse {
	pipelineId: string;
	pipelineName: string;
	stages: string[];
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

export interface PipelineVoReq {
	credential?: string;
	id?: string;
	name?: string;
	type?: keyof typeof PipelineVoReqType;
	url?: string;
	username?: string;
}

export enum PipelineVoReqType {
	"BAMBOO" = "BAMBOO",
	"JENKINS" = "JENKINS",
}

export interface PipelineVoRes {
	credential: string;
	id: string;
	name: string;
	type: keyof typeof PipelineVoResType;
	url: string;
	username: string;
}

export enum PipelineVoResType {
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
