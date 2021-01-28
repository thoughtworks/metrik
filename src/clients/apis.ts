import { createRequest } from "./createRequest";

export const pipelineVerify = createRequest<PipelineVerifyRequest>(requestParams => ({
	url: "/api/pipeline/verify",
	method: "POST",
	data: requestParams,
}));

export const postPipelineConfig = createRequest<PipelineConfigRequest>(pipelineConfig => ({
	url: "/api/pipeline/config",
	method: "POST",
	data: pipelineConfig,
}));

interface PipelineVerifyRequest {
	url: string;
	username: string;
	credential: string;
	type: "JENKINS";
}

interface PipelineConfigRequest {
	dashboardName: string;
	pipeline: PipelineConfigurationVo;
}

interface PipelineConfigurationVo {
	name: string;
	url: string;
	username: string;
	credential: string;
	type: string;
}

export interface DashboardResponse {
	id: string;
	name: string;
	pipelineConfigurations: PipelineConfiguration[];
}

export interface PipelineConfiguration {
	id: string;
	name: string;
	lastUpdateTime: number;
	username?: string;
	credential?: string;
	type: string;
	url: string;
}
