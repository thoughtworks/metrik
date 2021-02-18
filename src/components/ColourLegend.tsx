import React, { FC } from "react";
import { LegendRect } from "./LegendRect";
import { Typography } from "antd";
import { GRAY_1 } from "../constants/styles";
import { Row, Col } from "antd";

const { Text, Paragraph } = Typography;

interface ColourLegendProps {
	elite: string;
	high: string;
	medium: string;
	low: string;
}

export const ColourLegend: FC<ColourLegendProps> = ({ elite, high, medium, low }) => (
	<div>
		<Paragraph style={{ color: GRAY_1 }}>How to evaluate it?</Paragraph>
		<Row align={"middle"} justify={"space-between"}>
			<Col>
				<LegendRect color={"green"} />
			</Col>
			<Col>
				<Text style={{ color: GRAY_1 }}>{elite}</Text>
			</Col>
		</Row>
		<Row align={"middle"} justify={"space-between"}>
			<Col>
				<LegendRect color={"blue"} />
			</Col>
			<Col>
				<Text style={{ color: GRAY_1 }}>{high}</Text>
			</Col>
		</Row>
		<Row align={"middle"} justify={"space-between"}>
			<Col>
				<LegendRect color={"orange"} />
			</Col>
			<Col>
				<Text style={{ color: GRAY_1 }}>{medium}</Text>
			</Col>
		</Row>
		<Row align={"middle"} justify={"space-between"}>
			<Col>
				<LegendRect color={"red"} />
			</Col>
			<Col>
				<Text style={{ color: GRAY_1 }}>{low}</Text>
			</Col>
		</Row>
	</div>
);
