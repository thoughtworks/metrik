import { createRequest } from "./createRequest";
import { Pipeline } from "./pipelineApis";

export const updateProjectNameUsingPut = createRequest<{
	projectId: string;
	projectName: string;
}>(({ projectId, projectName }) => ({
	url: `/api/project/${projectId}`,
	method: "PUT",
	data: {
		projectName
	},
	headers: { "Content-Type": "application/json" },
}));

export const createProjectUsingPost = createRequest<
	{
		projectName: string;
		pipeline: Omit<Pipeline, "id">;
	},
	BaseProject
>(project => ({
	url: `/api/project`,
	method: "POST",
	data: project,
	headers: { "Content-Type": "application/json" },
}));

export const getProjectDetailsUsingGet = createRequest<
	{
		projectId: string;
	},
	Project
>(({ projectId }) => ({
	url: `/api/project/${projectId}`,
	method: "GET",
}));

export const getProjectsUsingGet = createRequest<undefined, Omit<Project, "pipelines">[]>(() => ({
	url: `/api/project`,
	method: "GET",
}));

export const getLastSynchronizationUsingGet = createRequest<
	{
		projectId: string;
	},
	Pick<Project, "synchronizationTimestamp">
>(({ projectId }) => ({
	url: `/api/project/${projectId}/synchronization`,
	method: "GET",
}));

export const updateBuildsUsingPost = createRequest<
	{
		projectId: string;
	},
	Pick<Project, "synchronizationTimestamp">
>(({ projectId }) => ({
	url: `/api/project/${projectId}/synchronization`,
	method: "POST",
	headers: { "Content-Type": "application/json" },
}));

export interface Project {
	id: string;
	name: string;
	synchronizationTimestamp: number;
	pipelines: Pipeline[];
}

export type BaseProject = Pick<Project, "id" | "name">;
