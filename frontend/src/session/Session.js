import React from 'react';
import Axios from 'axios';

class Session extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      isSessionValid: true,
    }
    this.componentDidMount = this.componentDidMount.bind(this);
  }

  invalidSessionProvided() {
    window.location = process.env.REACT_APP_FRONTEND_BASEURL;
  }

  componentDidMount() {
    let windowHref = window.location.href;
    let url = windowHref.match(process.env.REACT_APP_SESSION_REGEX);
    if(url === null) {
      return this.invalidSessionProvided();
    }

    let sessionId = url[0].match("[0-9, a-f]{8}-[0-9, a-f]{4}-[0-9, a-f]{4}-[0-9, a-f]{4}-[0-9, a-f]{12}");
    if(sessionId === null) {
      return this.invalidSessionProvided();
    }

    var self = this;  
    Axios.post(process.env.REACT_APP_BACKEND_BASEURL + '/verify-session/' + sessionId, null)
      .then(function (response) {
        self.setState({newSessionId: response.data.toString()});
      })
      .catch(function (error) {
        console.log("Received an error while verifying session: " + error);
        return self.invalidSessionProvided();
      });
  }

  render() {
    return (
      <div>
        Session page
      </div>
    )
  }
}

export default Session;