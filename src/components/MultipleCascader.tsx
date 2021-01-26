import { Checkbox, Row, Col, Radio } from "antd";
import React, { useState } from "react";
import { CaretDownOutlined, CaretRightOutlined } from "@ant-design/icons";
import { RadioChangeEvent } from "antd/es/radio";

const options = [
	{
		label: "AA",
		value: "a",
		children: [
			{
				label: "A1",
				value: "a1",
			},
			{
				label: "A2",
				value: "a2",
			},
			{
				label: "A3",
				value: "a3",
			},
		],
	},
	{
		label: "BB",
		value: "b",
		children: [
			{
				label: "B1",
				value: "b1",
			},
			{
				label: "B2",
				value: "b2",
			},
			{
				label: "B3",
				value: "b3",
			},
		],
	},
	{
		label: "CC",
		value: "c",
		children: [
			{
				label: "C1",
				value: "c1",
			},
			{
				label: "C2",
				value: "c2",
			},
			{
				label: "C3",
				value: "c3",
			},
		],
	},
];

interface Option {
	label: string;
	value: string;
	children?: Option[];
}

export const MultipleCascader = () => {
	const [visibleMap, setVisibleMap] = useState<{ [key: string]: boolean }>({});
	const [cascaderValue, setCascaderValue] = useState<{
		[key: string]: { value: string; checked: boolean; childValue: string };
	}>({});
	const onChange = (checkedValues: any) => {
		// setCheckedValues(checkedValues);

		setCascaderValue(state => {
			return {
				...state,
				...checkedValues.reduce((res: any, val: any) => {
					return {
						...res,
						[val]: {
							value: val,
							childValue:
								state[val]?.childValue || options.find(o => o.value === val)?.children[0].value,
						},
					};
				}, {}),
			};
		});
	};

	const onRadioChange = (e: RadioChangeEvent, option: Option) => {
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
		setCascaderValue(state => {
			return {
				...state,
				[e.target.value]: {
					...state[e.target.value],
					checked: !state[e.target.value]?.checked,
				},
			};
		});
	};

	return (
		<>
			<Checkbox.Group onChange={onChange} value={Object.values(cascaderValue).map(o => o.value)}>
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
									onChange={e => handleCheckBoxChange(e)}
									checked={cascaderValue[option.value]?.checked}>
									{option.label}
								</Checkbox>
								{visibleMap[option.value] && (
									<Row css={{ marginLeft: 25 }}>
										<Radio.Group
											onChange={e => onRadioChange(e, option)}
											value={cascaderValue[option.value]?.childValue}>
											{option.children.map((child, idx) => (
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
		</>
	);
};
