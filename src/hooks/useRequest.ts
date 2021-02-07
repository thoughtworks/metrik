import { useState, useMemo } from "react";

interface RequestCreator<TReq, TResp> {
	(params: TReq): Promise<TResp>;
	TReq: TReq;
	TResp: TResp;
}

export const useRequest = <T extends RequestCreator<T["TReq"], T["TResp"]>>(
	requestCreator: T,
	defaultData?: T["TResp"]
) => {
	const [data, setData] = useState<T["TResp"]>(defaultData);
	const [loading, setLoading] = useState(false);

	const getData = useMemo(() => {
		return (params: T["TReq"]) => {
			setLoading(true);
			const promise = requestCreator(params).then((resp: T["TResp"]) => {
				setData(resp);
				return resp;
			});

			promise.finally(() => {
				setLoading(false);
			});

			return promise;
		};
	}, []);

	return [data, getData, loading, setData] as const;
};
