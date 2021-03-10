import { isNumber, isEmpty, map } from "lodash";
import { Metrics, ValidMetric } from "../clients/metricsApis";

const domainMaximizeRatio = 1.1;

export const calcMaxValueWithRatio = (
	metrics: Metrics[],
	defaultMaxValue = 1,
	defaultRatio = domainMaximizeRatio
) => {
	const validList = metrics.filter(item => isNumber(item.value)) as ValidMetric[];
	if (isEmpty(validList)) {
		return defaultMaxValue;
	}

	const dataMax = Math.max(...map(validList, item => item.value));
	return dataMax === 0 ? defaultMaxValue : Math.ceil((dataMax * defaultRatio) / 10) * 10;
};
