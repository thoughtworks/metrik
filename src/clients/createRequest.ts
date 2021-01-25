import axios, { AxiosRequestConfig } from "axios";
import { notification } from "antd";

const axiosInstance = axios.create({});

export const createRequest = <TReq, TResp = any>(
	getConfig: (request: TReq) => AxiosRequestConfig
) => {
	const createFn = (request: TReq) => axiosInstance.request(getConfig(request));

	Object.assign(createFn, { TResp: {} as TResp });

	return createFn;
};

axiosInstance.interceptors.response.use(
	response => response,
	error => {
		notification.error({
			message: error.message,
			duration: 3,
			placement: "bottomRight",
		});
		return Promise.reject(error);
	}
);
