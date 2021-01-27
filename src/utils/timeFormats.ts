import moment from "moment";

export const formatLastUpdateTime = (timeStamp: number) => {
	return moment(timeStamp).format("hh:mm, D MMM, YYYY");
};
