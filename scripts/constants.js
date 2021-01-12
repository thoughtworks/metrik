const path = require("path");

const SOURCE_CODE_PATH = path.resolve(__dirname, "../src");
const ENTRY_PATH = path.resolve(SOURCE_CODE_PATH, "App.tsx");
const OUTPUT_PATH = path.resolve(__dirname, "../dist");
const PUBLIC_HTML_PATH = path.resolve(__dirname, "../public/index.html");

const isDev = process.env.NODE_ENV === "dev";
const isTest = process.env.NODE_ENV === "test";
const enableAnalyzer = !!process.env.ENABLE_ANALYZER;

module.exports = {
	SOURCE_CODE_PATH,
	ENTRY_PATH,
	OUTPUT_PATH,
	PUBLIC_HTML_PATH,
	isDev,
	isTest,
	enableAnalyzer,
};
