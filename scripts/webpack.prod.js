const {merge} = require("webpack-merge");
const glob = require("glob");
const MiniCssExtractPlugin=require("mini-css-extract-plugin");
const PurgecssWebpackPlugin = require("purgecss-webpack-plugin");
const commonConfig = require("./webpack.common");
const CssMinimizerPlugin = require('css-minimizer-webpack-plugin');

const {SOURCE_CODE_PATH} = require("./constants");
const prodConfig = {
  mode:"production",
  devtool:false,
  plugins:[
    new MiniCssExtractPlugin({
      filename:"css/[name].[contenthash:8].css",
      chunkFilename:"css/[name].[contenthash:8].css"
    }),
    new PurgecssWebpackPlugin({
      paths:glob.sync(`${SOURCE_CODE_PATH}/**/*.{tsx,less,css}`,{nodir:true})
    })
  ],
  optimization: {
    minimize: true,
    minimizer: [
      new CssMinimizerPlugin(),
    ],
  },
};


module.exports = merge(commonConfig,prodConfig);