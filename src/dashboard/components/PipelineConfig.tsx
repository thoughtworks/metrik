import React, { FC, useState } from "react";
import { FieldsStep2 } from "../../config/components/FieldsStep2";
import { Form } from "antd";
import { VerifyStatus } from "../../__types__/base";
import { PipelineVoRes, verifyPipelineUsingPost } from "../../clients/apis";
import { ConfigFormValues } from "../../config/PageConfig";

interface PipelineConfigProps {
	defaultData: PipelineVoRes;
	onCancel: () => void;
}

const PipelineConfig: FC<PipelineConfigProps> = ({ defaultData, onCancel }) => {
	const [form] = Form.useForm<ConfigFormValues>();
	const [verifyStatus, setVerifyStatus] = useState<VerifyStatus>(VerifyStatus.DEFAULT);

	const onFinish = async (values: ConfigFormValues) => {
		console.log("finish data", values);
	};

	const verifyPipeline = () => {
		// eslint-disable-next-line @typescript-eslint/no-unused-vars
		const { dashboardName, ...pipelineValues } = form.getFieldsValue();
		return verifyPipelineUsingPost({
			requestBody: pipelineValues,
		})
			.then(() => {
				setVerifyStatus(VerifyStatus.SUCCESS);
			})
			.catch(error => {
				setVerifyStatus(VerifyStatus.Fail);
				throw new Error(error);
			});
	};

	return (
		<Form layout="vertical" onFinish={onFinish} form={form} initialValues={defaultData}>
			{(formValues: ConfigFormValues) => (
				<FieldsStep2
					visible={true}
					formValues={formValues}
					onBack={onCancel}
					verifyStatus={verifyStatus}
					onVerify={verifyPipeline}
				/>
			)}
		</Form>
	);
};

export default PipelineConfig;
