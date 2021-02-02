import React, { useState } from "react";
import { Divider, Form, Layout, Steps, Typography } from "antd";
import { FieldsStep1 } from "./components/FieldsStep1";
import { FieldsStep2 } from "./components/FieldsStep2";
import { ConfigStep, VerifyStatus } from "../__types__/base";
import ConfigSuccess from "./components/ConfigSuccess";
import { createDashboardUsingPost, Dashboard, verifyPipelineUsingPost } from "../clients/apis";

const { Text, Paragraph } = Typography;
const { Step } = Steps;

type PipelineToolType = "JENKINS";

interface ConfigFormValues {
	dashboardName: string;
	pipelineName: string;
	pipelineTool: PipelineToolType;
	pipelineDomain: string;
	username: string;
	credential: string;
}

export const PageConfig = () => {
	const [form] = Form.useForm();
	const [verifyStatus, setVerifyStatus] = useState<VerifyStatus>(VerifyStatus.DEFAULT);
	const [currentStep, setCurrentStep] = useState<ConfigStep>(ConfigStep.CREATE_DASHBOARD);
	const [dashboard, setDashboard] = useState<Dashboard>();
	const onFinish = async (values: ConfigFormValues) => {
		await verifyPipeline();
		const response = await createDashboardUsingPost({
			requestBody: {
				dashboardName: values.dashboardName,
				pipelineRequest: {
					name: values.pipelineName,
					url: values.pipelineDomain,
					username: values.username,
					credential: values.credential,
					type: values.pipelineTool,
				},
			},
		});
		setDashboard(response);
		toNextStep();
	};

	const toNextStep = () => {
		setCurrentStep(currentStep + 1);
	};

	const toPrevStep = () => {
		setCurrentStep(currentStep - 1);
	};

	const verifyPipeline = () => {
		const { credential, username, pipelineTool, pipelineDomain } = form.getFieldsValue();
		return verifyPipelineUsingPost({
			requestBody: {
				url: pipelineDomain,
				credential,
				username,
				type: pipelineTool,
			},
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
				<div css={{ width: 896, margin: "24px auto", padding: 24, background: "#fff" }}>
					{currentStep !== ConfigStep.CONFIG_SUCCESS ? (
						<div>
							<Steps current={currentStep} css={{ margin: "44px 0" }}>
								<Step title="Create Dashboard" />
								<Step title="Config Project" />
								<Step title="Success" />
							</Steps>
							<div css={{ width: 440, marginBottom: 32 }}>
								<Text type={"secondary"}>Instructions:</Text>
								{currentStep === ConfigStep.CREATE_DASHBOARD && (
									<Paragraph type={"secondary"}>Please Input your dashboard name</Paragraph>
								)}
								{currentStep === ConfigStep.CONFIG_PIPELINE && (
									<Paragraph type={"secondary"}>
										The 4 key metrics is displayed based on PIPELINE and GIT data. Please configure
										the project data source you want to detect here.
									</Paragraph>
								)}
							</div>
							<Divider />

							<Form
								layout="vertical"
								onFinish={onFinish}
								form={form}
								initialValues={{ pipelineTool: "JENKINS" }}>
								{formValues => (
									<>
										<FieldsStep1
											onNext={toNextStep}
											visible={currentStep === ConfigStep.CREATE_DASHBOARD}
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
						<ConfigSuccess dashboard={dashboard!} />
					)}
				</div>
			</Layout.Content>
		</Layout>
	);
};
