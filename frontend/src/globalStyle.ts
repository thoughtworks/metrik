import { BG_GRAY_DARK } from "./constants/styles";

export const globalStyles = {
	".metric-info-overlay div.ant-tooltip-inner": {
		width: "300px !important",
	},
	".fullscreen-dashboard-modal .ant-modal": {
		maxWidth: "100vw",
		paddingBottom: "0",
	},
	".fullscreen-dashboard-modal .ant-modal-body": {
		padding: 0,
	},
	".fullscreen-dashboard-modal.ant-modal-wrap": {
		overflow: "hidden",
	},
	".ant-progress-text": {
		width: "auto",
	},
	"::-webkit-scrollbar": {
		backgroundColor: "transparent",
		width: "thin",
	},
	"::-webkit-scrollbar-thumb": {
		backgroundColor: BG_GRAY_DARK,
		borderRadius: 10,
		border: "3px solid transparent",
		backgroundClip: "content-box",
	},
	"::-webkit-scrollbar-corner": {
		backgroundColor: "transparent",
	},
};
