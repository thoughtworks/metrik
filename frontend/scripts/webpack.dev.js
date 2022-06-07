const { merge } = require("webpack-merge");
const commonConfig = require("./webpack.common");
const { OUTPUT_PATH } = require("./constants");

const devConfig = {
	mode: "development",
	devtool: "cheap-module-source-map",
	devServer: {
		allowedHosts: "all",
		static: OUTPUT_PATH,
		host: "localhost",
		port: 2333,
		hot: true,
		client: {
			overlay: true,
		},
		open: true,
		historyApiFallback: true,
		compress: false,
		proxy: {
			"/api": "http://localhost:9000",
		},
	},
};

module.exports = merge(commonConfig, devConfig);
