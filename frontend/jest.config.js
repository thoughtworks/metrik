module.exports = {
	clearMocks: true,

	transform: {
		"^.+\\.[jt]sx?$": "babel-jest",
	},
	transformIgnorePatterns: [],

	collectCoverage: true,
	coverageDirectory: "coverage",
	collectCoverageFrom: [
		"./src/**/*.ts",
		"!./src/models/*",
		"!./src/clients/*",
		"!**/*.d.ts",
		"!./src/constants/*",
		"!./src/hooks/*",
		"!./src/globalStyle.ts",
	],
	coverageReporters: ["html", "text", "cobertura"],
	coverageThreshold: {
		global: {
			branches: 80,
			functions: 80,
			lines: 80,
			statements: 80,
		},
	},

	setupFilesAfterEnv: ["<rootDir>/test/setup.ts", "<rootDir>/test/jsdomHelper.ts"],
	moduleNameMapper: {
		"\\.(jpg|jpeg|png|gif|eot|otf|webp|svg|ttf|woff|woff2)$": "<rootDir>/test/mocks/fileMock.ts",
		"\\.(css|less)$": "<rootDir>/test/mocks/styleMock.ts",
	},
};
