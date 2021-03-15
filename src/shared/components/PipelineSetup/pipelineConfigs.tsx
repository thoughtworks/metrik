import React from "react";

import { InfoCircleOutlined } from "@ant-design/icons";
import { ColProps, RowProps } from "antd/es/grid";
import { FormItemProps } from "antd/es/form";
import { PipelineTool } from "../../clients/pipelineApis";

interface PipelineConfig extends Pick<RowProps, "gutter"> {
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

export const BAMBOO_PIPELINE_CONFIG: PipelineConfig[] = [
	{
		gutter: 24,
		children: [
			{
				span: 8,
				name: "name",
				label: "Deployment Project Name",
				rules: [{ required: true, whitespace: true, message: "Please input pipeline name." }],
			},
			{
				span: 16,
				name: "url",
				label: "Deployment Project URL",
				placeholder: "e.g: http://bamboo_domain_name/deploy/viewDeploymentProjectEnvironments",
				tooltip: {
					icon: <InfoCircleOutlined />,
					title:
						"URL of the deployment plan. Please do ensure the URL is complete, including all information, such as project, subproject, plan keys.",
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
