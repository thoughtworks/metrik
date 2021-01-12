const { isTest } = require("./scripts/constants");

module.exports = {
  presets: [
    [
      "@babel/preset-env",
      {
        modules: isTest ? "auto" : false,
      },
    ],
    "@babel/preset-react",
    "@babel/preset-typescript",
  ],
  plugins: [
    isTest
      ? undefined
      : [
          "@babel/plugin-transform-runtime",
          {
            corejs: 3,
            helpers: true,
            useESModules: true,
          },
        ],
    [
      "import",
      {
        libraryName: "antd",
        libraryDirectory: "es",
        style: true,
      },
    ],
  ].filter(Boolean),
};
