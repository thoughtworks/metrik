import React, { useState } from "react";
import { Form, Input, Button, Alert, Divider, Row, Col, Typography, Layout, Steps } from "antd";
import { css } from "@emotion/react";

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

const backBtnStyles = css({
	marginRight: 8,
});

const pipelinesTextStyles = css({ fontWeight: "bold", display: "inline-block", marginBottom: 12 });

export const PageConfig = () => {
	const [form] = Form.useForm();
	const [errors, setErrors] = useState<FieldsErrors>({});
	const [verifyStatus, setVerifyStatus] = useState<VerifyStatus>(VerifyStatus.DEFAULT);
	const onFinish = (values: any) => {
		setErrors({});
		setTimeout(() => {
			setVerifyStatus(VerifyStatus.Fail);
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

	const onVerify = () => {
		const { pipelineTool, pipelineDomain, token } = form.getFieldsValue();
		const currentError = {
			pipelineTool: required(ERROR_MESSAGES.EMPTY_PIPELINE_TOOL)(pipelineTool),
			pipelineDomain: required(ERROR_MESSAGES.EMPTY_DOMAIN_NAME)(pipelineDomain),
			token: required(ERROR_MESSAGES.EMPTY_TOKEN)(token),
		};

		setErrors(prev => ({
			...prev,
			...currentError,
		}));

		if (!currentError.pipelineTool && !currentError.pipelineDomain && !currentError.token) {
			setTimeout(() => {
				setVerifyStatus(VerifyStatus.SUCCESS);
			}, 2000);
		}
	};

	return (
		<Layout style={{ height: "100vh" }}>
			<Layout.Content>
				<div css={{ width: 896, margin: "24px auto", padding: 24, background: "#fff" }}>
					<Steps current={1} css={{ margin: "44px 0" }}>
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

					<Form
						layout="vertical"
						name="basic"
						onFinish={onFinish}
						onFinishFailed={onFinishFailed}
						form={form}>
						<Text css={pipelinesTextStyles}>Pipelines</Text>

						<Row gutter={8} wrap={false}>
							<Col span={8}>
								<Form.Item
									label="Pipeline Name"
									name="pipelineName"
									rules={[{ required: true }]}
									validateStatus={errors.pipelineName ? "error" : "success"}
									help={errors.pipelineName ? ERROR_MESSAGES.EMPTY_PIPELINE_NAME : ""}>
									<Input />
								</Form.Item>
							</Col>
						</Row>

						<Row gutter={8} wrap={false}>
							<Col span={4}>
								<Form.Item
									label="Pipeline Tool"
									name="pipelineTool"
									rules={[{ required: true }]}
									validateStatus={errors.pipelineTool ? "error" : "success"}
									help={errors.pipelineTool ? ERROR_MESSAGES.EMPTY_PIPELINE_TOOL : ""}>
									<Input />
								</Form.Item>
							</Col>
							<Col span={8}>
								<Form.Item
									label="Pipeline Domain"
									name="pipelineDomain"
									rules={[{ required: true }]}
									validateStatus={errors.pipelineDomain ? "error" : "success"}
									help={errors.pipelineDomain ? ERROR_MESSAGES.EMPTY_DOMAIN_NAME : ""}>
									<Input />
								</Form.Item>
							</Col>
							<Col span={8}>
								<Form.Item
									label="Token"
									name="token"
									rules={[{ required: true }]}
									validateStatus={errors.token ? "error" : "success"}
									help={errors.token ? ERROR_MESSAGES.EMPTY_TOKEN : ""}>
									<Input />
								</Form.Item>
							</Col>
							<Col span={1}>
								<Form.Item label={" "} name={"verify"}>
									<Button onClick={onVerify}>Verify</Button>
								</Form.Item>
							</Col>
						</Row>
						{verifyStatus === VerifyStatus.SUCCESS && <Alert message="success" type="success" />}
						{verifyStatus === VerifyStatus.Fail && <Alert message="error" type="error" />}

						<Divider />
						<Row>
							<Col span={24} style={{ textAlign: "right" }}>
								<Button css={backBtnStyles}>Back</Button>
								<Button type="primary" htmlType="submit">
									Create
								</Button>
							</Col>
						</Row>
					</Form>
				</div>
			</Layout.Content>
		</Layout>
	);
};
