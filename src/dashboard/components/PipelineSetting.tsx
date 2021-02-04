import React, { FC, useState } from "react";
import { SettingOutlined } from "@ant-design/icons";
import { css } from "@emotion/react";
import { Typography } from "antd";
import PipelineSettingModal from "../../components/PipelineSettingModal";

const settingStyles = css({
	fontSize: 16,
	padding: "5px 0",
	cursor: "pointer",
});

const settingTextStyles = css({
	marginLeft: 10,
});

const { Text } = Typography;
const PipelineSetting: FC<{ dashboardId: string }> = ({ dashboardId }) => {
	const [visible, setVisible] = useState(false);

	function handleToggleVisible() {
		setVisible(!visible);
	}

	return (
		<>
			<span css={settingStyles} onClick={handleToggleVisible}>
				<SettingOutlined />
				<Text css={settingTextStyles}>Pipeline Setting</Text>
			</span>
			<PipelineSettingModal
				dashboardId={dashboardId}
				visible={visible}
				handleToggleVisible={handleToggleVisible}
			/>
		</>
	);
};

export default PipelineSetting;
