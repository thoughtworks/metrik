import React from "react";
import { PageDashboard } from "./dashboard/PageDashboard";
import { PageConfig } from "./config/PageConfig";
import { Redirect, Route } from "react-router-dom";

export const Routes = ({ dashboardId }: { dashboardId: string | undefined }) => (
	<>
		<Redirect from={"/config"} to={"/"} exact />
		<Route path={"/"} exact>
			{dashboardId ? (
				<Redirect to={{ pathname: "/dashboard", search: `?dashboardId=${dashboardId}` }} />
			) : (
				<PageConfig />
			)}
		</Route>
		<Route path={"/dashboard"} component={PageDashboard} exact />
	</>
);
