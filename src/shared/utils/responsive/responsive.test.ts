import { setResponsive } from "./responsive";
describe("setResponsive", () => {
	beforeAll(() => {
		document.documentElement.style.fontSize = "100px";
	});
	test("should add event listener for window resize", () => {
		setResponsive();
		window.resizeTo(960, 1080);
		expect(document.documentElement.style.fontSize).toEqual("50px");
	});
});
