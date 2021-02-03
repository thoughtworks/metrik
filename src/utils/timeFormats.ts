import moment from "moment";

export const formatLastUpdateTime = (timeStamp?: number) => {
	return timeStamp ? moment(timeStamp).format("hh:mm, D MMM, YYYY") : "";
};

export const momentObjToStartTimeStamp = (momentObj: moment.Moment) => {
	return momentObj.startOf("day").unix() * 1000;
};

export const momentObjToEndTimeStamp = (momentObj: moment.Moment) => {
	return momentObj.endOf("day").unix() * 1000;
};
