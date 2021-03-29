import { css } from "@emotion/react";
import React, { FC } from "react";
import Logo from "./Logo/Logo";
import { Link } from "react-router-dom";

const headerStyles = css({
	padding: "12px 30px 12px 32px",
	boxShadow: "0 2px 4px #f0f1f2",
	backgroundColor: "#ffffff",
	display: "flex",
	alignItems: "center",
});

const Header: FC = () => {
	return (
		<Link to={"/"}>
			<div css={headerStyles}>
				<Logo width={116} height={32} />
			</div>
		</Link>
	);
};

export default Header;
