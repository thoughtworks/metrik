import React from "react";
import { PageDashboard } from "../pages/dashboard/PageDashboard";
import { PageConfig } from "../pages/config/PageConfig";
import { Redirect, Route, Switch } from "react-router-dom";
import { renderRoutes, RouteConfig } from "react-router-config";
import { useQuery } from "../shared/hooks/useQuery";

const INITIAL_ROUTES: RouteConfig[] = [
	{
		path: "/config",
		exact: true,
		component: PageConfig,
	},
	{
		render: () => <Redirect to={"/config"} />,
	},
];

export const Routes = ({ projectId }: { projectId: string | undefined }) => {
	const query = useQuery();
	const queryId = query.get("projectId");

	if (!projectId) {
		return renderRoutes(INITIAL_ROUTES);
	}

	return (
		<Switch>
			<Route path={"/project"} exact>
				{queryId === projectId ? (
					<PageDashboard />
				) : (
					<Redirect to={{ pathname: "/project", search: `?projectId=${projectId}` }} />
				)}
			</Route>
			<Redirect to={{ pathname: "/project", search: `?projectId=${projectId}` }} />
		</Switch>
	);
};
