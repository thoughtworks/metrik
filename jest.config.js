/*
 * For a detailed explanation regarding each configuration property and type check, visit:
 * https://jestjs.io/docs/en/configuration.html
 */

module.exports = {

  clearMocks: true,

  transform: {
    "^.+\\.[jt]sx?$": "babel-jest",
  },
  transformIgnorePatterns: [],


  collectCoverage: true,
  coverageDirectory: "coverage",
  collectCoverageFrom: [
    "./src/**/*.{ts,tsx}",
    "!App.tsx",
    "!**/*.d.ts"
  ],
  coverageThreshold: {
    global: {
      "branches": 80,
      "functions": 80,
      "lines": 80,
      "statements": 80
    }
  },


  setupFilesAfterEnv: [
    "<rootDir>/test/setup.ts"
  ],
  moduleNameMapper: {
    "\\.(jpg|jpeg|png|gif|eot|otf|webp|svg|ttf|woff|woff2|mp4|webm|wav|mp3|m4a|aac|oga)$": "<rootDir>/test/mocks/fileMock.ts",
    "\\.(css|less)$": "<rootDir>/test/mocks/styleMock.ts"
  }
};
