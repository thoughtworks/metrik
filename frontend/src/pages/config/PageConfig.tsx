import React, { useState } from "react";
import { Layout, Steps, Typography } from "antd";
import ConfigSuccess from "./components/ConfigSuccess";
import ProjectNameSetup from "./components/ProjectNameSetup";
import PipelineSetup, { FormValues } from "../../components/PipelineSetup/PipelineSetup";
import { BaseProject, createProjectUsingPost } from "../../clients/projectApis";
import { ConfigStep } from "../../models/common";

const { Paragraph } = Typography;
const { Step } = Steps;

export const PageConfig = () => {
	const [project, setProject] = useState<BaseProject>({ id: "", name: "" });
	const [step, setStep] = useState<ConfigStep>(ConfigStep.CREATE_PROJECT);

	const toNextStep = (updated: Partial<BaseProject>) => {
		setProject({ ...project, ...updated });
		setStep(step + 1);
	};

	const toPrevStep = () => {
		setStep(step - 1);
	};

	const onCreateProject = async (values: FormValues) => {
		const { id, name } = await createProjectUsingPost({
			projectName: project.name,
			pipeline: values,
		});

		toNextStep({ id, name });
	};

	return (
		<Layout style={{ minHeight: "100vh" }}>
			<Layout.Content>
				<div
					css={{
						width: 896,
						minHeight: 500,
						margin: "24px auto",
						padding: 24,
						background: "#fff",
					}}>
					{step !== ConfigStep.CONFIG_SUCCESS ? (
						<div>
							<Steps current={step} css={{ margin: "44px 0" }}>
								<Step title="Create Project" />
								<Step title="Config Pipeline" />
								<Step title="Success" />
							</Steps>
							<div css={{ width: 620, marginBottom: 56 }}>
								{step === ConfigStep.CREATE_PROJECT && (
									<Paragraph
										type={"secondary"}
										css={{ "&.ant-typography-secondary": { color: "black" } }}>
										Please input your project name below. The project name will be shown as the
										dashboard title.
									</Paragraph>
								)}
								{step === ConfigStep.CONFIG_PIPELINE && (
									<Paragraph
										type={"secondary"}
										css={{ "&.ant-typography-secondary": { color: "black" } }}>
										Please note that the 4 key metrics data is calculated based on data collected
										from your pipelines. Input your pipeline details below to configure the
										dashboard data source.
									</Paragraph>
								)}
							</div>

							<ProjectNameSetup visible={step === ConfigStep.CREATE_PROJECT} onNext={toNextStep} />
							<PipelineSetup
								visible={step === ConfigStep.CONFIG_PIPELINE}
								onSubmit={onCreateProject}
								onBack={toPrevStep}
							/>
						</div>
					) : (
						<ConfigSuccess projectId={project.id} />
					)}
				</div>
			</Layout.Content>
		</Layout>
	);
};
