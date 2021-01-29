import React from "react";
import { SettingOutlined, FullscreenOutlined, SyncOutlined } from "@ant-design/icons";
import { Typography, Button, DatePicker, Row, Col, Form, Radio } from "antd";
import { SECONDARY_COLOR, PRIMARY_COLOR } from "../constants/styles";
import { css } from "@emotion/react";
import moment from "moment";
import { dateFormatYYYYMMDD } from "../constants/date-format";
import { MultipleCascadeSelect } from "../components/MultipleCascadeSelect";

const { Text, Title } = Typography;
const { RangePicker } = DatePicker;

const options = [
	{
		label: "AABBBBBBBBBB CCCCCC",
		value: "a",
		children: [
			{
				label: "A1",
				value: "a1",
			},
			{
				label: "A2",
				value: "a2",
			},
			{
				label: "A3",
				value: "a3",
			},
		],
	},
	{
		label: "BB",
		value: "b",
		children: [
			{
				label: "B1",
				value: "b1",
			},
			{
				label: "B2",
				value: "b2",
			},
			{
				label: "B3",
				value: "b3",
			},
		],
	},
	{
		label: "CC",
		value: "c",
		children: [
			{
				label: "C1",
				value: "c1",
			},
			{
				label: "C2",
				value: "c2",
			},
			{
				label: "C3",
				value: "c3",
			},
		],
	},
	{
		label: "8888",
		value: "d",
		children: [
			{
				label: "A1",
				value: "a1",
			},
			{
				label: "A2",
				value: "a2",
			},
			{
				label: "A3",
				value: "a3",
			},
		],
	},
	{
		label: "009890",
		value: "e",
		children: [
			{
				label: "B1",
				value: "b1",
			},
			{
				label: "B2",
				value: "b2",
			},
			{
				label: "B3",
				value: "b3",
			},
		],
	},
	{
		label: "CC11111",
		value: "f",
		children: [
			{
				label: "C1",
				value: "c1",
			},
			{
				label: "C2",
				value: "c2",
			},
			{
				label: "C3",
				value: "c3",
			},
		],
	},
];

const containerStyles = css({
	padding: "29px 32px",
});

const dividerStyles = css({
	display: "inline-block",
	borderRight: `1px solid ${SECONDARY_COLOR}`,
	padding: "4px 24px 4px 0",
});

const settingStyles = css({
	fontSize: 16,
	padding: "5px 0",
	cursor: "pointer",
});

const settingTextStyles = css({
	marginLeft: 10,
});

const fullScreenStyles = css({
	backgroundColor: SECONDARY_COLOR,
	fontSize: 16,
	borderRadius: 4,
	padding: 10,
	marginLeft: 24,
	cursor: "pointer",
});

const fullScreenIconStyles = css({
	color: PRIMARY_COLOR,
});

const headerStyles = css({
	display: "flex",
	alignItems: "center",
	justifyContent: "space-between",
});

const fullScreenTextStyles = css({ marginLeft: 10, color: PRIMARY_COLOR });

export const PageDashboard = () => {
	return (
		<div css={containerStyles}>
			<div css={headerStyles}>
				<div>
					<Title level={2} style={{ marginBottom: 0 }}>
						4KM
					</Title>
					<Text type={"secondary"}>The latest available data end at : 3 Jun, 2020</Text>
					<Button type="link" icon={<SyncOutlined />}>
						Sync Data
					</Button>
				</div>
				<div>
					<span css={dividerStyles}>
						<span css={settingStyles}>
							<SettingOutlined />
							<Text css={settingTextStyles}>Pipeline Setting</Text>
						</span>
					</span>
					<span css={fullScreenStyles}>
						<FullscreenOutlined css={fullScreenIconStyles} />
						<Text css={fullScreenTextStyles}>Full Screen</Text>
					</span>
				</div>
			</div>
			<div css={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
				<Form
					layout={"vertical"}
					css={{ marginTop: 16, width: "50%" }}
					initialValues={{
						duration: [
							moment(new Date(), dateFormatYYYYMMDD).startOf("day"),
							moment(new Date(), dateFormatYYYYMMDD).endOf("day").subtract(4, "month"),
						],
					}}
					onFinish={values => {
						console.log("submit", values);
					}}>
					<Row wrap={false} gutter={12}>
						<Col>
							<Form.Item label="Duration" name="duration">
								<RangePicker format={dateFormatYYYYMMDD} clearIcon={false} />
							</Form.Item>
						</Col>
						<Col span={10}>
							<Form.Item label="Pipelines" name="pipelines">
								<MultipleCascadeSelect
									options={options}
									defaultValues={[{ value: "a", childValue: "a1" }]}
								/>
							</Form.Item>
						</Col>
						<Col style={{ textAlign: "right" }}>
							<Form.Item label=" ">
								<Button htmlType="submit">Apply</Button>
							</Form.Item>
						</Col>
					</Row>
				</Form>

				<Radio.Group defaultValue="fortnightly">
					<Radio.Button value="fortnightly">Fortnightly</Radio.Button>
					<Radio.Button value="monthly">Monthly</Radio.Button>
				</Radio.Group>
			</div>
		</div>
	);
};
