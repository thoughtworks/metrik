import React, { FC } from "react";
import { ColourLegend } from "../../components/ColourLegend";
import { DurationUnit, MetricType } from "../../__types__/base";
import { metricsStanderMapping, metricsExplanations } from "../../constants/metrics";
import { Typography } from "antd";
import { GRAY_1 } from "../../constants/styles";

const { Title, Paragraph } = Typography;

const titleStyle = { color: GRAY_1, fontSize: 14 };

export const MetricInfo: FC<{ durationUnit: DurationUnit; type: MetricType }> = ({
	durationUnit,
	type,
}) => (
	<div>
		<Title style={titleStyle} level={5}>
			What is it?
		</Title>
		<Paragraph style={{ color: GRAY_1 }}>{metricsExplanations[type]}</Paragraph>
		<ColourLegend
			elite={metricsStanderMapping[durationUnit][type].elite}
			high={metricsStanderMapping[durationUnit][type].high}
			medium={metricsStanderMapping[durationUnit][type].medium}
			low={metricsStanderMapping[durationUnit][type].low}
		/>
	</div>
);
