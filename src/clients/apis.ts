import { createRequest } from "./createRequest";

export const pipelineVerify = createRequest<PipelineVerifyRequest>(requestParams => ({
	url: "/api/pipeline/verify",
	method: "GET",
	params: requestParams,
}));

export const pipelineConfig = createRequest<PipelineConfigRequest>(() => ({
	url: "/api/pipeline/config",
	method: "POST",
}));

interface PipelineVerifyRequest {
	url: string;
	username: string;
	token: string;
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
	token: string;
	type: string;
}
