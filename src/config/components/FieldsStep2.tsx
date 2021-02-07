import React, { FC } from "react";
import { Alert, Button, Col, Divider, Form, Input, Row, Select, Typography } from "antd";
import { css } from "@emotion/react";
import { ERROR_MESSAGES } from "../../constants/errorMessages";
import { VerifyStatus } from "../../__types__/base";
import { ConfigFormValues } from "../PageConfig";

const { Option } = Select;
const { Text } = Typography;

const backBtnStyles = css({
	marginRight: 8,
});

const groupTitleStyles = css({ fontWeight: "bold", display: "inline-block", marginBottom: 12 });

/* eslint-disable react/prop-types */
export const FieldsStep2: FC<{
	formValues: ConfigFormValues;
	onBack: () => void;
	visible?: boolean;
	verifyStatus: VerifyStatus;
	onVerify?: () => void;
	showTitle?: boolean;
}> = ({ onBack, formValues, visible = true, verifyStatus, onVerify, showTitle = true }) => (
	<div css={{ display: visible ? "block" : "none" }}>
		{showTitle && <Text css={groupTitleStyles}>Pipelines</Text>}

		<Row gutter={8} wrap={false}>
			<Col span={8}>
				<Form.Item
					label="Pipeline Name"
					name="name"
					rules={[
						{ required: true, whitespace: true, message: ERROR_MESSAGES.EMPTY_PIPELINE_NAME },
					]}>
					<Input />
				</Form.Item>
			</Col>
		</Row>

		<Row gutter={8} wrap={false}>
			<Col span={4}>
				<Form.Item
					label="Pipeline Tool"
					name="type"
					rules={[
						{ required: true, whitespace: true, message: ERROR_MESSAGES.EMPTY_PIPELINE_TOOL },
					]}>
					<Select>
						<Option value="JENKINS">Jenkins</Option>
					</Select>
				</Form.Item>
			</Col>
			<Col span={8}>
				<Form.Item
					label="Pipeline Url"
					name="url"
					rules={[{ required: true, whitespace: true, message: ERROR_MESSAGES.EMPTY_DOMAIN_NAME }]}>
					<Input />
				</Form.Item>
			</Col>
			<Col span={4}>
				<Form.Item
					label="Username"
					name="username"
					rules={[{ required: true, whitespace: true, message: ERROR_MESSAGES.EMPTY_USERNAME }]}>
					<Input />
				</Form.Item>
			</Col>
			<Col span={5}>
				<Form.Item
					label="Token"
					name="credential"
					rules={[{ required: true, whitespace: true, message: ERROR_MESSAGES.EMPTY_CREDENTIAL }]}>
					<Input type={"password"} />
				</Form.Item>
			</Col>
			<Col span={1}>
				<Form.Item label={" "}>
					<Button
						onClick={onVerify}
						disabled={
							!formValues.type || !formValues.url || !formValues.credential || !formValues.username
						}>
						Verify
					</Button>
				</Form.Item>
			</Col>
		</Row>
		{verifyStatus === VerifyStatus.SUCCESS && (
			<Alert message="Pipeline verify success" type="success" showIcon />
		)}
		{verifyStatus === VerifyStatus.Fail && (
			<Alert message="Pipeline verify failed" type="error" showIcon />
		)}

		<Divider css={{ margin: "24px -24px", width: "unset" }} />
		<Row>
			<Col span={24} style={{ textAlign: "right" }}>
				<Button css={backBtnStyles} onClick={onBack} size={"large"}>
					Back
				</Button>
				<Button
					type="primary"
					htmlType="submit"
					disabled={
						!formValues.name || !formValues.type || !formValues.url || !formValues.credential
					}
					size={"large"}>
					Create
				</Button>
			</Col>
		</Row>
	</div>
);
/* eslint-disable react/prop-types */
