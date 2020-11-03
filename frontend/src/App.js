import React from 'react';
import { BrowserRouter as Router, Switch, Route } from "react-router-dom";
import BaseSessionPage from "./session/BaseSessionPage";
import Splash from "./splash/Splash";

function App() {
  return (
    <Router>
      <Switch>
        <Route path="/session" component={BaseSessionPage}/>
        <Route path="/" component={Splash}/>
      </Switch>
    </Router>
  );
}

export default App;