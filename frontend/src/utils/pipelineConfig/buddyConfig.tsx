import React from "react";

import { InfoCircleOutlined } from "@ant-design/icons";
import { PipelineConfig } from "./jenkinsConfig";

export const BUDDY_CONFIG: PipelineConfig[] = [
	{
		gutter: 24,
		children: [
			{
				span: 8,
				name: "name",
				label: "Pipeline Name",
				rules: [
					{
						required: true,
						whitespace: true,
						message: "Please input pipeline name.",
					},
				],
			},
			{
				span: 16,
				name: "url",
				label: "Pipeline API Endpoint URL",
				placeholder: "e.g: https://api.buddy.works/workspaces/myws/projects/myprj/pipelines/123456",
				tooltip: {
					icon: <InfoCircleOutlined />,
					title:
						"URL of the Buddy API pipeline endpoint: https://<buddy_api_url>/workspaces/<workspace_name>/projects/<project_name>/pipelines/<pipeline_id>",
				},
				rules: [
					{
						required: true,
						whitespace: true,
						message: "Please input Buddy API pipeline endpoint URL.",
					},
				],
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
					title: "The Personal Access Token will be used to invoke Buddy APIs to fetch data.",
				},

				rules: [{ required: true, whitespace: true, message: "Please input access token." }],
			},
		],
	},
];
