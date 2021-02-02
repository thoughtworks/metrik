import moment from "moment";

export const formatLastUpdateTime = (timeStamp?: number) => {
	return timeStamp ? moment(timeStamp).format("hh:mm, D MMM, YYYY") : "";
};
