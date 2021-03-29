import {
	BambooPipeline,
	JenkinsPipeline,
	Pipeline,
	PipelineTool,
	verifyPipelineUsingPost,
} from "../../clients/pipelineApis";
import React, { FC, useState } from "react";
import { Alert, Button, Col, Divider, Form, Input, Row, Select, Typography } from "antd";
import { css } from "@emotion/react";
import { PIPELINE_CONFIG, PIPELINE_TYPE_NOTE } from "../../utils/pipelineConfig/pipelineConfig";
import { VerifyStatus } from "../../models/common";

const { Option } = Select;
const { Text } = Typography;
const { Item, useForm } = Form;

type JenkinsFormValues = Omit<JenkinsPipeline, "id">;
type BambooFormValues = Omit<BambooPipeline, "id">;
export type FormValues = JenkinsFormValues | BambooFormValues;

const groupTitleStyles = css({ fontWeight: "bold", display: "inline-block", marginBottom: 12 });
const backBtnStyles = css({ marginLeft: 8 });

const PipelineSetup: FC<{
	visible?: boolean;
	onText?: "Create" | "Update";
	pipeline?: Pipeline;
	onSubmit: (values: FormValues) => void;
	onBack: () => void;
}> = ({ visible = true, onText = "Create", pipeline, onSubmit, onBack }) => {
	const [loading, setLoading] = useState(false);
	const [verifyStatus, setVerifyStatus] = useState<VerifyStatus>(VerifyStatus.DEFAULT);

	const [form] = useForm<FormValues>();

	const shouldDisabledVerifyButton = (values: FormValues) => {
		return (
			!values.type ||
			!values.url ||
			!values.credential ||
			(values.type === PipelineTool.JENKINS && !values.username)
		);
	};
	const shouldDisabledSubmitButton = (values: FormValues) => {
		return !values.name || shouldDisabledVerifyButton(values);
	};

	const onVerify = () => {
		const { name, ...verification } = form.getFieldsValue();

		return verifyPipelineUsingPost({
			verification,
		})
			.then(() => {
				setVerifyStatus(VerifyStatus.SUCCESS);
			})
			.catch(error => {
				setVerifyStatus(VerifyStatus.Fail);
				throw new Error(error);
			});
	};

	const onValuesChange = (changedValues: Partial<FormValues>, values: FormValues) => {
		if (changedValues.type) {
			const result = Object.keys(values).reduce((sum, key) => ({ ...sum, [key]: undefined }), {});
			form.setFieldsValue({ ...result, type: changedValues.type });
			setVerifyStatus(VerifyStatus.DEFAULT);
		}
	};

	const onFinish = async (values: FormValues) => {
		setLoading(true);
		try {
			await onVerify();
			await onSubmit(values);
		} finally {
			setLoading(false);
		}
	};

	return (
		<Form
			css={{ height: "100%" }}
			layout={"vertical"}
			initialValues={{ type: PipelineTool.JENKINS, ...pipeline }}
			form={form}
			onValuesChange={onValuesChange}
			onFinish={onFinish}>
			{(formValues: FormValues) => {
				const config = PIPELINE_CONFIG[formValues.type];

				return (
					<div
						css={{
							display: visible ? "flex" : "none",
							height: "100%",
							flexDirection: "column",
						}}>
						<Text css={groupTitleStyles}>Pipeline Details</Text>
						<Row gutter={24} wrap={false} align={"bottom"}>
							<Col span={4}>
								<Item
									label="Pipeline Tool"
									name="type"
									rules={[
										{
											required: true,
											whitespace: true,
											message: "Please input pipeline tool",
										},
									]}>
									<Select>
										<Option value={PipelineTool.JENKINS}>{PipelineTool.JENKINS}</Option>
										<Option value={PipelineTool.BAMBOO}>{PipelineTool.BAMBOO}</Option>
									</Select>
								</Item>
							</Col>
						</Row>

						{PIPELINE_TYPE_NOTE[formValues.type] && (
							<Row wrap={false} align={"bottom"}>
								<Col span={24}>{PIPELINE_TYPE_NOTE[formValues.type]}</Col>
							</Row>
						)}
						{config.map((row, rowIdx) => (
							<Row key={rowIdx} gutter={row.gutter} wrap={false}>
								{row.children.map(({ span, type, placeholder, ...props }, colIdx) => (
									<Col key={colIdx} span={span}>
										<Item {...props}>
											<Input type={type} placeholder={placeholder} />
										</Item>
									</Col>
								))}
								{rowIdx === config.length - 1 && (
									<Col span={2}>
										<Item label={" "}>
											<Button disabled={shouldDisabledVerifyButton(formValues)} onClick={onVerify}>
												Verify
											</Button>
										</Item>
									</Col>
								)}
							</Row>
						))}

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
									size={"large"}
									loading={loading}
									disabled={shouldDisabledSubmitButton(formValues)}>
									{onText}
								</Button>
								<Button css={backBtnStyles} size={"large"} onClick={onBack}>
									Back
								</Button>
							</Col>
						</Row>
					</div>
				);
			}}
		</Form>
	);
};
export default PipelineSetup;
