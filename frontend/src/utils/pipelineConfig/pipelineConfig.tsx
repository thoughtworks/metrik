import React from "react";
import { JENKINS_PIPELINE_CONFIG } from "./jenkinsConfig";
import { BAMBOO_PIPELINE_CONFIG } from "./bambooConfig";
import { PipelineTool } from "../../models/pipeline";

export const PIPELINE_CONFIG = {
	[PipelineTool.JENKINS]: JENKINS_PIPELINE_CONFIG,
	[PipelineTool.BAMBOO]: BAMBOO_PIPELINE_CONFIG,
};

export const PIPELINE_TYPE_NOTE = {
	[PipelineTool.JENKINS]: undefined,
	[PipelineTool.BAMBOO]: (
		<div css={{ color: "rgba(0,0,0,0.25)", whiteSpace: "normal", marginBottom: 50 }}>
			Note: Deployment data is ought to be collected from Bamboo &quot;Build Plans&quot; and/or
			&quot;Deployment Projects&quot;. All you have to provide here is the URL of your &quot;Build
			Plan&quot; and the tool can find all associated deployment projects for you automatically.
			Struggle with the terms? More details please refer to:{" "}
			<a
				href={"https://confluence.atlassian.com/bamboo0700/deployment-projects-1014682237.html"}
				css={{
					textDecoration: "underline",
					"&:hover": { textDecoration: "underline" },
				}}>
				https://confluence.atlassian.com/bamboo0700/deployment-projects-1014682237.html
			</a>
		</div>
	),
};
