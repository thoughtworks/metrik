import React, { FC } from "react";
import { Button, Table } from "antd";
import { DownOutlined, RightOutlined } from "@ant-design/icons";
import { PipelineVoRes } from "../clients/apis";

const { Column } = Table;

interface DashboardConfigurationProps {
	pipelines: PipelineVoRes[];
	showDelete?: boolean;
	showAddPipeline?: boolean;
	updatePipeline: (pipeline: PipelineVoRes) => void;
	addPipeline?: () => void;
	deletePipeline?: (pipelineId: string) => void;
}

const DashboardConfig: FC<DashboardConfigurationProps> = ({
	pipelines,
	showDelete = false,
	showAddPipeline = true,
	updatePipeline,
	addPipeline,
	deletePipeline,
}) => {
	return (
		<Table<PipelineVoRes>
			tableLayout={"fixed"}
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
			<Column<PipelineVoRes> width={"25%"} title={"Pipeline Name"} dataIndex={"name"} />
			<Column<PipelineVoRes> width={"35%"} title={"Tool"} dataIndex={"type"} />
			<Column<PipelineVoRes>
				width={"35%"}
				title={() => (
					<div css={{ display: "flex", justifyContent: "space-between", alignItems: "baseline" }}>
						Actions{" "}
						{showAddPipeline && (
							<Button type={"link"} onClick={addPipeline}>
								+Add Pipeline
							</Button>
						)}
					</div>
				)}
				render={(value, record) => (
					<>
						<Button
							type={"link"}
							css={{ padding: "8px 8px 8px 0" }}
							onClick={() => updatePipeline(record)}>
							Config
						</Button>
						{showDelete && (
							<Button
								type={"link"}
								danger={true}
								css={{ padding: 8 }}
								onClick={() => deletePipeline?.(record.id)}>
								Delete
							</Button>
						)}
					</>
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

const ToolConfig = (config: PipelineVoRes) => {
	const convertConfig = (config: PipelineVoRes): ToolConfig[] => {
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
