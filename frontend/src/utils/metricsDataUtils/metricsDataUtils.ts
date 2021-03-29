import { Metrics, MetricsInfo } from "../../models/metrics";

export const cleanMetricsInfo = (metricsInfo: MetricsInfo) => {
	const eraseNaNValue = (metrics: Metrics) =>
		(metrics.value = metrics.value === "NaN" ? undefined : metrics.value);
	return {
		summary: { ...metricsInfo.summary, ...{ value: eraseNaNValue(metricsInfo.summary) } },
		details: metricsInfo.details.map(item => {
			return { ...item, ...{ value: eraseNaNValue(item) } };
		}),
	};
};
