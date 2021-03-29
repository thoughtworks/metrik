import React, { FC, useEffect } from "react";
import ReactDom from "react-dom";
import { Routes } from "./routes/Routes";
import { BrowserRouter as Router } from "react-router-dom";
import Header from "./shared/components/Header";
import { useRequest } from "./shared/hooks/useRequest";
import { Global } from "@emotion/react";
import { getProjectsUsingGet } from "./shared/clients/projectApis";
import { setResponsive } from "./shared/utils/responsive/responsive";
import "./shared/assets/fonts/fonts.less";
import { globalStyles } from "./globalStyle";

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
				<Routes projectId={projects[0]?.id} />
			</Router>
		</>
	) : null;
};

ReactDom.render(<App />, document.getElementById("root"));
