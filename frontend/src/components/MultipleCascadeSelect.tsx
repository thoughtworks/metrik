import { Checkbox, Row, Col, Radio, Tag, Typography } from "antd";
import React, { useState, useEffect, FC } from "react";
import { CaretDownOutlined, CaretRightOutlined } from "@ant-design/icons";
import { RadioChangeEvent } from "antd/es/radio";
import Trigger from "rc-trigger";
import { css } from "@emotion/react";
import { CheckboxChangeEvent } from "antd/es/checkbox";
import Overflow from "rc-overflow";
import { first, omit, isEqual, isEmpty } from "lodash";
import { usePrevious } from "../hooks/usePrevious";
import { findOptionByValue, generateTagLabel } from "../utils/dataTransform/dataTransform";

const { Text } = Typography;

export interface Option {
	label: string;
	value: string;
	children?: Option[];
}

interface MultipleCascadeSelectProps {
	onChange?: (value: CascadeValueItem[]) => void;
	defaultValues?: CascadeValueItem[];
	options: Option[];
	disabled?: boolean;
}

interface CascadeValue {
	[key: string]: CascadeValueItem;
}

export interface CascadeValueItem {
	value: string;
	childValue: string | undefined;
}

const popupContainerStyles = css({
	padding: "13px 20px",
	minWidth: 350,
	maxHeight: 500,
	overflow: "scroll",
});

const generateSelectContainerStyle = (disabled: boolean) => {
	return disabled
		? css`
				border: none;
				cursor: not-allowed;
				* {
					pointer-events: none;
					background: #f5f5f5 !important;
				}
				.ant-select-selector {
					border: 1px solid #d9d9d9 !important;
				}
				.ant-select-selection-overflow-item {
					opacity: 0.4;
				}
		  `
		: css`
				border: none;
		  `;
};
const generateTriggerStyle = (disabled: boolean) => {
	return disabled
		? css`
				cursor: not-allowed;
				&:hover {
					border: none;
				}
		  `
		: {};
};

const valuesToCascadeValue = (values: CascadeValueItem[]) => {
	return values.reduce((res, item) => {
		return {
			...res,
			[item.value]: {
				value: item.value,
				childValue: item.childValue,
			},
		};
	}, {});
};

const findExistsTags = (options: Option[], tags: CascadeValueItem[]) =>
	tags.filter(tag => {
		const option = findOptionByValue(options, tag.value);
		return option && findOptionByValue(option?.children || [], tag.childValue);
	});

export const MultipleCascadeSelect: FC<MultipleCascadeSelectProps> = ({
	onChange,
	defaultValues = [],
	options = [],
	disabled = false,
}) => {
	const prevOptions = usePrevious(options);
	const [popupVisible, setPopupVisible] = useState(false);
	const [cascadeValue, setCascadeValue] = useState<CascadeValue>(
		valuesToCascadeValue(defaultValues)
	);
	const prevCascadeValue = usePrevious(cascadeValue);
	const tags = Object.values(cascadeValue);
	const prevTags = prevCascadeValue ? Object.values(prevCascadeValue) : [];
	const checkedValues = tags.map(v => v.value);
	const prevCheckedValues = prevTags.map(v => v.value);

	const [visibleMap, setVisibleMap] = useState<{ [key: string]: boolean }>(
		checkedValues.reduce(
			(res, item) => ({
				...res,
				[item]: true,
			}),
			{}
		)
	);

	const onCollapseChange = (id: string) =>
		setVisibleMap(state => ({
			...state,
			[id]: !state[id],
		}));

	const onRadioChange = (e: RadioChangeEvent, option: Option) => {
		setCascadeValue((state: CascadeValue) => ({
			...state,
			[option.value]: {
				value: option.value,
				childValue: e.target.value,
			},
		}));
	};

	const handleCheckBoxChange = (e: CheckboxChangeEvent, option: Option) => {
		setCascadeValue(state => {
			if (e.target.checked) {
				return {
					...state,
					[option.value]: {
						value: option.value,
						childValue: first(option.children)?.value,
					},
				};
			}
			return omit(state, option.value);
		});
	};

	useEffect(() => {
		setVisibleMap(state => ({
			...state,
			...checkedValues
				.filter(value => !prevCheckedValues.includes(value))
				.reduce(
					(res, item) => ({
						...res,
						[item]: true,
					}),
					{}
				),
		}));
		onChange && onChange(Object.values(cascadeValue));
	}, [cascadeValue]);

	useEffect(() => {
		if (!isEmpty(options) && !isEqual(prevOptions, options)) {
			const existsTags = findExistsTags(options, tags);
			setCascadeValue(
				!isEmpty(existsTags)
					? valuesToCascadeValue(existsTags)
					: valuesToCascadeValue(defaultValues)
			);
		}
	}, [options]);
	return (
		<Trigger
			css={generateTriggerStyle(disabled)}
			action={["click"]}
			destroyPopupOnHide
			popupClassName={"ant-select-dropdown ant-select-dropdown-placement-bottomLeft"}
			popup={
				<div css={popupContainerStyles}>
					<Text type={"secondary"}>Pipelines</Text>
					<div css={{ marginTop: 20, padding: "0 15px" }}>
						<Checkbox.Group value={checkedValues}>
							{options.map((option, key) => (
								<Row key={key} css={{ marginBottom: 8 }}>
									<Col>
										<span
											onClick={() => onCollapseChange(option.value)}
											css={{ display: "inline-block", cursor: "pointer" }}>
											{visibleMap[option.value] ? <CaretDownOutlined /> : <CaretRightOutlined />}
										</span>
										<Checkbox
											value={option.value}
											onChange={e => handleCheckBoxChange(e, option)}
											disabled={tags.length === 1 && !!cascadeValue[option.value]}>
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
							))}
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
			<button
				className={`ant-select ant-select-multiple`}
				css={generateSelectContainerStyle(disabled)}
				disabled={disabled}>
				<div className={"ant-select-selector"}>
					<Overflow
						prefixCls={"ant-select-selection-overflow"}
						data={tags}
						maxCount={3}
						renderRest={() => <div className={"ant-select-selection-item"}>more</div>}
						renderItem={tag => {
							const label = generateTagLabel(options, tag);
							return (
								label && (
									<div className={"ant-select-selection-overflow-item"}>
										<Tag
											className={"ant-select-selection-item"}
											css={{ alignItems: "center" }}
											closable={tags.length > 1}
											onClose={() => setCascadeValue(omit(cascadeValue, tag.value))}>
											{label}
										</Tag>
									</div>
								)
							);
						}}
					/>
				</div>
			</button>
		</Trigger>
	);
};
