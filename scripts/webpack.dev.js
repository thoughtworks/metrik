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
	},
};

module.exports = merge(commonConfig, devConfig);
