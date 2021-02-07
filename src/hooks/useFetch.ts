import { useState } from "react";

export const useFetch = <TRes>(fetcher: () => Promise<TRes>) => {
	const [response, setResponse] = useState<TRes>();
	const [error, setError] = useState<Error>();
	const [isLoading, setIsLoading] = useState<boolean>(false);

	const doFetch = async () => {
		setIsLoading(true);
		setError(undefined);
		setResponse(undefined);

		try {
			const res = await fetcher();
			setResponse(res);
			setIsLoading(false);
		} catch (error) {
			setError(error);
			setIsLoading(false);
		}
	};

	return { response, error, isLoading, doFetch };
};
