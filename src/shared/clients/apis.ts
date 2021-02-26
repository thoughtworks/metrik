import { createRequest } from "./createRequest";

export const createDashboardUsingPost = createRequest<
	{
		requestBody: DashboardRequest;
	},
	DashboardDetailResponse
>("createDashboardUsingPost", ({ requestBody }) => ({
	url: `/api/dashboard`,
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

export const getDashboardDetailsUsingGet = createRequest<
	{
		dashboardId: string;
	},
	DashboardDetailResponse
>("getDashboardDetailsUsingGet", ({ dashboardId }) => ({
	url: `/api/dashboard/${dashboardId}`,
	method: "GET",
}));

export const getDashboardsUsingGet = createRequest<undefined, DashboardResponse[]>(
	"getDashboardsUsingGet",
	() => ({
		url: `/api/dashboard`,
		method: "GET",
	})
);

export const getFourKeyMetricsUsingPost = createRequest<
	{
		requestBody: MetricsQueryRequest;
	},
	FourKeyMetricsResponse
>("getFourKeyMetricsUsingPost", ({ requestBody }) => ({
	url: `/api/pipeline/metrics`,
	method: "POST",
	data: requestBody,
	headers: { "Content-Type": "application/json" },
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
	PipelineResponse
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
	url: `/test`,
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
	DashboardResponse
>("updateDashboardNameUsingPut", ({ dashboardId, requestBody }) => ({
	url: `/api/dashboard/${dashboardId}`,
	method: "PUT",
	data: requestBody,
	headers: { "Content-Type": "application/json" },
}));

export const createPipelineUsingPost = createRequest<
	{
		dashboardId: string;
		//TODO temporally add pipelineId here to match type of
		// updatePipelineUsingPut, can replace with better solutions in the future
		pipelineId: string;
		requestBody: PipelineRequest;
	},
	PipelineResponse
>("createPipelineUsingPost", ({ dashboardId, requestBody }) => ({
	url: `/api/dashboard/${dashboardId}/pipeline`,
	method: "POST",
	data: requestBody,
	headers: { "Content-Type": "application/json" },
}));

export const updatePipelineUsingPut = createRequest<
	{
		dashboardId: string;
		pipelineId: string;
		requestBody: PipelineRequest;
	},
	PipelineResponse
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

export interface DashboardDetailResponse {
	id: string;
	name: string;
	pipelines: PipelineResponse[];
	synchronizationTimestamp?: number;
}

export interface DashboardRequest {
	dashboardName: string;
	pipeline: PipelineRequest;
}

export interface DashboardResponse {
	id: string;
	name: string;
	synchronizationTimestamp?: number;
}

export interface FourKeyMetricsResponse {
	changeFailureRate: MetricsInfo;
	deploymentFrequency: MetricsInfo;
	leadTimeForChange: MetricsInfo;
	meanTimeToRestore: MetricsInfo;
}

export interface Metrics {
	endTimestamp: number;
	level?: keyof typeof MetricsLevel;
	startTimestamp: number;
	value?: number | "NaN";
}

export interface ValidMetric extends Metrics {
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

export interface MetricsQueryRequest {
	endTime?: number;
	pipelineStages: PipelineStageRequest[];
	startTime?: number;
	unit: keyof typeof MetricsQueryRequestUnit;
}

export enum MetricsQueryRequestUnit {
	"Fortnightly" = "Fortnightly",
	"Monthly" = "Monthly",
}

export interface Number {
	[key: string]: any;
}

export interface PipelineRequest {
	credential?: string;
	name?: string;
	type?: keyof typeof PipelineRequestType;
	url?: string;
	username?: string;
}

export enum PipelineRequestType {
	"BAMBOO" = "BAMBOO",
	"JENKINS" = "JENKINS",
}

export interface PipelineResponse {
	credential: string;
	id: string;
	name: string;
	type: keyof typeof PipelineResponseType;
	url: string;
	username: string;
}

export enum PipelineResponseType {
	"BAMBOO" = "BAMBOO",
	"JENKINS" = "JENKINS",
}

export interface PipelineStageRequest {
	pipelineId: string;
	stage: string;
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
	"PAUSED_PENDING_INPUT" = "PAUSED_PENDING_INPUT",
	"SUCCESS" = "SUCCESS",
}

export interface SynchronizationRecordResponse {
	synchronizationTimestamp?: number;
}
