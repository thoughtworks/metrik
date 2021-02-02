import axios, { AxiosRequestConfig } from "axios";
import { notification } from "antd";

const axiosInstance = axios.create({});

export const createRequest = <TReq, TResp = any>(
	_: string,
	getConfig: (request: TReq) => AxiosRequestConfig
) => {
	const createFn = (request: TReq) => axiosInstance.request<TResp, TResp>(getConfig(request));

	Object.assign(createFn, { TResp: {} as TResp });

	return createFn;
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
