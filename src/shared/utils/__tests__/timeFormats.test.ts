import {
	formatLastUpdateTime,
	momentObjToStartTimeStamp,
	momentObjToEndTimeStamp,
	durationFormatter,
	getRangeTimeStamps,
} from "../timeFormats";
import moment from "moment";

describe("time formats test", () => {
	it("should return correct formatted last update time", () => {
		// refers to 2021-01-26 10:43:56:125 +8
		expect(formatLastUpdateTime(1611629036125)).toEqual("10:43 am, 26 Jan, 2021");
		expect(formatLastUpdateTime(1611650849725)).toEqual("04:47 pm, 26 Jan, 2021");
	});

	it("should return empty string if given data not exists", () => {
		expect(formatLastUpdateTime()).toEqual("");
	});

	it("should return correct formatted tick time when time duration in same year", () => {
		const { startTime, endTime } = durationFormatter(1611629036125, 1629427436125);

		expect(startTime).toEqual("26 Jan");
		expect(endTime).toEqual("20 Aug");
	});

	it("should return correct formatted tick time with year when time duration across year", () => {
		const { startTime, endTime } = durationFormatter(1609430399125, 1609430400000);

		expect(startTime).toEqual("31 Dec 2020");
		expect(endTime).toEqual("01 Jan 2021");
	});
});

describe("#momentObjToStartTimeStamp", () => {
	it("should transform moment object to start time timestamp(milliseconds)", () => {
		expect(momentObjToStartTimeStamp(moment("2010-11-10T12:59:59"))).toEqual(1289318400000);
	});
});

describe("#momentObjToEndTimeStamp", () => {
	it("should transform moment object to end time timestamp(milliseconds)", () => {
		expect(momentObjToEndTimeStamp(moment("2010-11-10T12:59:59"))).toEqual(1289404799000);
	});
});

describe("#getRangeTimeStamps test", () => {
	it("should get transformed range timestamp", () => {
		expect(
			getRangeTimeStamps([moment("2010-11-10T12:59:59"), moment("2010-12-10T12:59:59")])
		).toEqual([1289318400000, 1291996799000]);

		expect(
			getRangeTimeStamps([moment("2010-12-10T12:59:59"), moment("2010-11-10T12:59:59")])
		).toEqual([1289318400000, 1291996799000]);
	});
});
