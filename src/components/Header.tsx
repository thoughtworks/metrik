import { css } from "@emotion/react";
import React, { FC } from "react";

const headerStyles = css({
	height: 64,
	padding: "0 1138px 0 29px",
	boxShadow: "0 2px 8px 0 #f0f1f2",
	backgroundColor: "#ffffff",
});
console.log("headerStyle", headerStyles);

const Header: FC = () => {
	return <div css={headerStyles}>Header</div>;
};

export default Header;
