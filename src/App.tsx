import { Button } from "antd";
import React, { FC, useEffect, useState, useCallback } from "react";
import ReactDom from "react-dom";
import "./App.less";

interface Perosn {
  name: string;
}

const a: Perosn = { age: 12 };

export const App: FC = () => {
  const [state] = useState(1);

  return (
    <div className={"app"}>
      Hello 4 key metrics <Button>Click me</Button>
    </div>
  );
};

ReactDom.render(<App />, document.getElementById("root"));
