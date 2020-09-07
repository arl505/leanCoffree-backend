import React from 'react';
import Axios from 'axios';

class Session extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      isSessionVerified: false,
      session: {
        metadata: {
          id: "",
          status: ""
        }
      }
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

    let sessionIdFromAddress = url[0].match("[0-9, a-f]{8}-[0-9, a-f]{4}-[0-9, a-f]{4}-[0-9, a-f]{4}-[0-9, a-f]{12}");
    if(sessionIdFromAddress === null) {
      return this.invalidSessionProvided();
    }

    var self = this;  
    Axios.post(process.env.REACT_APP_BACKEND_BASEURL + '/verify-session/' + sessionIdFromAddress, null)
      .then(function (response) {
        if(self.isVerificationResponseValid(response, sessionIdFromAddress[0])) {
          self.setState({
            isSessionVerified: true,
            session: {
              metadata: {
                id: response.data.sessionDetails.sessionId,
                status: response.data.sessionDetails.sessionStatus,
              }
            }
          })
        } else {
          self.invalidSessionProvided();
        }
      })
      .catch(function (error) {
        console.log("Received an error while verifying session: " + error);
        return self.invalidSessionProvided();
      });
  }

  isVerificationResponseValid(response, sessionIdFromAddress) {
    return response.data.verificationStatus === "VERIFICATION_SUCCESS" && response.data.sessionDetails.sessionId === sessionIdFromAddress &&
    response.data.sessionDetails.sessionStatus === "STARTED";
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