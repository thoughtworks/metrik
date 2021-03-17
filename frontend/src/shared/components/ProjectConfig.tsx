import React, { FC } from "react";
import { Button, Table } from "antd";
import { Pipeline } from "../clients/pipelineApis";

const { Column } = Table;

interface ProjectConfigProps {
	pipelines: Pipeline[];
	showDelete?: boolean;
	showAddPipeline?: boolean;
	updatePipeline: (pipeline: Pipeline) => void;
	addPipeline?: () => void;
	deletePipeline?: (pipelineId: string) => void;
}

const ProjectConfig: FC<ProjectConfigProps> = ({
	pipelines,
	showDelete = false,
	showAddPipeline = true,
	updatePipeline,
	addPipeline,
	deletePipeline,
}) => {
	return (
		<Table<Pipeline>
			tableLayout={"fixed"}
			css={{
				minHeight: 350,
				padding: 12,
				".ant-table-cell": { borderBottom: "none", padding: 10 },
				".ant-table-expanded-row > .ant-table-cell": {
					background: "unset",
				},
			}}
			pagination={false}
			rowKey={"id"}
			dataSource={pipelines}>
			<Column<Pipeline> width={"15%"} title={"Pipeline Name"} dataIndex={"name"} />
			<Column<Pipeline> width={"15%"} title={"Tool"} dataIndex={"type"} />
			<Column<Pipeline> width={"45%"} title={"URL"} dataIndex={"url"} />
			<Column<Pipeline>
				width={"25%"}
				title={() => (
					<div css={{ display: "flex", justifyContent: "space-between", alignItems: "baseline" }}>
						Action{showDelete ? "s" : ""}
						{showAddPipeline && (
							<Button type={"link"} onClick={addPipeline}>
								+ Add Pipeline
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
								onClick={() => deletePipeline?.(record.id)}
								disabled={pipelines.length <= 1}>
								Delete
							</Button>
						)}
					</>
				)}
			/>
		</Table>
	);
};

export default ProjectConfig;
