const HtmlWebpackPlugin = require("html-webpack-plugin");
const { CleanWebpackPlugin } = require("clean-webpack-plugin");
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const WebpackBarPlugin = require("webpackbar");
const { ENTRY_PATH, OUTPUT_PATH, PUBLIC_HTML_PATH, isDev } = require("./constants");

module.exports = {
	entry: ENTRY_PATH,
	output: {
		path: OUTPUT_PATH,
		filename: "js/[name].[contenthash:8].js",
	},
	target: "web",
	resolve: {
		extensions: [".ts", ".tsx", ".js", ".jsx", ".json"],
	},
	module: {
		rules: [
			{
				test: /\.([jt])sx?$/,
				loader: "babel-loader",
				options: {
					cacheDirectory: true,
				},
				exclude: /node_modules/,
			},
			{
				test: /\.css$/,
				use: [
					{
						loader: isDev ? "style-loader" : MiniCssExtractPlugin.loader,
					},
					{
						loader: "css-loader",
						options: {
							importLoaders: 1,
						},
					},
					{
						loader: "postcss-loader",
						options: {
							postcssOptions: {
								plugins: [require("autoprefixer")],
							},
						},
					},
				],
			},
			{
				test: /\.less$/,
				use: [
					{
						loader: isDev ? "style-loader" : MiniCssExtractPlugin.loader,
					},
					{
						loader: "css-loader",
						options: {
							importLoaders: 2,
						},
					},
					{
						loader: "postcss-loader",
						options: {
							postcssOptions: {
								plugins: [require("autoprefixer")],
							},
						},
					},
					{
						loader: "less-loader",
						options: {
							lessOptions: {
								javascriptEnabled: true,
							},
						},
					},
				],
			},
			{
				test: /\.(png|jpg|jpeg|svg)$/i,
				loader: "url-loader",
				options: {
					limit: 10 * 1024,
					outputPath: "assets/images",
					name: "[name].[contenthash:8].[ext]",
				},
			},
			{
				test: /\.(woff|woff2|e0t|ttf|otf)$/i,
				loader: "url-loader",
				options: {
					outputPath: "assets/fonts",
					name: "[name].[contenthash:8].[ext]",
				},
			},
		],
	},
	plugins: [
		new CleanWebpackPlugin(),

		new HtmlWebpackPlugin({
			template: PUBLIC_HTML_PATH,
		}),
		new WebpackBarPlugin({
			name: isDev ? "compiling" : "building",
		}),
	],
};
