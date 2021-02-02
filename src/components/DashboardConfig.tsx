import React, { FC } from "react";
import { Button, Table } from "antd";
import { DownOutlined, RightOutlined } from "@ant-design/icons";
import { Pipeline } from "../clients/apis";

const { Column } = Table;

interface DashboardConfigurationProps {
	pipelines: Pipeline[];
}

const DashboardConfig: FC<DashboardConfigurationProps> = ({ pipelines }) => {
	return (
		<Table<Pipeline>
			css={{
				minHeight: 350,
				".ant-table-cell": { borderBottom: "none" },
				".ant-table-expanded-row > .ant-table-cell": {
					background: "unset",
				},
			}}
			pagination={false}
			rowKey={"id"}
			dataSource={pipelines}
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
			<Column<Pipeline> width={"25%"} title={"Pipeline Name"} dataIndex={"name"} />
			<Column<Pipeline> width={"35%"} title={"Tool"} dataIndex={"type"} />
			<Column<Pipeline>
				width={"35%"}
				title={() => (
					<div css={{ display: "flex", justifyContent: "space-between", alignItems: "baseline" }}>
						Actions <Button type={"link"}>+Add Pipeline</Button>
					</div>
				)}
				render={() => (
					<Button type={"link"} css={{ padding: 4 }}>
						Config
					</Button>
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

const ToolConfig = (config: Pipeline) => {
	const convertConfig = (config: Pipeline): ToolConfig[] => {
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
			<Column<ToolConfig> width={"26%"} title={"Type"} dataIndex={"type"} />
			<Column<ToolConfig> width={"26%"} title={"Tool"} dataIndex={"tool"} />
			<Column<ToolConfig> title={"URL"} dataIndex={"url"} />
		</Table>
	);
};

export default DashboardConfig;
