import React, { FC, useEffect } from "react";
import ReactDom from "react-dom";
import { Routes } from "./Routes";
import { BrowserRouter as Router, Switch } from "react-router-dom";
import Header from "./components/Header";
import { getDashboardsUsingGet } from "./clients/apis";
import { useRequest } from "./hooks/useRequest";
import { isEmpty } from "lodash";

export const App: FC = () => {
	const [dashboards, getDashboardsRequest] = useRequest(getDashboardsUsingGet, []);

	useEffect(() => {
		getDashboardsRequest(undefined);
	}, []);

	console.log(dashboards, "dashboards");
	return isEmpty(dashboards) ? null : (
		<Router>
			<Header />
			<Switch>
				<Routes dashboardId={dashboards[0]?.id} />
			</Switch>
		</Router>
	);
};

ReactDom.render(<App />, document.getElementById("root"));
