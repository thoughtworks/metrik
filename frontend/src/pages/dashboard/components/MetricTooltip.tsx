import { MetricInfo } from "./MetricInfo";
import { Button, Tooltip } from "antd";
import { InfoCircleOutlined } from "@ant-design/icons";
import React, { FC } from "react";
import { MetricType } from "../../../shared/__types__/base";
import { GRAY_13, HINT_ICON_COLOR, OVERLAY_COLOR } from "../../../shared/constants/styles";
import { MetricsUnit } from "../../../shared/clients/metricsApis";

export const MetricTooltip: FC<{ unit: MetricsUnit; type: MetricType }> = ({ unit, type }) => (
	<Tooltip
		color={GRAY_13}
		placement={"bottomLeft"}
		arrowPointAtCenter
		overlayInnerStyle={{ backgroundColor: OVERLAY_COLOR }}
		title={<MetricInfo unit={unit} type={type} />}>
		<Button icon={<InfoCircleOutlined css={{ color: HINT_ICON_COLOR }} />} type={"text"} />
	</Tooltip>
);
