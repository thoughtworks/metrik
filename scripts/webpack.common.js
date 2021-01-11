const HtmlWebpackPlugin = require("html-webpack-plugin");
const {CleanWebpackPlugin} = require("clean-webpack-plugin");
const {ENTRY_PATH,OUTPUT_PATH} = require("./constants");


module.exports = {
  entry: ENTRY_PATH,
  output: {
    path:OUTPUT_PATH,
    filename: "js/[name].[hash:8].js"
  },
  target: "web",
  module: {
    rules: [
      {
        test:/\.css$/,
        use:[
          {
            loader: "style-loader"
          },
          {
            loader:"css-loader",
            options:{
              importLoaders:1
            }
          },
          {
            loader:"postcss-loader",
            options:{
              postcssOptions:{
                plugins:[require("autoprefixer")]
              }

            }
          }
        ]

      },
      {
        test:/\.less$/,
        use:[
          {
            loader: "style-loader"
          },
          {
            loader:"css-loader",
            options:{
              importLoaders:2
            }
          },
          {
            loader:"postcss-loader",
            options:{
              postcssOptions:{
                plugins:[require("autoprefixer")]
              }

            }
          },
          {
            loader:"less-loader"
          }
        ]

      }
    ]
  },
  plugins: [
    new HtmlWebpackPlugin(),
    new CleanWebpackPlugin()
  ]
};