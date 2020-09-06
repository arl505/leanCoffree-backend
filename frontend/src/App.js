import React from 'react';
import { BrowserRouter as Router, Switch, Route } from "react-router-dom";
import Splash from "./splash/Splash";

function App() {
  return (
    <Router>
      <Switch>
        <Route path="/" component={Splash}/>
      </Switch>
    </Router>
  );
}

export default App;