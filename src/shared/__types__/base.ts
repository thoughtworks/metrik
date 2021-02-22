export enum VerifyStatus {
	DEFAULT,
	SUCCESS,
	Fail,
}

export enum ConfigStep {
	CREATE_DASHBOARD,
	CONFIG_PIPELINE,
	CONFIG_SUCCESS,
}

export type DurationUnit = "Fortnightly" | "Monthly";
export type MetricType = "df" | "lt" | "mttr" | "cfr";
