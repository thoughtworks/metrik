import React, { FC } from "react";
import { Button, Col, Divider, Form, Input, Row, Typography } from "antd";
import { css } from "@emotion/react";
import { BaseProject } from "../../shared/clients/projectApis";

const { Text } = Typography;

const groupTitleStyles = css({ fontWeight: "bold", display: "inline-block", marginBottom: 12 });

interface FormValues {
	projectName: string;
}

const ProjectNameSetup: FC<{
	visible?: boolean;
	onNext: (updated: Partial<BaseProject>) => void;
}> = ({ visible = true, onNext }) => {
	const onFinish = (values: FormValues) => {
		onNext({ name: values.projectName });
	};
	return (
		<div css={{ display: visible ? "block" : "none" }}>
			<Form layout={"vertical"} onFinish={onFinish}>
				{(formValues: FormValues) => {
					return (
						<>
							<Text css={groupTitleStyles}>Project Name</Text>
							<Row gutter={8} wrap={false}>
								<Col span={8}>
									<Form.Item
										name="projectName"
										rules={[
											{
												required: true,
												whitespace: true,
												message: "Please input project name",
											},
										]}>
										<Input />
									</Form.Item>
								</Col>
							</Row>

							<Divider css={{ margin: "24px -24px", width: "unset" }} />
							<Row>
								<Col span={24} style={{ textAlign: "right" }}>
									<Button
										type="primary"
										htmlType={"submit"}
										disabled={!formValues.projectName?.trim()}
										size={"large"}>
										Next
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

export default ProjectNameSetup;
