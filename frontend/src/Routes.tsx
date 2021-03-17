import React from "react";
import { PageDashboard } from "./dashboard/PageDashboard";
import { PageConfig } from "./config/PageConfig";
import { Redirect, Route } from "react-router-dom";
import Fullscreen from "./fullscreen/Fullscreen";

export const Routes = ({ projectId }: { projectId: string | undefined }) => (
	<>
		<Route path={"/"} exact>
			{projectId ? (
				<Redirect to={{ pathname: "/project", search: `?projectId=${projectId}` }} />
			) : (
				<Redirect to={{ pathname: "/config" }} />
			)}
		</Route>

		<Route path={"/project"} exact>
			{projectId ? <PageDashboard /> : <Redirect to={{ pathname: "/config" }} />}
		</Route>

		<Route path={"/config"} exact>
			{projectId ? (
				<Redirect to={{ pathname: "/project", search: `?projectId=${projectId}` }} />
			) : (
				<PageConfig />
			)}
		</Route>

		<Route path={"/fullscreen"} exact>
			<Fullscreen />
		</Route>
	</>
);
