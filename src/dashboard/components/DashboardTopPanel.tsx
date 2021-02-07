import { EditableText } from "../../components/EditableText";
import { Button, Typography, DatePicker, Form, Row, Col, Select } from "antd";
import { SyncOutlined, FullscreenOutlined } from "@ant-design/icons";
import PipelineSetting from "./PipelineSetting";
import React, { FC, useEffect, useState } from "react";
import { css } from "@emotion/react";
import { PRIMARY_COLOR, SECONDARY_COLOR } from "../../constants/styles";
import { useRequest } from "../../hooks/useRequest";
import {
	getDashboardDetailsUsingGet,
	updateBuildsUsingPost,
	updateDashboardNameUsingPut,
	getPipelineStagesUsingGet,
	PipelineStagesResponse,
	getLastSynchronizationUsingGet,
} from "../../clients/apis";
import moment from "moment";
import { dateFormatYYYYMMDD } from "../../constants/date-format";
import { MultipleCascadeSelect } from "../../components/MultipleCascadeSelect";
import { isEmpty, isEqual } from "lodash";
import { formatLastUpdateTime } from "../../utils/timeFormats";
import { usePrevious } from "../../hooks/usePrevious";

const { Text } = Typography;
const { RangePicker } = DatePicker;

const containerStyles = css({
	padding: "29px 32px",
});

const headerStyles = css({
	display: "flex",
	alignItems: "center",
	justifyContent: "space-between",
});

const fullScreenStyles = css({
	backgroundColor: SECONDARY_COLOR,
	fontSize: 16,
	borderRadius: 4,
	padding: 10,
	marginLeft: 24,
	cursor: "pointer",
});

const fullScreenIconStyles = css({
	color: PRIMARY_COLOR,
});

const fullScreenTextStyles = css({ marginLeft: 10, color: PRIMARY_COLOR });

const dividerStyles = css({
	display: "inline-block",
	borderRight: `1px solid ${SECONDARY_COLOR}`,
	padding: "4px 24px 4px 0",
});

const transformPipelineStages = (data: typeof getPipelineStagesUsingGet.TResp = []) =>
	data.map((v: PipelineStagesResponse) => ({
		label: v.pipelineName,
		value: v.pipelineId,
		children: v.stages.map((stageName: string) => ({
			label: stageName,
			value: stageName,
		})),
	}));

export type FormValueUnit = "Fortnightly" | "Monthly";

export interface FormValues {
	duration: [moment.Moment, moment.Moment];
	pipelines: Array<{ value: string; childValue: string }>;
	unit: FormValueUnit;
}

interface DashboardTopPanelProps {
	dashboardId: string;
	onApply: (formValues: FormValues) => void;
}

