import React from "react";
import { PageDashboard } from "./dashboard/PageDashboard";
import { PageConfig } from "./config/PageConfig";
import { Redirect, Route } from "react-router-dom";

export const Routes = ({ dashboardId }: { dashboardId: string | undefined }) => (
	<>
		<Route path={"/"} exact>
			{dashboardId ? (
				<Redirect to={{ pathname: "/dashboard", search: `?dashboardId=${dashboardId}` }} />
			) : (
				<Redirect to={{ pathname: "/config" }} />
			)}
		</Route>

		<Route path={"/dashboard"} exact>
			{dashboardId ? <PageDashboard /> : <Redirect to={{ pathname: "/config" }} />}
		</Route>

		<Route path={"/config"} exact>
			{dashboardId ? (
				<Redirect to={{ pathname: "/dashboard", search: `?dashboardId=${dashboardId}` }} />
			) : (
				<PageConfig />
			)}
		</Route>
	</>
);
