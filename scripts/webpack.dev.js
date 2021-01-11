const {merge} = require("webpack-merge");
const commonConfig = require("./webpack.common");


const devConfig = {
  mode:"development",
  devtool:"cheap-module-source-map"
};

module.exports = merge(commonConfig,devConfig);