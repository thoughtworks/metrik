const path = require("path");

const ENTRY_PATH = path.resolve(__dirname, "../src/App.tsx");
const OUTPUT_PATH = path.resolve(__dirname, "../dist");

const PUBLIC_HTML_PATH = path.resolve(__dirname, "../public/index.html");
const FAVICON_PATH = path.resolve(__dirname, "../src/shared/assets/source/favicon.svg");

const isDev = process.env.NODE_ENV === "dev";
const isTest = process.env.NODE_ENV === "test";
const enableAnalyzer = !!process.env.ENABLE_ANALYZER;

module.exports = {
	ENTRY_PATH,
	OUTPUT_PATH,
	PUBLIC_HTML_PATH,
	FAVICON_PATH,
	isDev,
	isTest,
	enableAnalyzer,
};
