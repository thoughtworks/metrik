import React, { FC } from "react";
import { Tooltip } from "antd";
import { InfoCircleOutlined } from "@ant-design/icons/lib/icons";

interface HintIconProps {
	text: string;
	tooltip: string;
}

const HintIcon: FC<HintIconProps> = ({ text, tooltip }) => {
	return (
		<div>
			{text}
			<Tooltip placement={"topLeft"} arrowPointAtCenter={true} title={tooltip}>
				<InfoCircleOutlined css={{ marginLeft: 6, color: "rgba(0, 0, 0, 0.45)" }} />
			</Tooltip>
		</div>
	);
};

export default HintIcon;
