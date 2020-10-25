import React from 'react';
import Axios from 'axios';
import DiscussionPage from './DiscussionPage';
import './session.css'
import { Button, Modal, ModalHeader, ModalBody, ModalFooter } from 'reactstrap';

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
      topics: {},
      votesLeft: 3,
      currentTopicEndTime: '',
      isNameModalOpen: true,
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
        if(self.isVerificationResponseValid(response, sessionIdFromAddress[0])) {
          self.connectToWebSocketServer();
          let status = response.data.sessionDetails.sessionStatus === "STARTED"
            ? "ASK_FOR_USERNAME_STARTED"
            : "ASK_FOR_USERNAME_DISCUSSING";
          self.setState({
            isSessionVerified: true,
            sessionId: response.data.sessionDetails.sessionId,
            sessionStatus: status,
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
    return response.data.verificationStatus === "VERIFICATION_SUCCESS" && response.data.sessionDetails.sessionId === sessionIdFromAddress 
      && (response.data.sessionDetails.sessionStatus === "STARTED" || response.data.sessionDetails.sessionStatus === "DISCUSSING");
  }

  connectToWebSocketServer() {
    const Stomp = require('stompjs')
    var SockJS = require('sockjs-client')
    SockJS = new SockJS(process.env.REACT_APP_BACKEND_BASEURL + '/lean-coffree')
    stompClient = Stomp.over(SockJS);
    if(process.env.REACT_APP_STOMP_CLIENT_DEBUG === 'false') {
      stompClient.debug = null;
    }
    stompClient.connect({}, 
      (frame) => {
        stompClient.subscribe('/topic/discussion-topics/session/' + this.state.sessionId, 
          (payload) => {
            if(JSON.parse(payload.body).currentDiscussionItem.text === undefined) {
              this.setState({topics: JSON.parse(payload.body)});
            } else {
              this.setState({topics: JSON.parse(payload.body), currentTopicEndTime: JSON.parse(payload.body).currentDiscussionItem.endTime}, () => {
                if(this.state.sessionStatus !== "" && !this.state.sessionStatus.includes("ASK_FOR_USERNAME")) {
                  this.setState({sessionStatus: "DISCUSSING"});
                }
              });
            }
          }
        );
        stompClient.subscribe('/topic/users/session/' + this.state.sessionId, 
          (payload) => {
            let updateUsersBody = JSON.parse(payload.body);
            this.setState({usersInAttendance: updateUsersBody});
          }
        );
        Axios.get(process.env.REACT_APP_BACKEND_BASEURL + "/refresh-users/" + this.state.sessionId);
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
          self.setState({sessionStatus: response.data.sessionStatus, isNameModalOpen: false});
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
    if(this.state.usersInAttendance.displayNames !== undefined) {
      for(let i = 0; i < this.state.usersInAttendance.displayNames.length; i++) {
        let username = this.state.usersInAttendance.displayNames[i];
        if(username === this.state.userDisplayName) {
          if(username === this.state.usersInAttendance.moderator) {
            allHereListItems.push(<li key={i.toString()} style={{color:'#d4af37'}} class="usernameList"><b>{username}</b></li>);
          } else {
            allHereListItems.push(<li key={i.toString()} class="usernameList"><b>{username}</b></li>);
          }
        } else {
          if(username === this.state.usersInAttendance.moderator) {
            allHereListItems.push(<li key={i.toString()} style={{color:'#d4af37'}} class="usernameList">{username}</li>);
          } else {
            allHereListItems.push(<li key={i.toString()} class="usernameList">{username}</li>);
          }
        }
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
      Axios.post(process.env.REACT_APP_BACKEND_BASEURL + "/submit-topic", {submissionText: this.state.cardSubmissionText, sessionId: this.state.sessionId, displayName: this.state.userDisplayName})
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

    let allTopics = this.state.topics.discussionBacklogTopics;
    if(allTopics !== undefined) {
      for(let i = 0; i < allTopics.length; i++) {
        let text = allTopics[i].text;
        let votes = allTopics[i].voters.length;
        let votingButton;
        if(allTopics[i].voters.includes(this.state.userDisplayName)) {
          votingButton = <button id="cardButton" onClick={() => this.postVoteForTopic(text, 'UNCAST', allTopics[i].authorDisplayName)}>UnVote</button>;
        } else if(this.state.votesLeft !== 0) {
          votingButton = <button id="cardButton" onClick={() => this.postVoteForTopic(text, 'CAST', allTopics[i].authorDisplayName)}>Vote</button>;
        }

        // i + 1 because first square taken by compose card
        // mod by 5 to get column number, count is 1 based so add 1 to result
        let columnNum = ((i + 1) % 5) + 1;

        // i + 1 because first square taken by compose card
        // divide by 5 to get row number, count is 1 based so add 1 to result
        let rowNum = Math.floor((i + 1) / 5) + 1;
        topicsElements.push(
          <div key={i.toString()} class="cardItem" style={{gridColumn: columnNum, gridRow: rowNum}}>
            <p id="topicText">{text}</p>
            <p id="votesText">Votes: {votes}</p>
            {votingButton}
          </div>
        );
      }
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

  postVoteForTopic(topicText, commandType, authorDisplayName) {
    if(commandType === "CAST") {
      let newVotesLeft = this.state.votesLeft - 1;
      this.setState({votesLeft: newVotesLeft})
    } else {
      let newVotesLeft = this.state.votesLeft + 1;
      this.setState({votesLeft: newVotesLeft})
    }

    Axios.post(process.env.REACT_APP_BACKEND_BASEURL + "/post-vote", {command: commandType, sessionId: this.state.sessionId, text: topicText, voterDisplayName: this.state.userDisplayName, authorDisplayName: authorDisplayName})
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
    if(this.state.topics.discussionBacklogTopics.length >= 2) {
      Axios.post(process.env.REACT_APP_BACKEND_BASEURL + "/transition-to-discussion/" + this.state.sessionId, {})
      .then((response) => {
        if(response.data.status !== "SUCCESS") {
          alert(response.data.error);
        }
      })
      .catch((error) => {
        alert("Unable to transition to next section\n" + error)
      })
    }
  }

  render() {
    let usernameModal = !this.state.sessionStatus.includes("ASK_FOR_USERNAME")
      ? null
      : <Modal isOpen={this.state.isNameModalOpen}>
          <ModalHeader>Enter your name</ModalHeader>
          <ModalBody>
            <input name="displayName" placeholder="Johnny C." onChange={(event) => {this.setState({userDisplayName: event.target.value});}}></input>
          </ModalBody>
          <ModalFooter>
            <Button color="primary" onClick={this.submitDisplayName}>Submit</Button>
          </ModalFooter>
        </Modal>

    if((this.state.sessionStatus.includes("STARTED"))) {
      let nextSectionButton = this.state.topics.discussionBacklogTopics !== undefined && this.state.topics.discussionBacklogTopics.length >= 2
        ? <div class="nextSectionButton">
            <button onClick={this.transitionToDiscussion}>End voting and go to next section</button>
          </div>
        : null;
      return (
        <div>
          {usernameModal}
          <div class="session-grid-container">
            <div class="session-grid-item cardsSection">
              {this.populateCards()}
            </div>
            <div class="session-grid-item usersSection">
              <div>All here:</div>
              <div>{this.getAllHere()}</div>
              {nextSectionButton}
            </div>
          </div>
        </div>
      )
    }

    else if (this.state.sessionStatus.includes("DISCUSSING") && this.state.currentTopicEndTime !== null) {
      return (
        <div>
          {usernameModal}
          <DiscussionPage sessionId={this.state.sessionId} getAllHere={this.getAllHere} topics={this.state.topics} currentEndTime={this.state.currentTopicEndTime} userInfo={{displayName: this.state.userDisplayName}} />
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