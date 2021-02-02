import { formatLastUpdateTime } from "../timeFormats";

describe("time formats test", () => {
	it("should return correct formatted last update time", () => {
		// refers to 2021-01-26 10:43:56:125 +8
		expect(formatLastUpdateTime(1611629036125)).toEqual("10:43, 26 Jan, 2021");
	});
	it("should return empty string if given data not exists", () => {
		expect(formatLastUpdateTime()).toEqual("");
	});
});
