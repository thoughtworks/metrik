import React, { FC } from "react";
import { Tooltip, Typography } from "antd";
import { InfoCircleOutlined } from "@ant-design/icons/lib/icons";

interface HintIconProps {
	text?: string;
	tooltip: string;
}

const { Text } = Typography;

const HintIcon: FC<HintIconProps> = ({ text, tooltip }) => {
	return (
		<>
			{text ? <Text>{text}</Text> : null}
			<Tooltip placement={"topLeft"} arrowPointAtCenter={true} title={tooltip}>
				<InfoCircleOutlined
					css={{ marginLeft: 6, color: "rgba(0, 0, 0, 0.45)", cursor: "pointer" }}
				/>
			</Tooltip>
		</>
	);
};

export default HintIcon;
