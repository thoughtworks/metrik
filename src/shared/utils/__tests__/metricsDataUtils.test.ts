import { cleanMetricsInfo } from "../metricsDataUtils";
import { MetricsInfo, MetricsLevel } from "../../clients/metricsApis";

describe("cleanMetricsInfo", () => {
	it('should erase all "NaN" value from the Metrics data since it causes incorrect Tooltip value', () => {
		const initData: MetricsInfo = {
			summary: {
				startTimestamp: 1289318400000,
				endTimestamp: 1289318500000,
				level: MetricsLevel.ELITE,
				value: "NaN",
			},
			details: [
				{
					startTimestamp: 1289318400000,
					endTimestamp: 1289318500000,
					level: MetricsLevel.ELITE,
					value: 9,
				},
				{
					startTimestamp: 1289318400000,
					endTimestamp: 1289318500000,
					level: MetricsLevel.ELITE,
					value: "NaN",
				},
			],
		};
		const expectedResult: MetricsInfo = {
			summary: {
				startTimestamp: 1289318400000,
				endTimestamp: 1289318500000,
				level: MetricsLevel.ELITE,
			},
			details: [
				{
					startTimestamp: 1289318400000,
					endTimestamp: 1289318500000,
					level: MetricsLevel.ELITE,
					value: 9,
				},
				{
					startTimestamp: 1289318400000,
					endTimestamp: 1289318500000,
					level: MetricsLevel.ELITE,
				},
			],
		};
		expect(cleanMetricsInfo(initData)).toEqual(expectedResult);
	});
});
