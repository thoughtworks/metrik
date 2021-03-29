import React, { FC } from "react";
import { Tooltip, Typography, Button } from "antd";
import { InfoCircleOutlined } from "@ant-design/icons/lib/icons";
import { HINT_ICON_COLOR, OVERLAY_COLOR } from "../constants/styles";

interface HintIconProps {
	text?: string;
	tooltip: string;
}

const { Text } = Typography;

const HintIcon: FC<HintIconProps> = ({ text, tooltip }) => {
	return (
		<>
			{text ? <Text>{text}</Text> : null}
			<Tooltip
				placement={"topLeft"}
				arrowPointAtCenter={true}
				title={tooltip}
				overlayInnerStyle={{ backgroundColor: OVERLAY_COLOR }}>
				<Button icon={<InfoCircleOutlined css={{ color: HINT_ICON_COLOR }} />} type={"text"} />
			</Tooltip>
		</>
	);
};

export default HintIcon;
