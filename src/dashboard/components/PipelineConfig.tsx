import React, { FC, useState } from "react";
import { FieldsStep2 } from "../../config/components/FieldsStep2";
import { Form } from "antd";
import { VerifyStatus } from "../../shared/__types__/base";
import {
	createPipelineUsingPost,
	PipelineResponse,
	PipelineResponseType,
	updatePipelineUsingPut,
	verifyPipelineUsingPost,
} from "../../shared/clients/apis";
import { ConfigFormValues } from "../../config/PageConfig";
import { useRequest } from "../../shared/hooks/useRequest";

interface PipelineConfigProps {
	defaultData?: PipelineResponse;
	onBack: () => void;
	onSubmit: typeof createPipelineUsingPost | typeof updatePipelineUsingPut;
	onSuccess?: () => void;
	className?: string;
	projectId: string;
	updateProject: () => void;
}

const PipelineConfig: FC<PipelineConfigProps> = ({
	defaultData,
	onBack,
	className,
	projectId,
	updateProject,
	onSubmit,
	onSuccess,
}) => {
	const [form] = Form.useForm<ConfigFormValues>();
	const [verifyStatus, setVerifyStatus] = useState<VerifyStatus>(VerifyStatus.DEFAULT);
	const [, handleSubmit, loading] = useRequest(onSubmit);
	// eslint-disable-next-line @typescript-eslint/no-unused-vars
	const onFinish = async ({ projectName, ...pipeline }: ConfigFormValues) => {
		if (pipeline.type === PipelineResponseType.BAMBOO) {
			delete pipeline.username;
		}

		await verifyPipeline();
		handleSubmit({
			projectId: projectId,
			requestBody: pipeline,
			pipelineId: defaultData?.id as string,
		}).then(() => {
			updateProject();
			onBack();
			onSuccess?.();
		});
	};
	const verifyPipeline = () => {
		// eslint-disable-next-line @typescript-eslint/no-unused-vars
		const { projectName, ...pipelineValues } = form.getFieldsValue();

		if (pipelineValues.type === PipelineResponseType.BAMBOO) {
			delete pipelineValues.username;
		}

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
