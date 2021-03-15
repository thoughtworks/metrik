const setBaseFontSize = () => {
	const baseScreenWidth = 1920;
	const viewPortWidth = document.documentElement.clientWidth;
	const ratio = (viewPortWidth / baseScreenWidth) * 100;
	document.documentElement.style.fontSize = `${ratio}px`;
};

const setResponsive = () => {
	setBaseFontSize();
	window.onresize = setBaseFontSize;
};
export { setResponsive };
