import React, { FC, useState } from "react";
import { FieldsStep2 } from "../../config/components/FieldsStep2";
import { Form } from "antd";
import { VerifyStatus } from "../../shared/__types__/base";
import {
	createPipelineUsingPost,
	PipelineResponse,
	updatePipelineUsingPut,
	verifyPipelineUsingPost,
} from "../../shared/clients/apis";
import { ConfigFormValues } from "../../config/PageConfig";
import { useRequest } from "../../shared/hooks/useRequest";

interface PipelineConfigProps {
	defaultData?: PipelineResponse;
	onBack: () => void;
	onSubmit: typeof createPipelineUsingPost | typeof updatePipelineUsingPut;
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
	onSubmit,
}) => {
	const [form] = Form.useForm<ConfigFormValues>();
	const [verifyStatus, setVerifyStatus] = useState<VerifyStatus>(VerifyStatus.DEFAULT);
	const [, handleSubmit, loading] = useRequest(onSubmit);
	// eslint-disable-next-line @typescript-eslint/no-unused-vars
	const onFinish = async ({ dashboardName, ...pipeline }: ConfigFormValues) => {
		await verifyPipeline();
		handleSubmit({
			dashboardId,
			requestBody: pipeline,
			pipelineId: defaultData?.id as string,
		}).then(() => {
			updateDashboard();
			onBack();
		});
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
				css={{ height: "100%" }}
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
						loading={loading}
						verifyStatus={verifyStatus}
						onVerify={verifyPipeline}
						isUpdate={!!defaultData}
					/>
				)}
			</Form>
		</div>
	);
};

export default PipelineConfig;
