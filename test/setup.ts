import "@testing-library/jest-dom";

// eslint-disable-next-line @typescript-eslint/no-var-requires
const moment = require("moment-timezone");

jest.doMock("moment", () => {
	moment.tz.setDefault("Asia/Singapore");
	return moment;
});
