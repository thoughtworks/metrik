import React from "react";

import { InfoCircleOutlined } from "@ant-design/icons";
import { PipelineConfig } from "./jenkinsConfig";

export const AZURE_PIPELINES_CONFIG: PipelineConfig[] = [
	{
		gutter: 24,
		children: [
			{
				span: 16,
				name: "url",
				label: "Project URL",
				placeholder: "e.g: https://dev.azure.com/JetstarAirways/Raven",
				tooltip: {
					icon: <InfoCircleOutlined />,
					title:
						'URL of the project. Please ensure the URL is complete and including the organization/project name. e.g. "https://dev.azure.com/{organization}/{project}"',
				},
				rules: [{ required: true, whitespace: true, message: "Please input the project URL." }],
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
				label: "Personal Access Token",
				tooltip: {
					icon: <InfoCircleOutlined />,
					title:
						"The PAT (Personal Access Token) will be used to invoke Azure Pipeline APIs to fetch pipeline run status. Tokens can be narrowly scoped to allow only the read access to project and pipeline. Don't know how to manage tokens? Go to: https://learn.microsoft.com/en-us/azure/devops/organizations/accounts/use-personal-access-tokens-to-authenticate",
				},

				rules: [{ required: true, whitespace: true, message: "Please input access token." }],
			},
		],
	},
];
