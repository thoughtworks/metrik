import React, { FC, useEffect } from "react";
import ReactDom from "react-dom";
import { Routes } from "./Routes";
import { BrowserRouter as Router, Switch } from "react-router-dom";
import Header from "./components/Header";
import { getDashboardsUsingGet } from "./clients/apis";
import { useRequest } from "./hooks/useRequest";

export const App: FC = () => {
	const [dashboards, getDashboardsRequest] = useRequest(getDashboardsUsingGet);

	useEffect(() => {
		getDashboardsRequest(undefined);
	}, []);

	return dashboards !== undefined ? (
		<Router>
			<Header />
			<Switch>
				<Routes dashboardId={dashboards[0]?.id} />
			</Switch>
		</Router>
	) : null;
};

ReactDom.render(<App />, document.getElementById("root"));
