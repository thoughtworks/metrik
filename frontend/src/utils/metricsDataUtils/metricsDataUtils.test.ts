import { cleanMetricsInfo } from "./metricsDataUtils";
import { MetricsInfo } from "../../clients/metricsApis";
import { MetricsLevel } from "../../__types__/enum";

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
					value: 9,
				},
				{
					startTimestamp: 1289318400000,
					endTimestamp: 1289318500000,
					value: "NaN",
				},
			],
		};
		const expectedResult: MetricsInfo = {
			summary: {
				value: undefined,
				startTimestamp: 1289318400000,
				endTimestamp: 1289318500000,
				level: MetricsLevel.ELITE,
			},
			details: [
				{
					startTimestamp: 1289318400000,
					endTimestamp: 1289318500000,
					value: 9,
				},
				{
					startTimestamp: 1289318400000,
					endTimestamp: 1289318500000,
					value: undefined,
				},
			],
		};
		expect(cleanMetricsInfo(initData)).toEqual(expectedResult);
	});
});
