import React, { FC } from "react";
import { Modal } from "antd";

interface PipelineSettingModalProps {
	visible: boolean;
	handleToggleVisible: () => void;
	title?: React.ReactNode | string;
	footer?: React.ReactNode;
}

const PipelineSettingModal: FC<PipelineSettingModalProps> = ({
	visible,
	handleToggleVisible,
	title,
	footer,
	children,
}) => {
	return (
		<Modal
			visible={visible}
			onCancel={handleToggleVisible}
			centered={true}
			css={{
				".ant-modal-body": {
					height: 500,
					overflowY: "auto",
				},
			}}
			bodyStyle={{
				padding: 0,
			}}
			width={896}
			title={title}
			footer={footer}
			closable={false}>
			{children}
		</Modal>
	);
};

export default PipelineSettingModal;
