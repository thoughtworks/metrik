import React, { FC } from "react";
import { ColourLegend } from "../../../components/ColourLegend";
import { metricsStanderMapping, metricsExplanations } from "../../../constants/metrics";
import { Typography } from "antd";
import { GRAY_1 } from "../../../constants/styles";
import { MetricType } from "../../../__types__/base";
import { MetricsUnit } from "../../../clients/metricsApis";

const { Title, Paragraph } = Typography;

const titleStyle = { color: GRAY_1, fontSize: 14 };

export const MetricInfo: FC<{ unit: MetricsUnit; type: MetricType }> = ({ unit, type }) => (
	<div css={{ padding: 10 }}>
		<Title style={titleStyle} level={5}>
			What is it?
		</Title>
		<Paragraph style={{ color: GRAY_1 }}>{metricsExplanations[type]}</Paragraph>
		<ColourLegend
			elite={metricsStanderMapping[unit][type].elite}
			high={metricsStanderMapping[unit][type].high}
			medium={metricsStanderMapping[unit][type].medium}
			low={metricsStanderMapping[unit][type].low}
		/>
	</div>
);
