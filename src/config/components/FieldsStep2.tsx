import React, { useState, FC } from "react";
import { Form, Input, Button, Alert, Divider, Row, Col, Typography } from "antd";
import { css } from "@emotion/react";
import { FormInstance } from "antd/es/form";
import { ERROR_MESSAGES } from "../../constants/errorMessages";

const { Text } = Typography;

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

const groupTitleStyles = css({ fontWeight: "bold", display: "inline-block", marginBottom: 12 });

/* eslint-disable react/prop-types */
export const FieldsStep2: FC<{ form: FormInstance; onNext: () => void; onBack: () => void }> = ({
	form,
	onBack,
	onNext,
}) => {
	const [errors, setErrors] = useState<FieldsErrors>({});
	const [verifyStatus, setVerifyStatus] = useState<VerifyStatus>(VerifyStatus.DEFAULT);

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
		<>
			<Text css={groupTitleStyles}>Pipelines</Text>

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
					<Button
						css={backBtnStyles}
						onClick={() => {
							onBack();
						}}>
						Back
					</Button>
					<Button
						type="primary"
						htmlType="submit"
						onClick={() => {
							onNext();
						}}>
						Create
					</Button>
				</Col>
			</Row>
		</>
	);
};
/* eslint-disable react/prop-types */
