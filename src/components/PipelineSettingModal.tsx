import React, { FC } from "react";
import { Modal } from "antd";

interface PipelineSettingModalProps {
	visible: boolean;
	handleToggleVisible: () => void;
	title?: React.ReactNode | string;
	footer?: React.ReactNode;
	className?: string;
}

const PipelineSettingModal: FC<PipelineSettingModalProps> = ({
	visible,
	handleToggleVisible,
	title,
	footer,
	children,
	className,
}) => {
	return (
		<Modal
			visible={visible}
			onCancel={handleToggleVisible}
			centered={true}
			destroyOnClose={true}
			className={className}
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
