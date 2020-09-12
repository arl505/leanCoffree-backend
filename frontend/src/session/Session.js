import React from 'react';
import Axios from 'axios';
import './session.css'

let stompClient = null;
class Session extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      isSessionVerified: false,
      sessionId: "",
      sessionStatus: "",
      userDisplayName: "",
      websocketUserId: "",
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

  onConnected(frame) {
    stompClient.subscribe('/topic/session/' + this.state.sessionId, this.onUpdateUsers);
    this.setState({websocketUserId: frame.headers['user-name']})
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
      Axios.post(process.env.REACT_APP_BACKEND_BASEURL + "/refresh-users", {displayName: self.state.userDisplayName, sessionId: self.state.sessionId, command: "ADD", websocketUserId: self.state.websocketUserId})
      .then(function (response) {
        if(response.data.status === "SUCCESS") {
          self.setState({sessionStatus: "QUERYING_AND_VOTING"});
        } else {
          alert(response.data.error);
        }
      })
      .catch(function (error) {
        console.log("Error while adding displayname to backend: " + error + " - " + JSON.stringify(error.response.data))
      });
      
    }
  }

  captureDisplayNameChange(event) {
    this.setState({userDisplayName: event.target.value});
  }

  getAllHere() {
    let allHereListItems = [];
    for(let i = 0; i < this.state.usersInAttendance.length; i++) {
      let username = this.state.usersInAttendance[i];
      if(username === this.state.userDisplayName) {
        allHereListItems.push(<li class="usernameList"><b>{this.state.usersInAttendance[i]}</b></li>);
      } else {
        allHereListItems.push(<li class="usernameList">{this.state.usersInAttendance[i]}</li>);
      }
    }
    return (
      <ul class="usernameList">
        {allHereListItems}
      </ul>
    )
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
        <div class="voting-grid-container">
          <div class="voting-grid-item cardsItem">
            <div>
              <p>
              Here is where I will put cards and stuffHere is where I will put cards and stuffHere is where I will put cards and stuffHere is where I will put cards and stuffHere is where I will put cards and stuffHere is where I will put cards and stuffHere is where I will put cards and stuffHere is where I will put cards and stuffHere is where I will put cards and stuffHere is where I will put cards and stuffHere is where I will put cards and stuffHere is where I will put cards and stuffHere is where I will put cards and stuffHere is where I will put cards and stuffHere is where I will put cards and stuff
              </p>
            </div>
          </div>
          <div class="voting-grid-item usersItem">
            <div>All here:</div>
            <div>{this.getAllHere()}</div>
          </div>
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