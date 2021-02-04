import React, { FC, useState } from "react";
import { FieldsStep2 } from "../../config/components/FieldsStep2";
import { Form } from "antd";
import { VerifyStatus } from "../../__types__/base";
import {
	createPipelineUsingPost,
	PipelineVoRes,
	verifyPipelineUsingPost,
} from "../../clients/apis";
import { ConfigFormValues } from "../../config/PageConfig";

interface PipelineConfigProps {
	defaultData?: PipelineVoRes;
	onBack: () => void;
	className?: string;
	dashboardId: string;
	updateDashboard: () => void;
}

const PipelineConfig: FC<PipelineConfigProps> = ({
	defaultData,
	onBack,
	className,
	dashboardId,
	updateDashboard,
}) => {
	const [form] = Form.useForm<ConfigFormValues>();
	const [verifyStatus, setVerifyStatus] = useState<VerifyStatus>(VerifyStatus.DEFAULT);

	const onFinish = async ({ dashboardName, ...pipeline }: ConfigFormValues) => {
		await verifyPipeline();
		await createPipelineUsingPost({
			dashboardId: dashboardId,
			requestBody: pipeline,
		});
		updateDashboard();
		onBack();
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
		<div className={className}>
			<Form
				layout="vertical"
				onFinish={onFinish}
				form={form}
				initialValues={{ type: "JENKINS", ...defaultData }}>
				{(formValues: ConfigFormValues) => (
					<FieldsStep2
						showTitle={false}
						visible={true}
						formValues={formValues}
						onBack={onBack}
						verifyStatus={verifyStatus}
						onVerify={verifyPipeline}
					/>
				)}
			</Form>
		</div>
	);
};

export default PipelineConfig;
