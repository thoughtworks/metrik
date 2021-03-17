import React from "react";

import { InfoCircleOutlined } from "@ant-design/icons";
import { ColProps, RowProps } from "antd/es/grid";
import { FormItemProps } from "antd/es/form";

export interface PipelineConfig extends Pick<RowProps, "gutter"> {
	children: Array<
		FormItemProps & Pick<ColProps, "span"> & { type?: "text" | "password"; placeholder?: string }
	>;
}

export const JENKINS_PIPELINE_CONFIG: PipelineConfig[] = [
	{
		gutter: 24,
		children: [
			{
				span: 8,
				name: "name",
				label: "Pipeline Name",
				rules: [{ required: true, whitespace: true, message: "Please input pipeline name." }],
			},
			{
				span: 16,
				name: "url",
				label: "Pipeline URL",
				placeholder: "e.g: http://jenkins_domain_name/job/folder_name/job/job_name/",
				tooltip: {
					icon: <InfoCircleOutlined />,
					title:
						"URL of the pipeline. Please do ensure the URL is complete, including the folder/subfolder information if there’s any.",
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
				name: "username",
				label: "Username",
				tooltip: { icon: <InfoCircleOutlined />, title: "Username used to access Jenkins." },
				rules: [{ required: true, whitespace: true, message: "Please input username." }],
			},
			{
				span: 8,
				name: "credential",
				type: "password",
				label: "Access Token",
				tooltip: {
					icon: <InfoCircleOutlined />,
					title:
						"The access token will be used to invoke Jenkins APIs to fetch pipeline execution status.The regular password for the Jenkins UI also works here, though not recommended.",
				},

				rules: [{ required: true, whitespace: true, message: "Please input access token." }],
			},
		],
	},
];
