import { Checkbox, Row, Col, Radio, Tag, Typography } from "antd";
import React, { useState, useEffect, FC } from "react";
import { CaretDownOutlined, CaretRightOutlined } from "@ant-design/icons";
import { RadioChangeEvent } from "antd/es/radio";
import Trigger from "rc-trigger";
import { css } from "@emotion/react";
import { CheckboxChangeEvent } from "antd/es/checkbox";
import Overflow from "rc-overflow";

const { Text } = Typography;

interface Option {
	label: string;
	value: string;
	children?: Option[];
}

interface MultipleCascadeSelectProps {
	onChange?: (value: CascadeValueItem[]) => void;
	defaultValues?: CascadeValueItem[];
	options: Option[];
}

interface CascadeValue {
	[key: string]: CascadeValueItem;
}

interface CascadeValueItem {
	value: string;
	childValue: string | undefined;
}

const popupContainerStyles = css({ padding: "13px 20px", maxHeight: 500, overflow: "scroll" });

const findOptionByValue = (options: Option[], value?: string): Option | undefined =>
	options.find(o => o.value === value);

export const MultipleCascadeSelect: FC<MultipleCascadeSelectProps> = ({
	onChange,
	defaultValues = [],
	options = [],
}) => {
	const [popupVisible, setPopupVisible] = useState(false);
	const [checkedValues, setCheckedValues] = useState<string[]>(defaultValues.map(o => o.value));
	const defaultVisibleMap = defaultValues.reduce(
		(res, v) => ({
			...res,
			[v.value]: true,
		}),
		{}
	);

	const [visibleMap, setVisibleMap] = useState<{ [key: string]: boolean }>(defaultVisibleMap);
	const [cascadeValue, setCascadeValue] = useState<CascadeValue>({});

	useEffect(() => {
		setVisibleMap(state => ({
			...state,
			...checkedValues.reduce(
				(res, item) => ({
					...res,
					[item]: true,
				}),
				{}
			),
		}));

		setCascadeValue(state => ({
			...checkedValues.reduce((res: CascadeValue, val: string) => {
				const childOptions = options.find(o => o.value === val)?.children ?? [];
				return {
					...res,
					[val]: {
						value: val,
						childValue: state[val]?.childValue || childOptions[childOptions.length - 1].value,
					},
				};
			}, {}),
		}));
	}, [checkedValues]);

	const onRadioChange = (e: RadioChangeEvent, option: Option) => {
		if (!checkedValues.includes(option.value)) {
			setCheckedValues(state => [...state, option.value]);
		}

		setCascadeValue((state: CascadeValue) => ({
			...state,
			[option.value]: {
				value: option.value,
				childValue: e.target.value,
			},
		}));
	};

	const toggle = (id: string) =>
		setVisibleMap(state => ({
			...state,
			[id]: !state[id],
		}));

	const handleCheckBoxChange = (e: CheckboxChangeEvent) => {
		setCheckedValues(state =>
			e.target.checked ? [...state, e.target.value] : state.filter(v => v !== e.target.value)
		);
	};

	useEffect(() => {
		onChange && onChange(Object.values(cascadeValue));
	}, [cascadeValue]);

	const tags = Object.values(cascadeValue);

	return (
		<Trigger
			action={["click"]}
			destroyPopupOnHide
			popupClassName={"ant-select-dropdown ant-select-dropdown-placement-bottomLeft"}
			popup={
				<div css={popupContainerStyles}>
					<Text type={"secondary"}>Pipelines</Text>
					<div css={{ marginTop: 20, padding: "0 15px" }}>
						<Checkbox.Group
							onChange={(values: any[]) => setCheckedValues(values)}
							value={checkedValues}>
							{options.map((option, key) => {
								return (
									<Row key={key} css={{ marginBottom: 8 }}>
										<Col>
											<span
												onClick={() => toggle(option.value)}
												css={{ display: "inline-block", cursor: "pointer" }}>
												{visibleMap[option.value] ? <CaretDownOutlined /> : <CaretRightOutlined />}
											</span>
											<Checkbox
												value={option.value}
												onChange={handleCheckBoxChange}
												disabled={
													checkedValues.length === 1 && checkedValues.includes(option.value)
												}>
												{option.label}
											</Checkbox>
											{visibleMap[option.value] && (
												<Row css={{ marginLeft: 50, marginTop: 6 }}>
													<Radio.Group
														onChange={e => onRadioChange(e, option)}
														value={cascadeValue[option.value]?.childValue}>
														{(option.children ?? []).map((child, idx) => (
															<Col key={idx} css={{ marginTop: 4 }}>
																<Radio value={child.value}>{child.label}</Radio>
															</Col>
														))}
													</Radio.Group>
												</Row>
											)}
										</Col>
									</Row>
								);
							})}
						</Checkbox.Group>
					</div>
				</div>
			}
			popupAlign={{
				points: ["tl", "bl"],
				offset: [0, 5],
			}}
			popupVisible={popupVisible}
			onPopupVisibleChange={setPopupVisible}>
			<div className={"ant-select ant-select-multiple"}>
				<div className={"ant-select-selector"}>
					<Overflow
						prefixCls={"ant-select-selection-overflow"}
						data={tags}
						maxCount={"responsive"}
						renderRest={(items: any[]) => {
							return <div className={"ant-select-selection-item"}>+{items.length}...</div>;
						}}
						renderItem={tag => {
							const option = findOptionByValue(options, tag.value) || ({} as Option);
							const tagLabel = [
								option?.label,
								findOptionByValue(option?.children ?? [], tag.childValue)?.label,
							].join(",");

							return (
								<div className={"ant-select-selection-overflow-item"}>
									<Tag
										className={"ant-select-selection-item"}
										css={{ alignItems: "center" }}
										closable={tags.length > 1}
										onClose={() => {
											setCheckedValues(state => state.filter(v => v !== tag.value));
										}}>
										{tagLabel}
									</Tag>
								</div>
							);
						}}
					/>
				</div>
			</div>
		</Trigger>
	);
};
