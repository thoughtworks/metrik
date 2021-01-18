const { merge } = require("webpack-merge");
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const CssMinimizerPlugin = require("css-minimizer-webpack-plugin");
const TerserPlugin = require("terser-webpack-plugin");
const BundleAnalyzerPlugin = require("webpack-bundle-analyzer").BundleAnalyzerPlugin;
const commonConfig = require("./webpack.common");
const { enableAnalyzer } = require("./constants");

const prodConfig = {
	mode: "production",
	devtool: false,
	plugins: [
		new MiniCssExtractPlugin({
			filename: "css/[name].[contenthash:8].css",
		}),
		enableAnalyzer && new BundleAnalyzerPlugin(),
	].filter(Boolean),
	optimization: {
		splitChunks: {
			chunks: "all",
			cacheGroups: {
				vendors: {
					name: "vendors",
					test: /[\\/]node_modules[\\/]/,
					priority: -10,
					reuseExistingChunk: true,
				},
				default: {
					minChunks: 2,
					priority: -20,
					reuseExistingChunk: true,
				},
			},
		},

		minimize: true,
		minimizer: [
			new TerserPlugin({
				extractComments: false,
			}),
			new CssMinimizerPlugin(),
		],
	},
};

module.exports = merge(commonConfig, prodConfig);
