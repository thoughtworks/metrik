import {
	BambooPipeline,
	JenkinsPipeline,
	PipelineTool,
	verifyPipelineUsingPost,
} from "../../clients/pipelineApis";
import React, { FC, useState } from "react";
import { Alert, Button, Col, Divider, Form, Input, Row, Select, Typography } from "antd";
import { css } from "@emotion/react";
import { VerifyStatus } from "../../__types__/base";
import { BAMBOO_PIPELINE_CONFIG, JENKINS_PIPELINE_CONFIG } from "./pipelineConfigs";
import { BaseProject, createProjectUsingPost } from "../../clients/projectApis";

const { Option } = Select;
const { Text } = Typography;
const { Item, useForm } = Form;

type JenkinsFormValues = Omit<JenkinsPipeline, "id">;
type BambooFormValues = Omit<BambooPipeline, "id">;
type FormValues = JenkinsFormValues | BambooFormValues;

const groupTitleStyles = css({ fontWeight: "bold", display: "inline-block", marginBottom: 12 });
const backBtnStyles = css({ marginLeft: 8 });

const PipelineSetup: FC<{
	visible?: boolean;
	project: BaseProject;
	onNext: (updated: Partial<BaseProject>) => void;
	onBack: () => void;
}> = ({ visible = true, project, onNext, onBack }) => {
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

	const onFinish = async (values: FormValues) => {
		await onVerify();
		const { id, name } = await createProjectUsingPost({
			projectName: project.name,
			pipeline: values,
		});
		onNext({ id, name });
	};

	return (
		<div css={{ display: visible ? "flex" : "none", height: "100%", flexDirection: "column" }}>
			<Form
				layout={"vertical"}
				initialValues={{ type: PipelineTool.JENKINS }}
				form={form}
				onFinish={onFinish}>
				{(formValues: FormValues) => {
					const config =
						formValues.type === PipelineTool.JENKINS
							? JENKINS_PIPELINE_CONFIG
							: BAMBOO_PIPELINE_CONFIG;

					return (
						<>
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
							{config.map((row, rowIdx) => (
								<Row key={rowIdx} gutter={row.gutter} wrap={false}>
									{row.children.map(({ span, type, ...props }, colIdx) => (
										<Col key={colIdx} span={span}>
											<Item {...props}>
												<Input type={type} />
											</Item>
										</Col>
									))}
									{rowIdx === JENKINS_PIPELINE_CONFIG.length - 1 && (
										<Col span={2}>
											<Item label={" "}>
												<Button
													disabled={shouldDisabledVerifyButton(formValues)}
													onClick={onVerify}>
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
										disabled={shouldDisabledSubmitButton(formValues)}
										size={"large"}>
										Create
									</Button>
									<Button css={backBtnStyles} size={"large"} onClick={onBack}>
										Back
									</Button>
								</Col>
							</Row>
						</>
					);
				}}
			</Form>
		</div>
	);
};
export default PipelineSetup;
