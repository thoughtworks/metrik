import { compact } from "lodash";
import { CascadeValueItem, Option } from "../../components/MultipleCascadeSelect";

export const findOptionByValue = (options: Option[], value?: string): Option | undefined =>
	options.find(o => o.value === value);

export const generateTagLabel = (options: Option[], tag: CascadeValueItem) => {
	const option = findOptionByValue(options, tag.value) || ({} as Option);
	return compact([
		option?.label,
		findOptionByValue(option?.children ?? [], tag.childValue)?.label,
	]).join(":");
};
