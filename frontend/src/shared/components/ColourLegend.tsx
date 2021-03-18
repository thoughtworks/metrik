import React, { FC } from "react";
import { Colour, LegendRect } from "./LegendRect";
import { Col, Row, Typography } from "antd";
import { GRAY_1 } from "../constants/styles";

const { Text, Title } = Typography;

interface ColourLegendProps {
	elite: string;
	high: string;
	medium: string;
	low: string;
}

const textStyle = { color: GRAY_1, fontSize: 12 };

export const ColourLegend: FC<ColourLegendProps> = ({ elite, high, medium, low }) => (
	<div>
		<Title style={{ color: GRAY_1, fontSize: 14 }} level={5}>
			How to evaluate it?
		</Title>
		<Row align={"middle"} justify={"space-between"} css={{ marginBottom: 8 }}>
			<Col>
				<LegendRect color={Colour.green} text={"ELITE"} />
			</Col>
			<Col>
				<Text style={textStyle}>{elite}</Text>
			</Col>
		</Row>
		<Row align={"middle"} justify={"space-between"} css={{ marginBottom: 8 }}>
			<Col>
				<LegendRect color={Colour.blue} text={"HIGH"} />
			</Col>
			<Col>
				<Text style={textStyle}>{high}</Text>
			</Col>
		</Row>
		<Row align={"middle"} justify={"space-between"} css={{ marginBottom: 8 }}>
			<Col>
				<LegendRect color={Colour.orange} text={"MEDIUM"} />
			</Col>
			<Col>
				<Text style={textStyle}>{medium}</Text>
			</Col>
		</Row>
		<Row align={"middle"} justify={"space-between"}>
			<Col>
				<LegendRect color={Colour.red} text={"LOW"} />
			</Col>
			<Col>
				<Text style={textStyle}>{low}</Text>
			</Col>
		</Row>
	</div>
);
