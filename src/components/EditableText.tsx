import { Input, Typography } from "antd";
import React, { FC, useState } from "react";

const { Title } = Typography;

interface EditableTextProps {
	defaultValue: string;
	onEditDone?: (value: string) => void;
}

export const EditableText: FC<EditableTextProps> = ({ defaultValue, onEditDone }) => {
	const [editable, setEditable] = useState(false);
	const [value, setValue] = useState(defaultValue);

	return (
		<div css={{ height: 45 }}>
			{editable ? (
				<Input
					type={"text"}
					defaultValue={value}
					onBlur={evt => {
						const nextValue = evt.target.value || defaultValue;

						setValue(nextValue);
						setEditable(false);

						onEditDone && onEditDone(nextValue);
					}}
				/>
			) : (
				<span
					onDoubleClick={() => {
						setEditable(true);
					}}>
					<Title level={2} style={{ marginBottom: 0 }}>
						{value}
					</Title>
				</span>
			)}
		</div>
	);
};
