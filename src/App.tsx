import React, { FC, useEffect } from "react";
import ReactDom from "react-dom";
import { Routes } from "./Routes";
import { BrowserRouter as Router, Switch } from "react-router-dom";
import Header from "./shared/components/Header";
import { useRequest } from "./shared/hooks/useRequest";
import { Global } from "@emotion/react";
import { getProjectsUsingGet } from "./shared/clients/projectApis";
import { setResponsive } from "./shared/utils/responseive/responseive";

const globalStyles = {
	".metric-info-overlay div.ant-tooltip-inner": {
		width: "300px !important",
	},
};
setResponsive();
export const App: FC = () => {
	const [projects, getProjectsRequest] = useRequest(getProjectsUsingGet);

	useEffect(() => {
		getProjectsRequest(undefined);
	}, []);

	return projects !== undefined ? (
		<>
			<Global styles={globalStyles} />
			<Router>
				<Header />
				<Switch>
					<Routes projectId={projects[0]?.id} />
				</Switch>
			</Router>
		</>
	) : null;
};

ReactDom.render(<App />, document.getElementById("root"));
