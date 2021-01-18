import { Button } from "antd";
import React, { FC, useState } from "react";
import ReactDom from "react-dom";

export const App: FC = () => {
	const [state] = useState(1);

	return (
		<div css={{ color: "red" }}>
			Hello 4 key metrics <Button>Click me</Button>
		</div>
	);
};

ReactDom.render(<App />, document.getElementById("root"));
