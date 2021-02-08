import axios, { AxiosRequestConfig } from "axios";
import { notification } from "antd";

const axiosInstance = axios.create({
	timeout: 300000, // 5 minutes timeout
});

export const createRequest = <TReq, TResp = any>(
	_: string,
	getConfig: (request: TReq) => AxiosRequestConfig
) => {
	const createFn = (request: TReq) => axiosInstance.request<TResp, TResp>(getConfig(request));
	return Object.assign(createFn, { TResp: {} as TResp, TReq: {} as TReq });
};

axiosInstance.interceptors.response.use(
	response => response.data,
	error => {
		notification.error({
			message: error.message,
			duration: 3,
			placement: "topRight",
		});
		return Promise.reject(error);
	}
);
