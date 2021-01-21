import { css } from "@emotion/react";
import React, { FC } from "react";
import Title from "../assets/icons/Title";
import Logo from "../assets/icons/Logo";

const headerStyles = css({
	padding: "0 30px",
	boxShadow: "0 2px 4px #f0f1f2",
	backgroundColor: "#ffffff",
	display: "flex",
	alignItems: "center",
});

const Header: FC = () => {
	return (
		<div css={headerStyles}>
			<Logo />
			<Title css={{ marginLeft: 8 }} />
		</div>
	);
};

export default Header;
