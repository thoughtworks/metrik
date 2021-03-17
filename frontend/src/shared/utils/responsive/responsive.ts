const setBaseFontSize = () => {
	const baseScreenWidth = 1920;
	const viewPortWidth = document.documentElement.clientWidth || window.innerWidth;
	const ratio = (viewPortWidth / baseScreenWidth) * 100;
	document.documentElement.style.fontSize = `${ratio}px`;
};

const setResponsive = () => {
	window.onresize = setBaseFontSize;
	setBaseFontSize();
};
export { setResponsive };
