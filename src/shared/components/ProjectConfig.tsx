import React, { FC } from "react";
import { Button, Table } from "antd";
import { PipelineResponse } from "../clients/apis";

const { Column } = Table;

interface ProjectConfigProps {
	pipelines: PipelineResponse[];
	showDelete?: boolean;
	showAddPipeline?: boolean;
	updatePipeline: (pipeline: PipelineResponse) => void;
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
		<Table<PipelineResponse>
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
			<Column<PipelineResponse> width={"15%"} title={"Pipeline Name"} dataIndex={"name"} />
			<Column<PipelineResponse> width={"15%"} title={"Tool"} dataIndex={"type"} />
			<Column<PipelineResponse> width={"45%"} title={"URL"} dataIndex={"url"} />
			<Column<PipelineResponse>
				width={"25%"}
				title={() => (
					<div css={{ display: "flex", justifyContent: "space-between", alignItems: "baseline" }}>
						Action{showDelete ? "s" : ""}
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
