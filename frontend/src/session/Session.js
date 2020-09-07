import React from 'react';
import Axios from 'axios';

class Session extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      isSessionVerified: false,
      sessionId: "",
      sessionStatus: "",
      userDisplayName: "",
    }
    this.componentDidMount = this.componentDidMount.bind(this);
    this.captureDisplayNameChange = this.captureDisplayNameChange.bind(this);   
    this.submitDisplayName = this.submitDisplayName.bind(this);   
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
            sessionId: response.data.sessionDetails.sessionId,
            sessionStatus: "ASK_FOR_USERNAME",
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

  submitDisplayName() {
    if(this.state.userDisplayName !== "" && this.state.sessionId !== "") {
      var self = this;
      Axios.post(process.env.REACT_APP_BACKEND_BASEURL + '/add-user-to-session', {displayName: this.state.userDisplayName, sessionId: this.state.sessionId})
        .then(function (response) {
          if(response.data.status === "SUCCESS") {
            self.setState({sessionStatus: "QUERYING_AND_VOTING"});
          } else {
            alert(response.data.error);
          }
        })
        .catch(function (error) {
          console.log("Received an error while creating new session: " + error);
        }); 
    }
  }

  captureDisplayNameChange(event) {
    this.setState({userDisplayName: event.target.value});
  }

  render() {

    if(this.state.sessionStatus === "ASK_FOR_USERNAME") {
      return (
        <div>
          Enter your display name
          <input name="displayName" placeholder="Johnny C." onChange={this.captureDisplayNameChange}></input>
          <button onClick={this.submitDisplayName}>Submit</button>
        </div>
      )
    }

    else if (this.state.sessionStatus === "QUERYING_AND_VOTING") {
      return (
        <div>
          Hello {this.state.userDisplayName}
        </div>
      )
    }

    else {
      return (
        <div>
          Session page
        </div>
      )
    }
  }
}

export default Session;