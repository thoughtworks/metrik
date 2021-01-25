import { Button } from "antd";
import React, { FC } from "react";
import Success from "../../assets/icons/Success";

const ConfigSuccess: FC = () => {
	return (
		<div css={{ display: "flex", alignItems: "center" }}>
			<Success css={{ flexBasis: 85 }} />
			<div
				css={{
					display: "flex",
					flexDirection: "column",
					alignItems: "flex-start",
					fontSize: 24,
					color: "black",
					margin: "0 30px",
				}}>
				<span>Pipeline successfully created!</span>
				<span
					css={{
						fontSize: 14,
						opacity: 0.5,
					}}>
					The initial configuration is complete，4 Key Metrics will be presented based on the
					following projects
				</span>
			</div>
			<Button type={"primary"} size={"large"}>
				Go to Dashboard
			</Button>
		</div>
	);
};

export default ConfigSuccess;
