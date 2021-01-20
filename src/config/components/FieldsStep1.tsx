import React, { FC } from "react";
import { Form, Input, Button, Divider, Row, Col, Typography } from "antd";
import { css } from "@emotion/react";
import { ERROR_MESSAGES } from "../../constants/errorMessages";
import { FormInstance } from "antd/es/form";
import { required } from "../../utils/validates";

const { Text } = Typography;

const groupTitleStyles = css({ fontWeight: "bold", display: "inline-block", marginBottom: 12 });

/* eslint-disable react/prop-types */
export const FieldsStep1: FC<{
	errors: any; // TODO: add types later
	setErrors: any; // TODO: add types later
	onNext: () => void;
	form: FormInstance;
	visible?: boolean;
}> = ({ onNext, form, errors, setErrors, visible = true }) => {
	return (
		<div css={{ display: visible ? "block" : "none" }}>
			<Text css={groupTitleStyles}>Dashboard Name</Text>

			<Row gutter={8} wrap={false}>
				<Col span={8}>
					<Form.Item
						label="Dashboard Name"
						name="dashboardName"
						rules={[{ required: true }]}
						validateStatus={errors.dashboardName ? "error" : "success"}
						help={errors.dashboardName ? ERROR_MESSAGES.EMPTY_DASHBOARD_NAME : ""}>
						<Input />
					</Form.Item>
				</Col>
			</Row>

			<Divider />
			<Row>
				<Col span={24} style={{ textAlign: "right" }}>
					<Button
						type="primary"
						onClick={() => {
							const { dashboardName } = form.getFieldsValue();
							if (!dashboardName) {
								// TODO: add types later
								setErrors((prev: any) => ({
									...prev,
									dashboardName: required(ERROR_MESSAGES.EMPTY_DASHBOARD_NAME)(dashboardName),
								}));
								return;
							}
							onNext();
						}}>
						Next Step
					</Button>
				</Col>
			</Row>
		</div>
	);
};
/* eslint-disable react/prop-types */
