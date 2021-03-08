import React, { useState } from "react";
import { Form, Layout, Steps, Typography } from "antd";
import { FieldsStep1 } from "./components/FieldsStep1";
import { FieldsStep2 } from "./components/FieldsStep2";
import { ConfigStep, VerifyStatus } from "../shared/__types__/base";
import ConfigSuccess from "./components/ConfigSuccess";
import {
	createProjectUsingPost,
	PipelineResponse,
	verifyPipelineUsingPost,
} from "../shared/clients/apis";

const { Paragraph } = Typography;
const { Step } = Steps;

export interface ConfigFormValues extends PipelineResponse {
	projectName: string;
}

export const PageConfig = () => {
	const [form] = Form.useForm<ConfigFormValues>();

	const [verifyStatus, setVerifyStatus] = useState<VerifyStatus>(VerifyStatus.DEFAULT);
	const [currentStep, setCurrentStep] = useState<ConfigStep>(ConfigStep.CREATE_PROJECT);
	const [projectId, setProjectId] = useState<string>();
	const onFinish = async ({ projectName, ...pipeline }: ConfigFormValues) => {
		await verifyPipeline();
		const { id } = await createProjectUsingPost({
			requestBody: {
				projectName,
				pipeline,
			},
		});
		setProjectId(id);
		toNextStep();
	};

	const toNextStep = () => {
		setCurrentStep(currentStep + 1);
	};

	const toPrevStep = () => {
		setCurrentStep(currentStep - 1);
	};

	const verifyPipeline = () => {
		// eslint-disable-next-line @typescript-eslint/no-unused-vars
		const { projectName, ...pipelineValues } = form.getFieldsValue();
		return verifyPipelineUsingPost({
			requestBody: pipelineValues,
		})
			.then(() => {
				setVerifyStatus(VerifyStatus.SUCCESS);
			})
			.catch(error => {
				setVerifyStatus(VerifyStatus.Fail);
				throw new Error(error);
			});
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
					{currentStep !== ConfigStep.CONFIG_SUCCESS ? (
						<div>
							<Steps current={currentStep} css={{ margin: "44px 0" }}>
								<Step title="Create Project" />
								<Step title="Config Pipeline" />
								<Step title="Success" />
							</Steps>
							<div css={{ width: 620, marginBottom: 56 }}>
								{currentStep === ConfigStep.CREATE_PROJECT && (
									<Paragraph
										type={"secondary"}
										css={{ "&.ant-typography-secondary": { color: "black" } }}>
										Please input your project name below. The project name will be shown as the
										dashboard title.
									</Paragraph>
								)}
								{currentStep === ConfigStep.CONFIG_PIPELINE && (
									<Paragraph
										type={"secondary"}
										css={{ "&.ant-typography-secondary": { color: "black" } }}>
										Please note that the 4 key metrics data is calculated based on data collected
										from your pipelines. Input your pipeline details below to configure the
										dashboard data source.
									</Paragraph>
								)}
							</div>

							<Form
								layout="vertical"
								onFinish={onFinish}
								form={form}
								initialValues={{ type: "JENKINS" }}>
								{(formValues: ConfigFormValues) => (
									<>
										<FieldsStep1
											onNext={toNextStep}
											visible={currentStep === ConfigStep.CREATE_PROJECT}
											formValues={formValues}
										/>
										<FieldsStep2
											onBack={toPrevStep}
											formValues={formValues}
											visible={currentStep === ConfigStep.CONFIG_PIPELINE}
											verifyStatus={verifyStatus}
											onVerify={verifyPipeline}
										/>
									</>
								)}
							</Form>
						</div>
					) : (
						// eslint-disable-next-line @typescript-eslint/no-non-null-assertion
						<ConfigSuccess projectId={projectId!} />
					)}
				</div>
			</Layout.Content>
		</Layout>
	);
};
