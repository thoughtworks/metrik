import React from "react";

import { InfoCircleOutlined } from "@ant-design/icons";
import { PipelineConfig } from "./jenkinsConfig";

export const GITHUB_ACTIONS_CONFIG: PipelineConfig[] = [
	{
		gutter: 24,
		children: [
			{
				span: 8,
				name: "name",
				label: "Repository Name",
				rules: [
					{
						required: true,
						whitespace: true,
						message: "Please input name of your repository on Github.",
					},
				],
			},
			{
				span: 16,
				name: "url",
				label: "Repository URL",
				placeholder: "e.g: https://github.com/thoughtworks/metrik",
				tooltip: {
					icon: <InfoCircleOutlined />,
					title:
						'URL of the repository. Please ensure the URL is complete and including the owner/repo name. e.g. "https://github.com/my_organization/repository_name"',
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
				label: "Personal Access Token",
				tooltip: {
					icon: <InfoCircleOutlined />,
					title:
						"The PAC (Personal Access Token) will be used to invoke Github APIs to fetch workflow run status. Tokens can be narrowly scoped to allow only the read access to repo and workflow. Don't know how to manage tokens? Go to: https://github.com/settings/tokens",
				},

				rules: [{ required: true, whitespace: true, message: "Please input access token." }],
			},
		],
	},
];
