import React, { FC } from "react";
import ReactDom from "react-dom";
import { Routes } from "./Routes";
import { BrowserRouter as Router, Switch } from "react-router-dom";
import Header from "./components/Header";

export const App: FC = () => {
	return (
		<Router>
			<Header />
			<Switch>
				<Routes />
			</Switch>
		</Router>
	);
};

ReactDom.render(<App />, document.getElementById("root"));
