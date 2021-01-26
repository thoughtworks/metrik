import React from "react";
import { PageDashboard } from "./dashboard/PageDashboard";
import { PageConfig } from "./config/PageConfig";
import { Route } from "react-router-dom";

export const Routes = () => {
	return (
		<>
			<Route path={"/"} component={PageDashboard} exact />
			<Route path={"/config"} component={PageConfig} exact />
		</>
	);
};
