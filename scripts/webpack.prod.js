const {merge} = require("webpack-merge");
const commonConfig = require("./webpack.common");

const prodConfig = {
  mode:"production",
  devtool:false
};


module.exports = merge(commonConfig,prodConfig);