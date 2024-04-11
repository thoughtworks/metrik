import { createRequest } from "./createRequest";
import { PipelineTool } from "../models/pipeline";

export const createPipelineUsingPost = createRequest<{
	projectId: string;
	pipeline: Pipeline;
}>(({ projectId, pipeline }) => ({
	url: `/api/project/${projectId}/pipeline`,
	method: "POST",
	data: pipeline,
	headers: { "Content-Type": "application/json" },
}));

export const updatePipelineUsingPut = createRequest<{
	projectId: string;
	pipeline: Pipeline;
}>(({ projectId, pipeline }) => ({
	url: `/api/project/${projectId}/pipeline/${pipeline.id}`,
	method: "PUT",
	data: pipeline,
	headers: { "Content-Type": "application/json" },
}));

export const deletePipelineUsingDelete = createRequest<{
	projectId: string;
	pipelineId: string;
}>(({ projectId, pipelineId }) => ({
	url: `/api/project/${projectId}/pipeline/${pipelineId}`,
	method: "DELETE",
}));

export const verifyPipelineUsingPost = createRequest<{
	verification: PipelineVerification;
}>(({ verification }) => ({
	url: `/api/pipeline/verify`,
	method: "POST",
	data: verification,
	headers: { "Content-Type": "application/json" },
}));

export const getPipelineStagesUsingGet = createRequest<
	{
		projectId: string;
	},
	PipelineStages[]
>(({ projectId }) => ({
	url: `/api/project/${projectId}/pipelines-stages`,
	method: "GET",
}));

export interface PipelineStages {
	pipelineId: string;
	pipelineName: string;
	stages: string[];
}

interface BasePipeline {
	id: string;
	type: PipelineTool;
	url: string;
	name: string;
	credential: string;
}

export interface JenkinsPipeline extends BasePipeline {
	type: PipelineTool.JENKINS;
	username: string;
}

export interface BambooPipeline extends BasePipeline {
	type: PipelineTool.BAMBOO;
}

export interface GithubActions extends BasePipeline {
	type: PipelineTool.GITHUB_ACTIONS;
}

export interface AzurePipelines extends BasePipeline {
	type: PipelineTool.AZURE_PIPELINES;
}

export interface BambooDeployedPipeline extends BasePipeline {
	type: PipelineTool.BAMBOO_DEPLOYMENT;
}

export type Pipeline =
	| JenkinsPipeline
	| BambooPipeline
	| GithubActions
	| AzurePipelines
	| BambooDeployedPipeline;

export type PipelineVerification =
	| Omit<JenkinsPipeline, "id" | "name">
	| Omit<BambooPipeline, "id" | "name">
	| Omit<GithubActions, "id" | "name">
	| Omit<AzurePipelines, "id" | "name">
	| Omit<BambooDeployedPipeline, "id" | "name">;
