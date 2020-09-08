import React from 'react';
import Axios from 'axios';

let stompClient = null;
class Session extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      isSessionVerified: false,
      sessionId: "",
      sessionStatus: "",
      userDisplayName: "",
      usersInAttendance: []
    }
    this.componentDidMount = this.componentDidMount.bind(this);
    this.captureDisplayNameChange = this.captureDisplayNameChange.bind(this);   
    this.submitDisplayName = this.submitDisplayName.bind(this);   
    this.onUpdateUsers = this.onUpdateUsers.bind(this);
    this.onConnected = this.onConnected.bind(this);
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
          self.connectToWebSocketServer();
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

  connectToWebSocketServer() {
    const Stomp = require('stompjs')
    var SockJS = require('sockjs-client')
    SockJS = new SockJS(process.env.REACT_APP_BACKEND_BASEURL + '/lean-coffree')
    stompClient = Stomp.over(SockJS);
    stompClient.connect({}, this.onConnected, this.onError);   
  }

  onConnected() {
    stompClient.subscribe('/topic/session/' + this.state.sessionId, this.onUpdateUsers);
  }

  onUpdateUsers(payload) {
    let updateUsersBody = JSON.parse(payload.body);
    this.setState({usersInAttendance: updateUsersBody.displayNames});
  }

  onError(error) {
    alert('Could not connect you: ' + error);
  }

  submitDisplayName() {
    let self = this;
    if(this.state.userDisplayName !== "" && this.state.sessionId !== "") {
      Axios.post(process.env.REACT_APP_BACKEND_BASEURL + "/refresh-users", {displayName: self.state.userDisplayName, sessionId: self.state.sessionId, command: "ADD"})
      .then(function (response) {
        if(response.data.status === "SUCCESS") {
          self.setState({sessionStatus: "QUERYING_AND_VOTING"});
        } else {
          alert(response.data.error);
        }
      })
      .catch(function (error) {
        console.log("Error while adding displayname to backend: " + error)
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
      let allHere = this.state.usersInAttendance.join(", ");
      return (
        <div>
          <p>Hello {this.state.userDisplayName}</p>
          <p>All here: {allHere}</p>
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