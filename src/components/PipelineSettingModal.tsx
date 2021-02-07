import React, { FC } from "react";
import { Button, Modal, Result, Spin } from "antd";

interface PipelineSettingModalProps {
	visible: boolean;
	handleToggleVisible: () => void;
	title?: React.ReactNode | string;
	footer?: React.ReactNode;
	className?: string;
	isLoading?: boolean;
	error?: Error;
	reload?: () => void;
}

const PipelineSettingModal: FC<PipelineSettingModalProps> = ({
	isLoading = false,
	error,
	reload,
	visible,
	handleToggleVisible,
	title,
	footer,
	children,
	className,
}) => {
	console.log(isLoading);
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
			{isLoading ? (
				<Spin
					size="large"
					css={{
						display: "flex",
						justifyContent: "center",
						alignItems: "center",
						height: "100%",
					}}
				/>
			) : error ? (
				<Result
					css={{
						display: "flex",
						flexDirection: "column",
						justifyContent: "center",
						alignItems: "center",
						height: "100%",
					}}
					status="warning"
					title="Fail to connect to pipeline API"
					subTitle={"Please click the button below, reload the API"}
					extra={
						<Button type="primary" key="console" onClick={reload}>
							Reload
						</Button>
					}
				/>
			) : (
				children
			)}
		</Modal>
	);
};

export default PipelineSettingModal;
