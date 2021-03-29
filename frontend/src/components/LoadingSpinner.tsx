import React, { FC } from "react";
import { Spin } from "antd";
import { Loading3QuartersOutlined } from "@ant-design/icons";

const Spinner = <Loading3QuartersOutlined style={{ fontSize: 36 }} spin />;

export const LoadingSpinner: FC = () => {
	return <Spin indicator={Spinner} />;
};
