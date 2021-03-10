import { Metrics, MetricsInfo } from "../clients/metricsApis";

export const cleanMetricsInfo = (metricsInfo: MetricsInfo) => {
	const eraseNaNValue = (metrics: Metrics) =>
		(metrics.value = metrics.value === "NaN" ? undefined : metrics.value);

	eraseNaNValue(metricsInfo.summary);
	metricsInfo.details.forEach(item => eraseNaNValue(item));

	return metricsInfo;
};
