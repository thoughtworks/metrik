import React, { FC } from "react";
import { Alert, Button, Col, Divider, Form, Input, Row, Select, Typography } from "antd";
import { css } from "@emotion/react";
import { ERROR_MESSAGES } from "../../shared/constants/errorMessages";
import { VerifyStatus } from "../../shared/__types__/base";
import { ConfigFormValues } from "../PageConfig";
import HintIcon from "../../shared/components/HintIcon";

const { Option } = Select;
const { Text } = Typography;

const backBtnStyles = css({
	marginLeft: 8,
});

const groupTitleStyles = css({ fontWeight: "bold", display: "inline-block", marginBottom: 12 });

/* eslint-disable react/prop-types */
export const FieldsStep2: FC<{
	formValues: ConfigFormValues;
	onBack: () => void;
	visible?: boolean;
	verifyStatus: VerifyStatus;
	onVerify?: () => void;
	loading?: boolean;
	isUpdate?: boolean;
}> = ({
	onBack,
	formValues,
	visible = true,
	verifyStatus,
	onVerify,
	loading = false,
	isUpdate = false,
}) => (
	<div css={{ display: visible ? "flex" : "none", height: "100%", flexDirection: "column" }}>
		<Text css={groupTitleStyles}>Pipeline Details</Text>

		<Row gutter={8} wrap={false} align={"bottom"}>
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
		</Row>

		<Row gutter={24} wrap={false} align={"bottom"}>
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

			<Col span={16}>
				<Form.Item
					name="url"
					label={
						<HintIcon
							text={"Pipeline URL"}
							tooltip={
								"URL of the pipeline. Please do ensure the URL is complete, including the folder/subfolder information if there’s any."
							}
						/>
					}
					rules={[{ required: true, whitespace: true, message: ERROR_MESSAGES.EMPTY_DOMAIN_NAME }]}>
					<Input placeholder={"e.g: http://jenkins_domain_name/job/folder_name/job/job_name/"} />
				</Form.Item>
			</Col>
		</Row>

		<Row gutter={24} wrap={false} align={"bottom"}>
			<Col span={8}>
				<Form.Item
					label="Jenkins Username"
					name="username"
					rules={[{ required: true, whitespace: true, message: ERROR_MESSAGES.EMPTY_USERNAME }]}>
					<Input />
				</Form.Item>
			</Col>
			<Col span={8}>
				<Form.Item
					name="credential"
					label={
						<HintIcon
							text={"Access Token"}
							tooltip={
								"The access token will be used to invoke Jenkins APIs to fetch pipeline execution status.The regular password for the Jenkins UI also works here, though not recommended."
							}
						/>
					}
					rules={[{ required: true, whitespace: true, message: ERROR_MESSAGES.EMPTY_CREDENTIAL }]}>
					<Input type={"password"} />
				</Form.Item>
			</Col>
			<Col span={2}>
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
			<Alert message="Pipeline successfully verified." type="success" showIcon />
		)}
		{verifyStatus === VerifyStatus.Fail && (
			<Alert
				message="Pipeline verify is failed. Please check your pipeline URL and token."
				type="error"
				showIcon
			/>
		)}

		<div css={{ flexGrow: 1 }} />
		<Divider css={{ margin: "24px -24px", width: "unset" }} />
		<Row>
			<Col span={24} style={{ textAlign: "right" }}>
				<Button
					type="primary"
					htmlType="submit"
					disabled={
						!formValues.name ||
						!formValues.type ||
						!formValues.url ||
						!formValues.username ||
						!formValues.credential
					}
					size={"large"}
					loading={loading}>
					{isUpdate ? "Update" : "Create"}
				</Button>
				<Button css={backBtnStyles} onClick={onBack} size={"large"}>
					Back
				</Button>
			</Col>
		</Row>
	</div>
);

/* eslint-disable react/prop-types */
