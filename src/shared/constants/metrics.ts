export const metricsExplanations = {
	df: "The number of successful deployments to target environment under the given date range.",
	lt:
		"The average amount of time it takes to go from code commit to code running in target environment under the given date range.",
	mttr:
		"The average amount of time it takes to restore from deployment failures in target environment under the given date range.",
	cfr: "The percentage of deployment failures in target environment under the given date range.",
};

export const metricsStanderMapping = {
	Fortnightly: {
		df: {
			elite: ">14",
			high: "<=14 & >2",
			medium: "<=2 & >0.5",
			low: "<=0.5",
		},
		lt: {
			elite: "<1 day",
			high: "<7 days & >=1 day",
			medium: "<30 days & >=7 days",
			low: ">=30 days",
		},
		mttr: {
			elite: "<1 hour",
			high: "<24 hours & >=1 hour",
			medium: "<168 hours & >=24 hours",
			low: ">=168 hours",
		},
		cfr: {
			elite: "<15%",
			high: ">=15% & <30%",
			medium: ">=30% & <45%",
			low: ">=45%",
		},
	},
	Monthly: {
		df: {
			elite: ">30",
			high: "<=30 & >4",
			medium: "<=4 & >1",
			low: "<=1",
		},
		lt: {
			elite: "<1 day",
			high: "<7 days & >=1 day",
			medium: "<30 days & >=7 days",
			low: ">=30 days",
		},
		mttr: {
			elite: "<1 hour",
			high: "<24 hours & >=1 hour",
			medium: "<168 hours & >=24 hours",
			low: ">=168 hours",
		},
		cfr: {
			elite: "<15%",
			high: ">=15% & <30%",
			medium: ">=30% & <45%",
			low: ">=45%",
		},
	},
};
