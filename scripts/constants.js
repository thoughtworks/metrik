const path=require("path");

const ENTRY_PATH=path.resolve(__dirname,"../src/app.js");
const OUTPUT_PATH = path.resolve(__dirname, "../dist");

const isDev=process.env.NODE_EVN==="dev";


 module.exports = {
   ENTRY_PATH,
   OUTPUT_PATH,
   isDev
 };