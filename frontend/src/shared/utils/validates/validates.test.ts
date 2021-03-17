import { required } from "./validates";

describe("validates test", () => {
	it("should return error message when given value is undefined", () => {
		expect(required("error happened")(undefined)).toEqual("error happened");
	});

	it("should return null when given value isn't null", () => {
		expect(required("error happened")("value")).toBeNull();
	});
});
