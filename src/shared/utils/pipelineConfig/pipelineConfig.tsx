import React from "react";
import { PipelineTool } from "../../clients/pipelineApis";
import { JENKINS_PIPELINE_CONFIG } from "./jenkinsConfig";
import { BAMBOO_PIPELINE_CONFIG } from "./bambooConfig";

export const PIPELINE_CONFIG = {
	[PipelineTool.JENKINS]: JENKINS_PIPELINE_CONFIG,
	[PipelineTool.BAMBOO]: BAMBOO_PIPELINE_CONFIG,
};

export const PIPELINE_TYPE_NOTE = {
	[PipelineTool.JENKINS]: undefined,
	[PipelineTool.BAMBOO]: (
		<div css={{ color: "rgba(0,0,0,0.25)", marginBottom: 50 }}>
			Note: Deployment data is ought to be collected from Bamboo “deployment projects”. For now the
			4km tool does not support deployment data from “build plans”. More details please refer to:
			<a
				href={"https://confluence.atlassian.com/bamboo0700/deployment-projects-1014682237.html"}
				css={{ display: "inline-block", color: "rgba(0,0,0,0.25)" }}>
				https://confluence.atlassian.com/bamboo0700/deployment-projects-1014682237.html
			</a>
		</div>
	),
};
