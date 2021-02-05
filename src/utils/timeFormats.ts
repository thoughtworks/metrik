import moment from "moment";

export const formatLastUpdateTime = (timestamp?: number): string => {
	return timestamp ? moment(timestamp).format("hh:mm, D MMM, YYYY") : "";
};

export const formatTickTime = (
	startTimestamp: number,
	endTimestamp: number
): { startTime: string; endTime: string } => {
	const crossYear = moment(endTimestamp).year() - moment(startTimestamp).year() > 0;
	const yearPattern = crossYear ? " YYYY" : "";

	const startTime = moment(startTimestamp).format(`DD MMM${yearPattern}`);
	const endTime = moment(endTimestamp).format(`DD MMM${yearPattern}`);

	return { startTime, endTime };
};

export const momentObjToStartTimeStamp = (momentObj: moment.Moment): number => {
	return momentObj.startOf("day").unix() * 1000;
};

export const momentObjToEndTimeStamp = (momentObj: moment.Moment): number => {
	return momentObj.endOf("day").unix() * 1000;
};
