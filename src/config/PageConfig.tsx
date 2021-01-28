import React, { useState } from "react";
import { Divider, Form, Layout, Steps, Typography } from "antd";
import { FieldsStep1 } from "./components/FieldsStep1";
import { FieldsStep2 } from "./components/FieldsStep2";
import { ConfigStep, VerifyStatus } from "../__types__/base";
import ConfigSuccess from "./components/ConfigSuccess";
import { pipelineVerify, postPipelineConfig } from "../clients/apis";

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
	const onFinish = async (values: ConfigFormValues) => {
		await verifyPipeline();
		await postPipelineConfig({
			dashboardName: values.dashboardName,
			pipeline: {
				name: values.pipelineName,
				url: values.pipelineDomain,
				username: values.username,
				credential: values.credential,
				type: values.pipelineTool,
			},
		});
		toNextStep();
	};

	const toNextStep = () => {
		setCurrentStep(currentStep + 1);
	};

	const toPrevStep = () => {
		setCurrentStep(currentStep - 1);
	};

	const handleVerify = () => {
		verifyPipeline().then(() => {
			setVerifyStatus(VerifyStatus.SUCCESS);
		});
	};

	const verifyPipeline = () => {
		const { credential, username, pipelineTool, pipelineDomain } = form.getFieldsValue();
		return pipelineVerify({
			url: pipelineDomain,
			credential,
			username,
			type: pipelineTool,
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
											onVerify={handleVerify}
										/>
									</>
								)}
							</Form>
						</div>
					) : (
						<ConfigSuccess />
					)}
				</div>
			</Layout.Content>
		</Layout>
	);
};
