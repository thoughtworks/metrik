const { merge } = require("webpack-merge");
const commonConfig = require("./webpack.common");
const { OUTPUT_PATH } = require("./constants");

const devConfig = {
	mode: "development",
	devtool: "cheap-module-source-map",
	devServer: {
		contentBase: OUTPUT_PATH,
		host: "localhost",
		port: 2333,
		hot: true,
		overlay: true,
		open: true,
		historyApiFallback: true,
		proxy: {
			"/api":
				process.env.API_ENV === "local" ? "http://localhost:9000" : "http://18.162.205.128:9000",
		},
	},
};

module.exports = merge(commonConfig, devConfig);
