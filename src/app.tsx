import React,{FC} from "react";
import ReactDom from "react-dom";

import "./style.less";
import "./util"

const App: FC = () => {
  return <div className={"test"}>Hello React</div>
};


ReactDom.render(<App/>,document.getElementById("root"));