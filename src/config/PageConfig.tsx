import React, { useState } from "react";
import { Form, Divider, Typography, Layout, Steps } from "antd";
import { FieldsStep1 } from "./components/FieldsStep1";
import { FieldsStep2 } from "./components/FieldsStep2";

const { Text, Paragraph } = Typography;
const { Step } = Steps;

const ERROR_MESSAGES = {
	EMPTY_PIPELINE_NAME: "Please input pipeline name",
	EMPTY_PIPELINE_TOOL: "Please input pipeline tool",
	EMPTY_DOMAIN_NAME: "Please input pipeline domain",
	EMPTY_TOKEN: "Please input token",
};

const required = (errorMsg: string) => (value?: string) => (value ? null : errorMsg);

interface FieldsErrors {
	pipelineName?: string | null;
	pipelineTool?: string | null;
	pipelineDomain?: string | null;
	token?: string | null;
}

enum VerifyStatus {
	DEFAULT,
	SUCCESS,
	Fail,
}

export const PageConfig = () => {
	const [form] = Form.useForm();
	const [errors, setErrors] = useState<FieldsErrors>({});
	const [verifyStatus, setVerifyStatus] = useState<VerifyStatus>(VerifyStatus.DEFAULT);
	const [currentStep, setCurrentStep] = useState(0);
	const onFinish = (values: any) => {
		setErrors({});
		setTimeout(() => {
			setVerifyStatus(VerifyStatus.Fail);
			toNextStep();
		}, 2000);
		console.log("on finish", values);
	};

	const onFinishFailed = (errorInfo: any) => {
		const { pipelineName, pipelineTool, pipelineDomain, token } = errorInfo.values;

		setErrors({
			pipelineName: required(ERROR_MESSAGES.EMPTY_PIPELINE_NAME)(pipelineName),
			pipelineTool: required(ERROR_MESSAGES.EMPTY_PIPELINE_TOOL)(pipelineTool),
			pipelineDomain: required(ERROR_MESSAGES.EMPTY_PIPELINE_TOOL)(pipelineDomain),
			token: required(ERROR_MESSAGES.EMPTY_PIPELINE_TOOL)(token),
		});
	};

	const toNextStep = () => {
		setErrors({});
		setCurrentStep(currentStep + 1);
	};

	const toPrevStep = () => {
		setErrors({});
		setCurrentStep(currentStep - 1);
	};

	return (
		<Layout style={{ height: "100vh" }}>
			<Layout.Content>
				<div css={{ width: 896, margin: "24px auto", padding: 24, background: "#fff" }}>
					<Steps current={currentStep} css={{ margin: "44px 0" }}>
						<Step title="Create Dashboard" />
						<Step title="Config Project" />
						<Step title="Success" />
					</Steps>
					<div css={{ width: 440, marginBottom: 32 }}>
						<Text type={"secondary"}>Instructions:</Text>
						<Paragraph type={"secondary"}>
							The 4 key metrics is displayed based on PIPELINE and GIT data. Please configure the
							project data source you want to detect here.
						</Paragraph>
					</div>
					<Divider />

					<Form layout="vertical" onFinish={onFinish} onFinishFailed={onFinishFailed} form={form}>
						<FieldsStep1
							form={form}
							onNext={toNextStep}
							setErrors={setErrors}
							errors={errors}
							visible={currentStep === 0}
						/>
						<FieldsStep2
							form={form}
							onBack={toPrevStep}
							setErrors={setErrors}
							errors={errors}
							visible={currentStep === 1}
						/>
					</Form>
				</div>
			</Layout.Content>
		</Layout>
	);
};
