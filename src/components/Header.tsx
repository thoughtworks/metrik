import { css } from "@emotion/react";
import React, { FC } from "react";
import logo from "../assets/images/logo.png";

const headerStyles = css({
	height: 64,
	padding: "8px 24px",
	boxShadow: "0 2px 4px #f0f1f2",
	backgroundColor: "#ffffff",
	display: "flex",
	alignItems: "center",
});

const logoStyles = css({
	height: 32,
	width: 32,
});

const logoTextStyles = css({
	fontsSize: 24,
	color: "#096DD9",
	marginLeft: 10,
});

const Header: FC = () => {
	return (
		<div css={headerStyles}>
			<img src={logo} css={logoStyles} alt="logo" />
			<span css={logoTextStyles}>
				<span>4 KEY </span>
				<span css={css({ fontWeight: "bold" })}>METRICS</span>
			</span>
		</div>
	);
};

export default Header;
