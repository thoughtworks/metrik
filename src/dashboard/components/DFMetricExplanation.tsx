import React, { FC } from "react";
import { ColourLegend } from "../../components/ColourLegend";
import { DurationUnit } from "../../__types__/base";
import { metricsStanderMapping, metricsExplanations } from "../../constants/metrics";
import { Typography } from "antd";
import { GRAY_1 } from "../../constants/styles";

const { Title, Paragraph } = Typography;

const titleStyle = { color: GRAY_1, fontSize: 14 };

export const DFMetricExplanation: FC<{ durationUnit: DurationUnit }> = ({ durationUnit }) => (
	<div>
		<Title style={titleStyle} level={5}>
			What is it?
		</Title>
		<Paragraph style={{ color: GRAY_1 }}>{metricsExplanations.df}</Paragraph>
		<ColourLegend
			elite={metricsStanderMapping[durationUnit].df.elite}
			high={metricsStanderMapping[durationUnit].df.high}
			medium={metricsStanderMapping[durationUnit].df.medium}
			low={metricsStanderMapping[durationUnit].df.low}
		/>
	</div>
);
