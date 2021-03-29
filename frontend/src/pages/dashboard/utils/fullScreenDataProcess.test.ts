import {
	addBaseValueToDetailData,
	isNumber,
	mapMetricsList,
	mapPipelines,
} from "./fullScreenDataProcess";
import { MetricsLevel } from "../../../shared/__types__/enum";
import { MetricsUnit } from "../../../shared/clients/metricsApis";

describe("#fullscreenDataProcess #mapMetricsList", () => {
	test("should map out correct data type given metrics response", () => {
		const itemData = {
			summary: {
				value: 30,
				level: MetricsLevel.LOW,
				endTimestamp: 1616428799000,
				startTimestamp: 1605974400000,
			},
			details: [
				{
					value: "NaN" as const,
					endTimestamp: 1607961599999,
					startTimestamp: 1606752000000,
				},
				{
					value: 15.79,
					endTimestamp: 1615219199999,
					startTimestamp: 1614009600000,
				},
			],
		};
		const metricsResponse = {
			changeFailureRate: itemData,
			deploymentFrequency: itemData,
			leadTimeForChange: itemData,
			meanTimeToRestore: itemData,
		};
		const expectedData = [
			{
				endTimestamp: 1607961599999,
				startTimestamp: 1606752000000,
			},
			{
				value: 15.79,
				endTimestamp: 1615219199999,
				startTimestamp: 1614009600000,
			},
		];
		const expectResult = [
			{
				metricsSummaryData: 30,
				metricsLevel: MetricsLevel.LOW,
				metricsDataLabel: "AVG Times / Fortnightly",
				metricsText: "Deployment Frequency",
				data: expectedData,
			},
			{
				metricsSummaryData: 30,
				metricsLevel: MetricsLevel.LOW,
				metricsDataLabel: "AVG Days",
				metricsText: "Lead Time for Change",
				data: expectedData,
			},
			{
				metricsSummaryData: 30,
				metricsLevel: MetricsLevel.LOW,
				metricsDataLabel: "AVG Hours",
				metricsText: "Mean Time To Restore",
				data: expectedData,
			},
			{
				metricsSummaryData: 30,
				metricsLevel: MetricsLevel.LOW,
				metricsDataLabel: "AVG %",
				metricsText: "Change Failure Rate",
				data: expectedData,
			},
		];
		const result = mapMetricsList(metricsResponse, MetricsUnit.FORTNIGHTLY);
		expect(expectResult).toEqual(result);
	});
});
describe("#fullscreenDataProcess #mapPipelines", () => {
	test("should map out correct pipelines type given pipelines from form data", () => {
		const backendServicePipelineValue = "60594b5c9d70cd32a2798012";
		const frontendServicdePipelineValue = "60594b849d70cd32a279813c";
		const pipelineOptions = [
			{
				label: "backend-service",
				value: backendServicePipelineValue,
				children: [
					{ label: "Deploy to DEV", value: "Deploy to DEV" },
					{ label: "Deploy to UAT", value: "Deploy to UAT" },
				],
			},
			{
				label: "frontend-service",
				value: frontendServicdePipelineValue,
				children: [
					{ label: "Deploy to DEV", value: "Deploy to DEV" },
					{ label: "Deploy to UAT", value: "Deploy to UAT" },
				],
			},
		];

		const selectedStageList = [
			{ value: backendServicePipelineValue, childValue: "Deploy to DEV" },
			{ value: frontendServicdePipelineValue, childValue: "Deploy to UAT" },
		];
		const result = mapPipelines(pipelineOptions, selectedStageList);
		const expectedPipelines = ["backend-service:Deploy to DEV", "frontend-service:Deploy to UAT"];
		expect(result).toEqual(expectedPipelines);
	});
});

describe("#fullscreenDataProcess #isNumber", () => {
	test("should detect whether number when given various type of data", () => {
		expect(isNumber(undefined)).toBe(false);
		expect(isNumber("nana")).toBe(false);
		expect(isNumber(10)).toBe(true);
		expect(isNumber(-10)).toBe(true);
		expect(isNumber(0.1)).toBe(true);
	});
});

describe("#fullscreenDataProcess #addBaseValueToDetailData", () => {
	test("should not add base value to details data when contains none items with number value", () => {
		const originalDetailsData = [
			{
				value: undefined,
				endTimestamp: 1607961599999,
				startTimestamp: 1606752000000,
			},
			{
				value: undefined,
				endTimestamp: 1615219199999,
				startTimestamp: 1614009600000,
			},
		];
		const expectedResult = [...originalDetailsData];
		const result = addBaseValueToDetailData(originalDetailsData);
		expect(expectedResult).toEqual(result);
	});
	test("should add base value to details data when contains only one items with number value", () => {
		const originalDetailsData = [
			{
				value: undefined,
				endTimestamp: 1607961599999,
				startTimestamp: 1606752000000,
			},
			{
				value: 15.79,
				endTimestamp: 1615219199999,
				startTimestamp: 1614009600000,
			},
		];
		const expectedResult = [...originalDetailsData];
		const result = addBaseValueToDetailData(originalDetailsData);
		expect(expectedResult).toEqual(result);
	});

	test("should add base value to details data when contains item of max value and min value are equal", () => {
		const originalDetailsData = [
			{
				value: 10,
				endTimestamp: 1607961599999,
				startTimestamp: 1606752000000,
			},
			{
				value: undefined,
				endTimestamp: 1615219199999,
				startTimestamp: 1614009600000,
			},
			{
				value: 10,
				endTimestamp: 1615219199999,
				startTimestamp: 1614009600000,
			},
		];
		const expectedResult = [...originalDetailsData];
		const result = addBaseValueToDetailData(originalDetailsData);
		expect(expectedResult).toEqual(result);
	});
	test("should add base value to details data when contains item of max value and min value are not equal", () => {
		const originalDetailsData = [
			{
				value: 0.1,
				endTimestamp: 1607961599999,
				startTimestamp: 1606752000000,
			},
			{
				value: undefined,
				endTimestamp: 1607961599999,
				startTimestamp: 1606752000000,
			},
			{
				value: 1.1,
				endTimestamp: 1615219199999,
				startTimestamp: 1614009600000,
			},
		];
		const expectedResult = [
			{
				value: 0.1 + 0.5,
				endTimestamp: 1607961599999,
				startTimestamp: 1606752000000,
			},
			{
				value: undefined,
				endTimestamp: 1607961599999,
				startTimestamp: 1606752000000,
			},
			{
				value: 1.1 + 0.5,
				endTimestamp: 1615219199999,
				startTimestamp: 1614009600000,
			},
		];
		const result = addBaseValueToDetailData(originalDetailsData);
		expect(expectedResult).toEqual(result);
	});
});
