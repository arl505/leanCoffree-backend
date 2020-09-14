import React from 'react';
import Axios from 'axios';
import DiscussionPage from './DiscussionPage';
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
      usersInAttendance: [],
      cardSubmissionText: "",
      topics: [],
      votesLeft: 3,
    }
    this.componentDidMount = this.componentDidMount.bind(this);
    this.submitDisplayName = this.submitDisplayName.bind(this);
    this.submitCard = this.submitCard.bind(this);
    this.transitionToDiscussion = this.transitionToDiscussion.bind(this);
    this.getAllHere = this.getAllHere.bind(this);
  }

  componentDidMount() {
    let windowHref = window.location.href;
    let url = windowHref.match(process.env.REACT_APP_SESSION_REGEX);
    if(url === null) {
      return window.location = process.env.REACT_APP_FRONTEND_BASEURL;
    }

    let sessionIdFromAddress = url[0].match("[0-9, a-f]{8}-[0-9, a-f]{4}-[0-9, a-f]{4}-[0-9, a-f]{4}-[0-9, a-f]{12}");
    if(sessionIdFromAddress === null) {
      return window.location = process.env.REACT_APP_FRONTEND_BASEURL;
    }

    var self = this;  
    Axios.post(process.env.REACT_APP_BACKEND_BASEURL + '/verify-session/' + sessionIdFromAddress, null)
      .then((response) => {
        if(self.isVerificationResponseValid(response, sessionIdFromAddress[0]) && (response.data.sessionDetails.sessionStatus === "STARTED" || response.data.sessionDetails.sessionStatus === "DISCUSSING")) {
          self.connectToWebSocketServer();
          self.setState({
            isSessionVerified: true,
            sessionId: response.data.sessionDetails.sessionId,
            sessionStatus: "ASK_FOR_USERNAME",
          })
        } else {
          return window.location = process.env.REACT_APP_FRONTEND_BASEURL;
        }
      })
      .catch((error) => {
        console.log("Received an error while verifying session: " + error);
        return window.location = process.env.REACT_APP_FRONTEND_BASEURL;
      });
  }

  isVerificationResponseValid(response, sessionIdFromAddress) {
    return response.data.verificationStatus === "VERIFICATION_SUCCESS" && response.data.sessionDetails.sessionId === sessionIdFromAddress;
  }

  connectToWebSocketServer() {
    const Stomp = require('stompjs')
    var SockJS = require('sockjs-client')
    SockJS = new SockJS(process.env.REACT_APP_BACKEND_BASEURL + '/lean-coffree')
    stompClient = Stomp.over(SockJS);
    stompClient.connect({}, 
      (frame) => {
        stompClient.subscribe('/topic/discussion-topics/session/' + this.state.sessionId, 
          (payload) => this.setState({topics: JSON.parse(payload.body)})
        );
        stompClient.subscribe('/topic/status/session/' + this.state.sessionId,
          (payload) => this.setState({sessionStatus: payload.body})
        );
        stompClient.subscribe('/topic/users/session/' + this.state.sessionId, 
          (payload) => {
            let updateUsersBody = JSON.parse(payload.body);
            this.setState({usersInAttendance: updateUsersBody.displayNames});
          }
        );
        this.setState({websocketUserId: frame.headers['user-name']})
      }, 
      (error) => {
        alert('Could not connect you: ' + error);
      }
    );
  }

  submitDisplayName() {
    let self = this;
    if(this.state.userDisplayName !== "" && this.state.sessionId !== "") {
      Axios.post(process.env.REACT_APP_BACKEND_BASEURL + "/refresh-users", {displayName: self.state.userDisplayName, sessionId: self.state.sessionId, command: "ADD", websocketUserId: self.state.websocketUserId})
      .then((response) => {
        if(response.data.status === "SUCCESS") {
          self.setState({sessionStatus: response.data.sessionStatus});
        } else {
          alert(response.data.error);
        }
      })
      .catch((error) => {
        alert("Error while adding displayname to backend\n" + error)
      });
      
    }
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

  submitCard() {
    if(this.state.cardSubmissionText !== "") {
      Axios.post(process.env.REACT_APP_BACKEND_BASEURL + "/submit-topic", {submissionText: this.state.cardSubmissionText, sessionId: this.state.sessionId})
        .then((response) => {
          if(response.data.status === "SUCCESS") {
            this.setState({cardSubmissionText: ""})
          } else {
            alert(response.data.error);
          }
        })
        .catch((error) => 
          alert("Unable to subit discussion topic\n" + error)
        );
    }
  }

  populateCards() {
    let topicsElements = [];

    let allTopics = this.state.topics;
    for(let i = 0; i < allTopics.length; i++) {
      let text = allTopics[i].text;
      let votes = allTopics[i].voters.length;
      let votingButton;
      if(allTopics[i].voters.includes(this.state.userDisplayName)) {
        votingButton = <button id="cardButton" onClick={() => this.postVoteForTopic(text, 'UNCAST')}>UnVote</button>;
      } else if(this.state.votesLeft !== 0) {
        votingButton = <button id="cardButton" onClick={() => this.postVoteForTopic(text, 'CAST')}>Vote</button>;
      }

      // i + 1 because first square taken by compose card
      // mod by 5 to get column number, count is 1 based so add 1 to result
      let columnNum = ((i + 1) % 5) + 1;

      // i + 1 because first square taken by compose card
      // divide by 5 to get row number, count is 1 based so add 1 to result
      let rowNum = Math.floor((i + 1) / 5) + 1;
      topicsElements.push(
        <div class="cardItem" style={{gridColumn: columnNum, gridRow: rowNum}}>
          <p id="topicText">{text}</p>
          <p id="votesText">Votes: {votes}</p>
          {votingButton}
        </div>
      );
    }

    return (
      <div class="cards-grid-container">
        <div class="cardItem composeCard" style={{gridRow: 1, gridColumn: 1}}>
          <textarea id="composeTextArea" value={this.state.cardSubmissionText} onChange={(event) => {this.setState({cardSubmissionText: event.target.value});}} placeholder="Submit a discussion topic!"/>
          <button id="cardButton" onClick={this.submitCard}>Submit</button>
        </div>

        {topicsElements}
      </div>
    )
  }

  postVoteForTopic(topicText, commandType) {
    if(commandType === "CAST") {
      let newVotesLeft = this.state.votesLeft - 1;
      this.setState({votesLeft: newVotesLeft})
    } else {
      let newVotesLeft = this.state.votesLeft + 1;
      this.setState({votesLeft: newVotesLeft})
    }

    Axios.post(process.env.REACT_APP_BACKEND_BASEURL + "/post-vote", {command: commandType, sessionId: this.state.sessionId, text: topicText, voterDisplayName: this.state.userDisplayName})
      .then((response) => {
        if(response.data.status !== "SUCCESS") {
          alert(response.data.error);
        }
      })
      .catch((error) => 
        alert("Unable to submit vote\n" + error)
      );
  }

  transitionToDiscussion() {
    Axios.post(process.env.REACT_APP_BACKEND_BASEURL + "/transition-to-discussion/" + this.state.sessionId, {})
    .then((response) => {
      if(response.data.status !== "SUCCESS") {
        alert(response.data.error);
      } else {
        this.setState({sessionStatus: "DISCUSSING"})
      }
    })
    .catch((error) => {
      alert("Unable to transition to next section\n" + error)
    })
  }

  render() {
    if(this.state.sessionStatus === "ASK_FOR_USERNAME") {
      return (
        <div>
          Enter your display name
          <input name="displayName" placeholder="Johnny C." onChange={(event) => {this.setState({userDisplayName: event.target.value});}}></input>
          <button onClick={this.submitDisplayName}>Submit</button>
        </div>
      )
    }

    else if (this.state.sessionStatus === "STARTED") {
      return (
        <div class="session-grid-container">
          <div class="session-grid-item cardsSection">
            {this.populateCards()}
          </div>
          <div class="session-grid-item usersSection">
            <div>All here:</div>
            <div>{this.getAllHere()}</div>
            <div class="nextSectionButton">
              <button onClick={this.transitionToDiscussion}>End voting and go to next section</button>
            </div>
          </div>
        </div>
      )
    }

    else if (this.state.sessionStatus === "DISCUSSING") {
      return (
        <DiscussionPage getAllHere={this.getAllHere} topics={this.state.topics} userInfo={{displayName: this.state.userDisplayName}} />
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