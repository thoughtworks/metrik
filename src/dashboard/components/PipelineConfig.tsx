import React, { FC, useState } from "react";
import { FieldsStep2 } from "../../config/components/FieldsStep2";
import { Form } from "antd";
import { VerifyStatus } from "../../shared/__types__/base";
import { ConfigFormValues } from "../../config/PageConfig";
import { useRequest } from "../../shared/hooks/useRequest";
import {
	createPipelineUsingPost,
	Pipeline,
	PipelineTool,
	updatePipelineUsingPut,
	verifyPipelineUsingPost,
} from "../../shared/clients/pipelineApis";

interface PipelineConfigProps {
	defaultPipeline?: Pipeline;
	onBack: () => void;
	onSubmit: typeof createPipelineUsingPost | typeof updatePipelineUsingPut;
	onSuccess?: () => void;
	className?: string;
	projectId: string;
	updateProject: () => void;
}

const PipelineConfig: FC<PipelineConfigProps> = ({
	defaultPipeline,
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
		if (pipeline.type === PipelineTool.BAMBOO) {
			delete pipeline.username;
		}
		console.log(defaultPipeline, pipeline);
		await verifyPipeline();
		handleSubmit({
			projectId: projectId,
			pipeline: { ...pipeline, id: defaultPipeline?.id as string },
		}).then(() => {
			updateProject();
			onBack();
			onSuccess?.();
		});
	};
	const verifyPipeline = () => {
		// eslint-disable-next-line @typescript-eslint/no-unused-vars
		const { projectName, ...pipelineValues } = form.getFieldsValue();

		if (pipelineValues.type === PipelineTool.BAMBOO) {
			delete pipelineValues.username;
		}

		return verifyPipelineUsingPost({
			verification: pipelineValues,
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
				initialValues={{ type: PipelineTool.JENKINS, ...defaultPipeline }}>
				{(formValues: ConfigFormValues) => (
					<FieldsStep2
						visible={true}
						formValues={formValues}
						onBack={onBack}
						loading={loading}
						verifyStatus={verifyStatus}
						onVerify={verifyPipeline}
						isUpdate={!!defaultPipeline}
					/>
				)}
			</Form>
		</div>
	);
};

export default PipelineConfig;
