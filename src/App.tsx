import React, {FC} from "react";
import ReactDom from "react-dom";
import {Button,} from 'antd';
import "./App.less";


export const App: FC = () => {
  return <div className={"app"}>Hello 4 key metrics   <Button>Click me</Button></div>
};


ReactDom.render(<App/>, document.getElementById("root"));