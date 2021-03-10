import { createRequest } from "./createRequest";

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

export interface Pipeline {
	id: string;
	type: PipelineTool;
	url: string;
	name: string;
	username?: string;
	credential: string;
}

export type PipelineVerification = Omit<Pipeline, "id" | "name">;

export enum PipelineTool {
	BAMBOO = "BAMBOO",
	JENKINS = "JENKINS",
}

export interface PipelineStages {
	pipelineId: string;
	pipelineName: string;
	stages: string[];
}
