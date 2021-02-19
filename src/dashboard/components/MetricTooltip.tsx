import { MetricInfo } from "./MetricInfo";
import { Button, Tooltip } from "antd";
import { InfoCircleOutlined } from "@ant-design/icons";
import React, { FC } from "react";
import { DurationUnit, MetricType } from "../../__types__/base";

export const MetricTooltip: FC<{ durationUnit: DurationUnit; type: MetricType }> = ({
	durationUnit,
	type,
}) => (
	<Tooltip
		placement={"bottomLeft"}
		arrowPointAtCenter
		title={<MetricInfo durationUnit={durationUnit} type={type} />}>
		<Button icon={<InfoCircleOutlined />} type={"text"} />
	</Tooltip>
);
