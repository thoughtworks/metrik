import { Checkbox, Row, Col, Radio, Tag } from "antd";
import React, { useState, useEffect, FC } from "react";
import { CaretDownOutlined, CaretRightOutlined } from "@ant-design/icons";
import { RadioChangeEvent } from "antd/es/radio";
import Trigger from "rc-trigger";

interface Option {
	label: string;
	value: string;
	children?: Option[];
}

interface MultipleCascaderProps {
	onValueChange?: (value: CascaderValue) => void;
	defaultValue?: string[];
	options: Option[];
}

interface CascaderValue {
	[key: string]: { value: string; childValue: string | undefined };
}

export const MultipleCascader: FC<MultipleCascaderProps> = ({
	onValueChange,
	defaultValue = [],
	options = [],
}) => {
	const [checkedValues, setCheckedValues] = useState<string[]>(defaultValue);
	const defaultVisibleMap = defaultValue.reduce((res, v) => {
		return {
			...res,
			[v]: true,
		};
	}, {});

	const [visibleMap, setVisibleMap] = useState<{ [key: string]: boolean }>(defaultVisibleMap);
	const [cascaderValue, setCascaderValue] = useState<CascaderValue>({});
	const onChange = (values: any) => {
		setCheckedValues(values);
	};

	useEffect(() => {
		setCascaderValue(state => {
			return {
				...checkedValues?.reduce((res: any, val: any) => {
					return {
						...res,
						[val]: {
							value: val,
							childValue:
								state[val]?.childValue ||
								(options.find(o => o.value === val)?.children ?? [])[0].value,
						},
					};
				}, {}),
			};
		});
	}, [checkedValues]);

	useEffect(() => {
		onValueChange && onValueChange(cascaderValue);
	}, [cascaderValue]);

	const onRadioChange = (e: RadioChangeEvent, option: any) => {
		if (!checkedValues.includes(option.value)) {
			setCheckedValues(state => {
				return [...state, option?.value];
			});
		}
		setCascaderValue((state: any) => {
			return {
				...state,
				[option.value]: {
					value: option.value,
					childValue: e.target.value,
				},
			};
		});
	};

	const toggle = (id: string) => {
		setVisibleMap(state => {
			return {
				...state,
				[id]: !state[id],
			};
		});
	};

	const handleCheckBoxChange = (e: any) => {
		setCheckedValues((state: any) => {
			if (e.target.checked) {
				return [...state, e.target.value];
			}
			return state.filter((v: any) => v !== e.target.value);
		});
	};

	const [popupVisible, setPopupVisible] = useState(false);

	return (
		<Trigger
			action={["click"]}
			destroyPopupOnHide
			popup={
				<Checkbox.Group onChange={onChange} value={checkedValues}>
					{options.map((option, key) => {
						return (
							<Row key={key}>
								<Col>
									<span
										onClick={() => toggle(option.value)}
										css={{ display: "inline-block", cursor: "pointer" }}>
										{visibleMap[option.value] ? <CaretDownOutlined /> : <CaretRightOutlined />}
									</span>
									<Checkbox
										value={option.value}
										onChange={handleCheckBoxChange}
										disabled={checkedValues.length === 1 && checkedValues.includes(option.value)}>
										{option.label}
									</Checkbox>
									{visibleMap[option.value] && (
										<Row css={{ marginLeft: 25 }}>
											<Radio.Group
												onChange={e => onRadioChange(e, option)}
												value={cascaderValue[option.value]?.childValue}>
												{(option.children ?? []).map((child, idx) => (
													<Col key={idx}>
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
			}
			popupAlign={{
				points: ["tl", "bl"],
				offset: [0, 15],
			}}
			popupVisible={popupVisible}
			onPopupVisibleChange={setPopupVisible}>
			<div>
				<input readOnly />
				{Object.values(cascaderValue).map((item, key) => {
					return <Tag key={key} closable={true}>{`${item.value} ${item.childValue}`}</Tag>;
				})}
			</div>
		</Trigger>
	);
};
