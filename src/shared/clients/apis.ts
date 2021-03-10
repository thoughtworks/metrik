import { createRequest } from "./createRequest";

export const createProjectUsingPost = createRequest<
	{
		requestBody: ProjectRequest;
	},
	Pick<ProjectDetailResponse, "id" | "name">
>("createProjectUsingPost", ({ requestBody }) => ({
	url: `/api/project`,
	method: "POST",
	data: requestBody,
	headers: { "Content-Type": "application/json" },
}));

export const deleteProjectUsingDelete = createRequest<{
	projectId: string;
}>("deleteProjectUsingDelete", ({ projectId }) => ({
	url: `/api/project/${projectId}`,
	method: "DELETE",
}));

export const deletePipelineUsingDelete = createRequest<{
	projectId: string;
	pipelineId: string;
}>("deletePipelineUsingDelete", ({ projectId, pipelineId }) => ({
	url: `/api/project/${projectId}/pipeline/${pipelineId}`,
	method: "DELETE",
}));

export const getProjectDetailsUsingGet = createRequest<
	{
		projectId: string;
	},
	ProjectDetailResponse
>("getProjectDetailsUsingGet", ({ projectId }) => ({
	url: `/api/project/${projectId}`,
	method: "GET",
}));

export const getProjectsUsingGet = createRequest<undefined, ProjectResponse[]>(
	"getProjectsUsingGet",
	() => ({
		url: `/api/project`,
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
		projectId: string;
	},
	SynchronizationRecordResponse
>("getLastSynchronizationUsingGet", ({ projectId }) => ({
	url: `/api/project/${projectId}/synchronization`,
	method: "GET",
}));

export const getPipelineStagesUsingGet = createRequest<
	{
		projectId: string;
	},
	PipelineStagesResponse[]
>("getPipelineStagesUsingGet", ({ projectId }) => ({
	url: `/api/project/${projectId}/pipelines-stages`,
	method: "GET",
}));

export const getPipelineUsingGet = createRequest<
	{
		projectId: string;
		pipelineId: string;
	},
	PipelineResponse
>("getPipelineUsingGet", ({ projectId, pipelineId }) => ({
	url: `/api/project/${projectId}/pipeline/${pipelineId}`,
	method: "GET",
}));

export const updateBuildsUsingPost = createRequest<
	{
		projectId: string;
	},
	SynchronizationRecordResponse
>("updateBuildsUsingPost", ({ projectId }) => ({
	url: `/api/project/${projectId}/synchronization`,
	method: "POST",
	headers: { "Content-Type": "application/json" },
}));

export const updateProjectNameUsingPut = createRequest<{
	projectId: string;
	requestBody: string;
}>("updateProjectNameUsingPut", ({ projectId, requestBody }) => ({
	url: `/api/project/${projectId}`,
	method: "PUT",
	data: requestBody,
	headers: { "Content-Type": "application/json" },
}));

export const createPipelineUsingPost = createRequest<{
	projectId: string;
	//TODO temporally add pipelineId here to match type of
	// updatePipelineUsingPut, can replace with better solutions in the future
	pipelineId: string;
	requestBody: PipelineRequest;
}>("createPipelineUsingPost", ({ projectId, requestBody }) => ({
	url: `/api/project/${projectId}/pipeline`,
	method: "POST",
	data: requestBody,
	headers: { "Content-Type": "application/json" },
}));

export const updatePipelineUsingPut = createRequest<{
	projectId: string;
	pipelineId: string;
	requestBody: PipelineRequest;
}>("updatePipelineUsingPut", ({ projectId, pipelineId, requestBody }) => ({
	url: `/api/project/${projectId}/pipeline/${pipelineId}`,
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

export interface ProjectDetailResponse {
	id: string;
	name: string;
	pipelines: PipelineResponse[];
	synchronizationTimestamp?: number;
}

export interface ProjectRequest {
	projectName: string;
	pipeline: PipelineRequest;
}

export interface ProjectResponse {
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
	username?: string;
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
	username?: string;
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
