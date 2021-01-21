import axios, { AxiosRequestConfig } from "axios";

const axiosInstance = axios.create({
	baseURL: "http://127.0.0.1:9000",
});

export const createRequest = <TReq, TResp = any>(
	getConfig: (request: TReq) => AxiosRequestConfig
) => {
	const createFn = (request: TReq) => axiosInstance.request(getConfig(request));

	Object.assign(createFn, { TResp: {} as TResp });

	return createFn;
};
