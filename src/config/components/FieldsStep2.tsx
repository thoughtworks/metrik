import React, { useState, FC } from "react";
import { Form, Input, Button, Alert, Divider, Row, Col, Typography, Select } from "antd";
import { css } from "@emotion/react";
import { ERROR_MESSAGES } from "../../constants/errorMessages";
import { VerifyStatus } from "../../__types__/base";

const { Option } = Select;
const { Text } = Typography;

const backBtnStyles = css({
	marginRight: 8,
});

const groupTitleStyles = css({ fontWeight: "bold", display: "inline-block", marginBottom: 12 });

/* eslint-disable react/prop-types */
export const FieldsStep2: FC<{
	formValues: { [key: string]: any };
	onBack: () => void;
	visible?: boolean;
	verifyStatus: VerifyStatus;
	onVerify?: () => void;
}> = ({ onBack, formValues, visible = true, verifyStatus, onVerify }) => (
	<div css={{ display: visible ? "block" : "none" }}>
		<Text css={groupTitleStyles}>Pipelines</Text>

		<Row gutter={8} wrap={false}>
			<Col span={8}>
				<Form.Item
					label="Pipeline Name"
					name="pipelineName"
					rules={[{ required: true, message: ERROR_MESSAGES.EMPTY_PIPELINE_NAME }]}>
					<Input />
				</Form.Item>
			</Col>
		</Row>

		<Row gutter={8} wrap={false}>
			<Col span={4}>
				<Form.Item
					label="Pipeline Tool"
					name="pipelineTool"
					rules={[{ required: true, message: ERROR_MESSAGES.EMPTY_PIPELINE_TOOL }]}>
					<Select>
						<Option value="jenkins">Jenkins</Option>
					</Select>
				</Form.Item>
			</Col>
			<Col span={8}>
				<Form.Item
					label="Pipeline Domain"
					name="pipelineDomain"
					rules={[{ required: true, message: ERROR_MESSAGES.EMPTY_DOMAIN_NAME }]}>
					<Input />
				</Form.Item>
			</Col>
			<Col span={8}>
				<Form.Item
					label="Token"
					name="token"
					rules={[{ required: true, message: ERROR_MESSAGES.EMPTY_TOKEN }]}>
					<Input />
				</Form.Item>
			</Col>
			<Col span={1}>
				<Form.Item label={" "}>
					<Button
						onClick={onVerify}
						disabled={!formValues.pipelineTool || !formValues.pipelineDomain || !formValues.token}>
						Verify
					</Button>
				</Form.Item>
			</Col>
		</Row>
		{verifyStatus === VerifyStatus.SUCCESS && <Alert message="success" type="success" />}
		{verifyStatus === VerifyStatus.Fail && <Alert message="error" type="error" />}

		<Divider />
		<Row>
			<Col span={24} style={{ textAlign: "right" }}>
				<Button css={backBtnStyles} onClick={onBack}>
					Back
				</Button>
				<Button
					type="primary"
					htmlType="submit"
					disabled={
						!formValues.pipelineName ||
						!formValues.pipelineTool ||
						!formValues.pipelineDomain ||
						!formValues.token
					}>
					Create
				</Button>
			</Col>
		</Row>
	</div>
);
/* eslint-disable react/prop-types */
