import React from "react";

import { InfoCircleOutlined } from "@ant-design/icons";
import { PipelineConfig } from "./jenkinsConfig";

export const BAMBOO_DEPLOYED_PIPELINE_CONFIG: PipelineConfig[] = [
	{
		gutter: 24,
		children: [
			{
				span: 8,
				name: "name",
				label: "Build Plan Name",
				rules: [{ required: true, whitespace: true, message: "Please input name of Build Plan." }],
			},
			{
				span: 16,
				name: "url",
				label: "Build Plan URL",
				placeholder: "e.g: http://bamboo_domain_name/browse/PROJECT-PLAN",
				tooltip: {
					icon: <InfoCircleOutlined />,
					title:
						"URL of the build plan. Please do ensure the URL is complete, including all information, such as project, subproject, plan keys.",
				},
				rules: [{ required: true, whitespace: true, message: "Please input pipeline URL." }],
			},
		],
	},
	{
		gutter: 24,
		children: [
			{
				span: 8,
				name: "credential",
				type: "password",
				label: "Access Token",
				tooltip: {
					icon: <InfoCircleOutlined />,
					title:
						"The access token will be used to invoke BambooPipeline REST APIs to fetch pipeline execution status. You can manage your tokens via BambooPipeline profile management page. Note the console password do not work here.",
				},

				rules: [{ required: true, whitespace: true, message: "Please input access token." }],
			},
		],
	},
];
