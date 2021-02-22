import { MetricInfo } from "./MetricInfo";
import { Button, Tooltip } from "antd";
import { InfoCircleOutlined } from "@ant-design/icons";
import React, { FC } from "react";
import { DurationUnit, MetricType } from "../../shared/__types__/base";
import { GRAY_13 } from "../../shared/constants/styles";

export const MetricTooltip: FC<{ durationUnit: DurationUnit; type: MetricType }> = ({
	durationUnit,
	type,
}) => (
	<Tooltip
		color={GRAY_13}
		placement={"bottomLeft"}
		arrowPointAtCenter
		overlayClassName={"metric-info-overlay"}
		title={<MetricInfo durationUnit={durationUnit} type={type} />}>
		<Button icon={<InfoCircleOutlined />} type={"text"} />
	</Tooltip>
);
