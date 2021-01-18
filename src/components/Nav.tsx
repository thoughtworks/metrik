import { Link } from "react-router-dom";
import React from "react";
import { css } from "@emotion/react";

const linkStyles = css({
	fontSize: 14,
	color: "#000",
	marginRight: 15,
});

export const Nav = () => (
	<>
		<Link css={linkStyles} to={"/"}>
			Dashboard
		</Link>
		<Link css={linkStyles} to={"/config"}>
			Configuration
		</Link>
	</>
);
