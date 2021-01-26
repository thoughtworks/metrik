import React, { FC } from "react";
import { Button, Table } from "antd";
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
				columnWidth: "5%",
				expandedRowRender: ToolConfig,
				expandIcon: ({ expanded, onExpand, record }) =>
					expanded ? (
						<DownOutlined onClick={e => onExpand(record, e)} />
					) : (
						<RightOutlined onClick={e => onExpand(record, e)} />
					),
			}}>
			<Column<PipelineConfiguration> width={"20%"} title={"Pipeline Name"} dataIndex={"name"} />
			<Column<PipelineConfiguration> width={"25%"} title={"Tool"} dataIndex={"type"} />
			<Column<PipelineConfiguration>
				width={"25%"}
				title={"Last update time"}
				dataIndex={"lastUpdateTime"}
			/>
			<Column<PipelineConfiguration>
				width={"25%"}
				title={() => (
					<div css={{ display: "flex", justifyContent: "space-between", alignItems: "baseline" }}>
						Actions <Button type={"link"}>+Add Project</Button>
					</div>
				)}
				render={() => (
					<div>
						<Button type={"link"} css={{ padding: "4px 4px 4px 0" }}>
							Duplicate
						</Button>
						<Button type={"link"} css={{ padding: 4 }}>
							Config
						</Button>
					</div>
				)}
			/>
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
				".ant-table-thead .ant-table-cell": {
					fontWeight: "normal",
					opacity: 0.8,
				},
			}}
			pagination={false}
			rowKey={"id"}
			dataSource={data}>
			<Column<ToolConfig> width={"20%"} title={"Type"} dataIndex={"type"} />
			<Column<ToolConfig> width={"27%"} title={"Tool"} dataIndex={"tool"} />
			<Column<ToolConfig> title={"URL"} dataIndex={"url"} />
		</Table>
	);
};

export default DashboardConfig;
