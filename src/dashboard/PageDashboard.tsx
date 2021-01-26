import React from "react";
import { SettingOutlined, FullscreenOutlined, SyncOutlined } from "@ant-design/icons";
import { Typography, Button } from "antd";
import { SECONDARY_COLOR, PRIMARY_COLOR } from "../constants/styles";
import { css } from "@emotion/react";

const { Text, Title } = Typography;

const containerStyles = css({
	display: "flex",
	alignItems: "center",
	justifyContent: "space-between",
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

const fullScreenTextStyles = css({ marginLeft: 10, color: PRIMARY_COLOR });

export const PageDashboard = () => {
	return (
		<div css={containerStyles}>
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
	);
};
