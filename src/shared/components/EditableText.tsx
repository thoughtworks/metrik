import { Input, Typography } from "antd";
import React, { FC, useState } from "react";
import { EditOutlined } from "@ant-design/icons";
import { BLUE_5 } from "../constants/styles";

const { Title } = Typography;

interface EditableTextProps {
	defaultValue: string;
	onEditDone?: (value: string) => void;
}

export const EditableText: FC<EditableTextProps> = ({ defaultValue, onEditDone }) => {
	const [editable, setEditable] = useState(false);
	const [value, setValue] = useState(defaultValue);

	const handleEdit = (nextValue: string) => {
		setValue(nextValue);
		setEditable(false);
		onEditDone && onEditDone(nextValue);
	};

	return (
		<div css={{ height: 45, display: "flex" }}>
			{editable ? (
				<Input
					css={{ cursor: "pointer" }}
					type={"text"}
					defaultValue={value}
					autoFocus
					onBlur={evt => handleEdit(evt.target.value || defaultValue)}
					onKeyDown={e => {
						if (e.key === "Enter") {
							handleEdit((e.target as HTMLInputElement).value || defaultValue);
						}
					}}
				/>
			) : (
				<div
					css={{ display: "flex", alignItems: "baseline", cursor: "pointer" }}
					onClick={() => {
						setEditable(true);
					}}>
					<Title level={2} style={{ marginBottom: 0 }}>
						{value}
					</Title>
					<EditOutlined style={{ fontSize: 16, color: BLUE_5, marginLeft: 8 }} />
				</div>
			)}
		</div>
	);
};
