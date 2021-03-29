import { useState } from "react";

export const useModalVisible = () => {
	const [visible, setVisible] = useState(false);

	function handleToggleVisible() {
		setVisible(!visible);
	}

	return { visible, handleToggleVisible };
};
