import React, { FC } from "react";
import { Table } from "antd";
import { PipelineConfiguration } from "../clients/apis";
import { DownOutlined, RightOutlined } from "@ant-design/icons";

const { Column } = Table;

interface DashboardConfigurationProps {
	pipelineConfigurations: PipelineConfiguration[];
}

const DashboardConfig: FC<DashboardConfigurationProps> = ({ pipelineConfigurations }) => {
	return (
		<Table<PipelineConfiguration>
			css={{
				minHeight: 350,
				".ant-table-cell": { borderBottom: "none" },
				".ant-table-expanded-row > .ant-table-cell": {
					background: "unset",
				},
			}}
			pagination={false}
			rowKey={"id"}
			dataSource={pipelineConfigurations}
			expandable={{
				columnWidth: 45,
				expandedRowRender: ToolConfig,
				expandIcon: ({ expanded, onExpand, record }) =>
					expanded ? (
						<DownOutlined onClick={e => onExpand(record, e)} />
					) : (
						<RightOutlined onClick={e => onExpand(record, e)} />
					),
			}}>
			<Column<PipelineConfiguration> title={"Pipeline Name"} dataIndex={"name"} />
			<Column<PipelineConfiguration> title={"Tool"} dataIndex={"type"} />
			<Column<PipelineConfiguration> title={"Last update time"} dataIndex={"lastUpdateTime"} />
			<Column<PipelineConfiguration> title={"Action"} />
		</Table>
	);
};

interface ToolConfig {
	id: string;
	type: string;
	tool: string;
	url: string;
}

const ToolConfig = (config: PipelineConfiguration) => {
	const convertConfig = (config: PipelineConfiguration): ToolConfig[] => {
		return [{ id: config.id, type: "Pipelines", tool: config.type, url: config.url }];
	};
	const data = convertConfig(config);

	return (
		<Table<ToolConfig>
			css={{
				".ant-table-cell": {
					background: "#fafafa",
				},
			}}
			pagination={false}
			rowKey={"id"}
			dataSource={data}>
			<Column<ToolConfig> title={"Type"} dataIndex={"type"} />
			<Column<ToolConfig> title={"Tool"} dataIndex={"tool"} />
			<Column<ToolConfig> title={"URL"} dataIndex={"url"} />
		</Table>
	);
};

export default DashboardConfig;
