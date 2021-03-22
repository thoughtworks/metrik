import { mapMetricsList, mapPipelines } from "./fullScreenDataProcess";
import { MetricsLevel } from "../../../../shared/__types__/enum";
import { MetricsUnit } from "../../../../shared/clients/metricsApis";

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
				metricsDataLabel: "AVG/Times / Fortnightly",
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
				metricsDataLabel: "AVG%",
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
		const originalPipelines = [
			{
				value: "PA",
				childValue: "2km: a",
			},
			{
				value: "PB",
				childValue: "2km: b",
			},
		];
		const result = mapPipelines(originalPipelines);
		const expectedPipelines = ["2km: a", "2km: b"];
		expect(expectedPipelines).toEqual(result);
	});
});
