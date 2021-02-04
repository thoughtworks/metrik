import moment from "moment";

export const formatLastUpdateTime = (timestamp?: number): string => {
	return timestamp ? moment(timestamp).format("hh:mm, D MMM, YYYY") : "";
};

export const formatTickTime = (timestamp: number): string => {
	return moment(timestamp).format("DD MMM");
};

export const momentObjToStartTimeStamp = (momentObj: moment.Moment): number => {
	return momentObj.startOf("day").unix() * 1000;
};

export const momentObjToEndTimeStamp = (momentObj: moment.Moment): number => {
	return momentObj.endOf("day").unix() * 1000;
};
