import {
	formatLastUpdateTime,
	momentObjToStartTimeStamp,
	momentObjToEndTimeStamp,
	formatTickTime,
} from "../timeFormats";
import moment from "moment";

describe("time formats test", () => {
	it("should return correct formatted last update time", () => {
		// refers to 2021-01-26 10:43:56:125 +8
		expect(formatLastUpdateTime(1611629036125)).toEqual("10:43, 26 Jan, 2021");
	});
	it("should return empty string if given data not exists", () => {
		expect(formatLastUpdateTime()).toEqual("");
	});
	it("should return correct formatted tick time", () => {
		expect(formatTickTime(1611629036125)).toEqual("26 Jan");
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
