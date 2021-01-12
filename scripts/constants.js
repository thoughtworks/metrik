const path=require("path");

const SOURCE_CODE_PATH = path.resolve(__dirname, "../src");
const ENTRY_PATH=path.resolve(SOURCE_CODE_PATH,"app.tsx");
const OUTPUT_PATH = path.resolve(__dirname, "../dist");
const PUBLIC_HTML_PATH=path.resolve(__dirname,"../public/index.html");

const isDev=process.env.NODE_EVN==="dev";

 module.exports = {
   SOURCE_CODE_PATH,
   ENTRY_PATH,
   OUTPUT_PATH,
   PUBLIC_HTML_PATH,
   isDev
 };