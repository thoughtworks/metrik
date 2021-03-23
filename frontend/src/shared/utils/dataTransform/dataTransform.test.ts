import { generateTagLabel } from "./dataTransform";

describe("#dataTransform #generateTagLabel", () => {
	it("should generate correct tag label given pipeline and stage", () => {
		const exceptResult = "backend-service:Deploy to UAT";
		const options = [
			{
				label: "backend-service",
				value: "60594b5c9d70cd32a2798012",
				children: [
					{ label: "Deploy to DEV", value: "Deploy to DEV" },
					{ label: "Deploy to UAT", value: "Deploy to UAT" },
				],
			},
		];
		const tag = { value: "60594b5c9d70cd32a2798012", childValue: "Deploy to UAT" };
		const result = generateTagLabel(options, tag);
		expect(exceptResult).toEqual(result);
	});
	it("should generate correct tag label given stage is not belong to pipeline", () => {
		const exceptResult = "";
		const options = [
			{
				label: "backend-service",
				value: "60594b5c9d70cd32a2798012",
				children: [{ label: "Deploy to DEV", value: "Deploy to DEV" }],
			},
		];
		const tag = { value: "101", childValue: "None exited stage" };
		const result = generateTagLabel(options, tag);
		expect(exceptResult).toEqual(result);
	});
});
