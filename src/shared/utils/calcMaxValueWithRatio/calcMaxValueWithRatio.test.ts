import { calcMaxValueWithRatio } from "./calcMaxValueWithRatio";
import { MetricsLevel } from "../../__types__/enum";

describe("#calcMaxValueWithRatio", () => {
	it("should return default max value if metrics is empty", () => {
		expect(calcMaxValueWithRatio([], 1, 1)).toEqual(1);
	});

	it("should return default max value if there is no valid metrics value", () => {
		expect(
			calcMaxValueWithRatio(
				[
					{
						value: "NaN",
						startTimestamp: 1610726400000,
						endTimestamp: 1611935999999,
						level: MetricsLevel.ELITE,
					},
					{
						value: "NaN",
						startTimestamp: 1610726400000,
						endTimestamp: 1611935999999,
						level: MetricsLevel.ELITE,
					},
				],
				100,
				1
			)
		).toEqual(100);
	});

	it("should return default max value 1 if theres is no valid metrics value without a given default value", () => {
		expect(
			calcMaxValueWithRatio([
				{
					value: "NaN",
					startTimestamp: 1610726400000,
					endTimestamp: 1611935999999,
					level: MetricsLevel.ELITE,
				},
				{
					value: "NaN",
					startTimestamp: 1610726400000,
					endTimestamp: 1611935999999,
					level: MetricsLevel.ELITE,
				},
			])
		).toEqual(1);
	});

	it("should return default max value if metrics max value is 0", () => {
		expect(
			calcMaxValueWithRatio(
				[
					{
						value: 0,
						startTimestamp: 1603727999000,
						endTimestamp: 1604678399999,
						level: MetricsLevel.ELITE,
					},
					{
						value: "NaN",
						startTimestamp: 1610726400000,
						endTimestamp: 1611935999999,
						level: MetricsLevel.ELITE,
					},
				],
				1,
				1.1
			)
		).toEqual(1);
	});

	it("should return max value with specific ratio", () => {
		expect(
			calcMaxValueWithRatio(
				[
					{
						value: "NaN",
						startTimestamp: 1603727999000,
						endTimestamp: 1604678399999,
						level: MetricsLevel.ELITE,
					},
					{
						value: 100.0,
						startTimestamp: 1609516800000,
						endTimestamp: 1610726399999,
						level: MetricsLevel.ELITE,
					},
					{
						value: "NaN",
						startTimestamp: 1610726400000,
						endTimestamp: 1611935999999,
						level: MetricsLevel.ELITE,
					},
				],
				100,
				1
			)
		).toEqual(100);

		expect(
			calcMaxValueWithRatio(
				[
					{
						value: 0,
						startTimestamp: 1603727999000,
						endTimestamp: 1604678399999,
						level: MetricsLevel.ELITE,
					},
					{
						value: "NaN",
						startTimestamp: 1610726400000,
						endTimestamp: 1611935999999,
						level: MetricsLevel.ELITE,
					},
				],
				1,
				1.1
			)
		).toEqual(1);

		expect(
			calcMaxValueWithRatio(
				[
					{
						value: 899,
						startTimestamp: 1603727999000,
						endTimestamp: 1604678399999,
						level: MetricsLevel.ELITE,
					},
					{
						value: 100,
						startTimestamp: 1610726400000,
						endTimestamp: 1611935999999,
						level: MetricsLevel.ELITE,
					},
				],
				1,
				2
			)
		).toEqual(1800);
	});

	it("should return max value with default ratio", () => {
		expect(
			calcMaxValueWithRatio([
				{
					value: 899,
					startTimestamp: 1603727999000,
					endTimestamp: 1604678399999,
					level: MetricsLevel.ELITE,
				},
				{
					value: 100,
					startTimestamp: 1610726400000,
					endTimestamp: 1611935999999,
					level: MetricsLevel.ELITE,
				},
			])
		).toEqual(990);
	});
});
