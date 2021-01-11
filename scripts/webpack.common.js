const HtmlWebpackPlugin = require("html-webpack-plugin");
const {CleanWebpackPlugin} = require("clean-webpack-plugin");
const {ENTRY_PATH,OUTPUT_PATH,PUBLIC_HTML_PATH} = require("./constants");


module.exports = {
  entry: ENTRY_PATH,
  output: {
    path:OUTPUT_PATH,
    filename: "js/[name].[hash:8].js"
  },
  target: "web",
  resolve: {
    extensions: [".ts",".tsx",".js",".jsx",".json"]
  },
  module: {
    rules: [
      {
        test:/\.([jt])sx?$/,
        loader: "babel-loader",
        options: {
          cacheDirectory:true,
          presets:[["@babel/preset-env",{
            "modules":false
          }],"@babel/preset-react","@babel/preset-typescript"],
          plugins:[
            ["@babel/plugin-transform-runtime",{
              "corejs":3,
              "helpers":true,
              "useESModules":true
            }]
          ]
        },
        exclude: /node_modules/
      },
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
    new HtmlWebpackPlugin({
      template: PUBLIC_HTML_PATH
    }),
    new CleanWebpackPlugin()
  ]
};