export const DashboardTopPanel: FC<DashboardTopPanelProps> = ({ dashboardId, onApply }) => {
	const defaultValues = {
		duration: [
			moment(new Date(), dateFormatYYYYMMDD).startOf("day"),
			moment(new Date(), dateFormatYYYYMMDD).endOf("day").subtract(4, "month"),
		] as any,
		unit: "Fortnightly",
		pipelines: [],
	} as FormValues;
	const [dashboard, getDashboardRequest] = useRequest(getDashboardDetailsUsingGet);
	const [, updateBuildsRequest, syncing] = useRequest(updateBuildsUsingPost);
	const [, updateDashboardNameRequest] = useRequest(updateDashboardNameUsingPut);
	const [synchronization, getLastSynchronizationRequest] = useRequest(
		getLastSynchronizationUsingGet
	);
	const [pipelineStagesResp, getPipelineStagesRequest, , setPipelineStages] = useRequest(
		getPipelineStagesUsingGet
	);
	const pipelineStages = transformPipelineStages(pipelineStagesResp);

	const updateDashboardName = async (name: string) => {
		await updateDashboardNameRequest({
			dashboardId,
			requestBody: name,
		});
		getDashboard();
	};

	const getLastSyncTime = async () => {
		const resp = await getLastSynchronizationRequest({ dashboardId });
		if (!resp?.synchronizationTimestamp) {
			syncBuilds();
		}
	};

	const syncBuilds = async () => {
		await updateBuildsRequest({
			dashboardId,
		});

		getLastSyncTime();

		setPipelineStages([]);
		getPipelineStages();
	};

	const getDashboard = () => getDashboardRequest({ dashboardId });

	const getPipelineStages = () => getPipelineStagesRequest({ dashboardId });

	useEffect(() => {
		getLastSyncTime();
		getPipelineStages();
		getDashboard();
	}, []);

	const [formValues, setFormValues] = useState<FormValues>(defaultValues);
	const prevPipelines = usePrevious(formValues.pipelines);
	const options = isEmpty(pipelineStages[0]?.children) ? [] : pipelineStages;
	const prevOptions = usePrevious(options);

	useEffect(() => {
		if (isEmpty(formValues.pipelines)) {
			return;
		}

		if (
			(isEmpty(prevPipelines) && !isEmpty(formValues.pipelines)) ||
			(!isEmpty(options) && !isEqual(prevOptions, options))
		) {
			onApply && onApply(formValues);
		}
	}, [formValues.pipelines, options]);

	return (
		<div css={containerStyles}>
			<div css={headerStyles}>
				<div>
					{dashboard?.name && (
						<EditableText defaultValue={dashboard?.name ?? ""} onEditDone={updateDashboardName} />
					)}
					<Text type={"secondary"}>
						The latest available data end at :{" "}
						{formatLastUpdateTime(synchronization?.synchronizationTimestamp)}
					</Text>
					<Button type="link" icon={<SyncOutlined />} loading={syncing} onClick={syncBuilds}>
						{syncing ? "Synchronizing...." : "Sync Data"}
					</Button>
				</div>
				<div>
					<span css={dividerStyles}>
						<PipelineSetting dashboardId={dashboardId} />
					</span>
					<span css={fullScreenStyles}>
						<FullscreenOutlined css={fullScreenIconStyles} />
						<Text css={fullScreenTextStyles}>Full Screen</Text>
					</span>
				</div>
			</div>
			<div css={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
				<Form
					layout={"vertical"}
					css={{ marginTop: 16, width: "50%" }}
					initialValues={defaultValues}
					onFinish={() => {
						if (isEmpty(formValues.pipelines)) {
							return;
						}
						onApply && onApply(formValues);
					}}
					onValuesChange={(_, values) => setFormValues(values)}>
					<Row wrap={false} gutter={12}>
						<Col>
							<Form.Item label="Duration" name="duration">
								<RangePicker format={dateFormatYYYYMMDD} clearIcon={false} />
							</Form.Item>
						</Col>
						<Col span={10}>
							<Form.Item label="Pipelines" name="pipelines">
								<MultipleCascadeSelect
									options={options}
									defaultValues={
										!isEmpty(pipelineStages[0]?.children)
											? [
													{
														value: formValues.pipelines[0]?.value || pipelineStages[0]?.value,
														childValue:
															formValues.pipelines[0]?.childValue ||
															(pipelineStages[0]?.children ?? [])[0]?.label,
													},
											  ]
											: []
									}
								/>
							</Form.Item>
						</Col>
						<Col span={4}>
							<Form.Item label="Unit" name="unit">
								<Select>
									<Select.Option value="Fortnightly">Fortnightly</Select.Option>
									<Select.Option value="Monthly">Monthly</Select.Option>
								</Select>
							</Form.Item>
						</Col>
						<Col style={{ textAlign: "right" }}>
							<Form.Item label=" ">
								<Button htmlType="submit" disabled={syncing || isEmpty(options)}>
									Apply
								</Button>
							</Form.Item>
						</Col>
					</Row>
				</Form>
			</div>
		</div>
	);
};
