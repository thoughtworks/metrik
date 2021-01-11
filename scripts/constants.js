const path=require("path");

const ENTRY_PATH=path.resolve(__dirname,"../src/app.jsx");
const OUTPUT_PATH = path.resolve(__dirname, "../dist");
const PUBLIC_HTML_PATH=path.resolve(__dirname,"../public/index.html");
const isDev=process.env.NODE_EVN==="dev";


 module.exports = {
   ENTRY_PATH,
   OUTPUT_PATH,
   PUBLIC_HTML_PATH,
   isDev
 };