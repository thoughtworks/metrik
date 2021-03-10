import { PipelineTool } from "../clients/pipelineApis";

export const TOOLTIP_MAPPING = {
	[PipelineTool.JENKINS]: {
		URL:
			"URL of the pipeline. Please do ensure the URL is complete, including the folder/subfolder information if there’s any.",
		USERNAME: "Username used to access Jenkins.",
		CREDENTIAL:
			"The access token will be used to invoke Jenkins APIs to fetch pipeline execution status.The regular password for the Jenkins UI also works here, though not recommended.",
	},
	[PipelineTool.BAMBOO]: {
		URL:
			"URL of the pipeline. Please do ensure the URL is complete, including all information such as project, subproject, plan keys.",
		CREDENTIAL:
			"The access token will be used to invoke Bamboo REST APIs to fetch pipeline execution status. You can manage your tokens via Bamboo profile management page. Note the console password do not work here.",
	},
};
