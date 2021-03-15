type length = number | string;
export function resizeTo(windowObj: any, width: length, height: length): void {
	Object.assign(windowObj, {
		innerWidth: width,
		innerHeight: height,
		outerWidth: width,
		outerHeight: height,
	}).dispatchEvent(new windowObj.Event("resize"));
}

window.resizeTo = function (width: length, height: length) {
	resizeTo(this, width, height);
};
