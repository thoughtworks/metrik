import React, { useState } from "react";
import { Divider, Form, Layout, Steps, Typography } from "antd";
import { FieldsStep1 } from "./components/FieldsStep1";
import { FieldsStep2 } from "./components/FieldsStep2";
import { VerifyStatus } from "../__types__/base";

const { Text, Paragraph } = Typography;
const { Step } = Steps;

export const PageConfig = () => {
	const [form] = Form.useForm();
	const [verifyStatus, setVerifyStatus] = useState<VerifyStatus>(VerifyStatus.DEFAULT);
	const [currentStep, setCurrentStep] = useState(0);
	const onFinish = (values: any) => {
		verifyPipeline().then(() => {
			toNextStep();
		});
		console.log("on finish", values);
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

	const verifyPipeline = () =>
		new Promise(resolve => {
			setTimeout(() => {
				resolve({ code: 200, msg: "verify success" });
			}, 2000);
		});

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
						{currentStep === 0 && (
							<Paragraph type={"secondary"}>Please Input your dashboard name</Paragraph>
						)}
						{currentStep === 1 && (
							<Paragraph type={"secondary"}>
								The 4 key metrics is displayed based on PIPELINE and GIT data. Please configure the
								project data source you want to detect here.
							</Paragraph>
						)}
					</div>
					<Divider />

					<Form
						layout="vertical"
						onFinish={onFinish}
						form={form}
						initialValues={{ pipelineTool: "jenkins" }}>
						{formValues => (
							<>
								<FieldsStep1
									onNext={toNextStep}
									visible={currentStep === 0}
									formValues={formValues}
								/>
								<FieldsStep2
									onBack={toPrevStep}
									formValues={formValues}
									visible={currentStep === 1}
									verifyStatus={verifyStatus}
									onVerify={handleVerify}
								/>
							</>
						)}
					</Form>
				</div>
			</Layout.Content>
		</Layout>
	);
};
