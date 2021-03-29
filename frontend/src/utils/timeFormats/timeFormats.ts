import moment, { Moment } from "moment";
import { max, min } from "lodash";

export const formatLastUpdateTime = (timestamp?: number): string =>
	timestamp ? moment(timestamp).format("hh:mm a, D MMM, YYYY") : "";

export const durationFormatter = (
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

export const getRangeTimeStamps = (duration: [Moment, Moment]) => {
	const [start, end] = duration[0].isBefore(duration[1]) ? duration : [...duration].reverse();
	return [momentObjToStartTimeStamp(start), momentObjToEndTimeStamp(end)];
};
export const getDurationTimestamps = (duration: [Moment, Moment]) => {
	const durationTimestamps = getRangeTimeStamps(duration);
	return {
		startTimestamp: min(durationTimestamps),
		endTimestamp: max(durationTimestamps),
	};
};
