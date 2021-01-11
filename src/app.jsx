import React from "react";
import ReactDom from "react-dom";

import "./style.less";


function App() {
  return <div className={"test"}>Hello React</div>
}


ReactDom.render(<App/>,document.getElementById("root"));