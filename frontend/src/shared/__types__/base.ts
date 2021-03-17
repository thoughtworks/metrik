export enum VerifyStatus {
	DEFAULT,
	SUCCESS,
	Fail,
}

export enum ConfigStep {
	CREATE_PROJECT,
	CONFIG_PIPELINE,
	CONFIG_SUCCESS,
}

export type MetricType = "df" | "lt" | "mttr" | "cfr";
