import { MetricInfo } from "./MetricInfo";
import { Button, Tooltip } from "antd";
import { InfoCircleOutlined } from "@ant-design/icons";
import React, { FC } from "react";
import { DurationUnit, MetricType } from "../../shared/__types__/base";
import { GRAY_13, HINT_ICON_COLOR, OVERLAY_COLOR } from "../../shared/constants/styles";

export const MetricTooltip: FC<{ durationUnit: DurationUnit; type: MetricType }> = ({
	durationUnit,
	type,
}) => (
	<Tooltip
		color={GRAY_13}
		placement={"bottomLeft"}
		arrowPointAtCenter
		overlayInnerStyle={{ backgroundColor: OVERLAY_COLOR }}
		title={<MetricInfo durationUnit={durationUnit} type={type} />}>
		<Button icon={<InfoCircleOutlined css={{ color: HINT_ICON_COLOR }} />} type={"text"} />
	</Tooltip>
);
