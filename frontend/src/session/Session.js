import React from 'react';
import Axios from 'axios';
import DiscussionPage from './DiscussionPage';
import ShareableLinkModal from './ShareableLinkModal';
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
      showShareableLink: false,
      discussionVotes: {},
    }
    this.componentDidMount = this.componentDidMount.bind(this);
    this.submitDisplayName = this.submitDisplayName.bind(this);
    this.submitCard = this.submitCard.bind(this);
    this.transitionToDiscussion = this.transitionToDiscussion.bind(this);
    this.getAllHere = this.getAllHere.bind(this);
    this.toggleShareableLink = this.toggleShareableLink.bind(this);
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



  submitDisplayName() {
    let self = this;
    if(this.state.userDisplayName !== "" && this.state.sessionId !== "") {
      Axios.post(process.env.REACT_APP_BACKEND_BASEURL + "/refresh-users", {displayName: self.state.userDisplayName, sessionId: self.state.sessionId, command: "ADD", websocketUserId: self.state.websocketUserId})
      .then((response) => {
        if(response.data.status === "SUCCESS") {
          self.setState({sessionStatus: response.data.sessionStatus, isNameModalOpen: false, showShareableLink: response.data.showShareableLink});
        } else {
          alert(response.data.error);
        }
      })
      .catch((error) => {
        alert("Error while adding displayname to backend\n" + error)
      }); 
    }
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

  deleteTopic(topic) {
    if(window.confirm("Confirm if you'd like to delete the following topic: " + topic.text)) {
      Axios.post(process.env.REACT_APP_BACKEND_BASEURL + '/delete-topic', {sessionId: this.state.sessionId, topicText: topic.text, authorName: topic.authorDisplayName})
        .then((response) => {
          if(response.data.status !== "SUCCESS") {
            alert(response.data.error);
          }
        })
        .catch((error) => 
          alert("Unable to delete topic\n" + error)
        );
    }
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
      
      if (this.state.sessionStatus.includes("DISCUSSING") && this.state.currentTopicEndTime !== null) {
      return (
        <div>
          {usernameModal}
          <ShareableLinkModal/>
          <DiscussionPage discussionVotes={this.state.discussionVotes} isUsernameModalOpen={usernameModal !== null} sessionId={this.state.sessionId} getAllHere={this.getAllHere} topics={this.state.topics} currentEndTime={this.state.currentTopicEndTime} userInfo={{displayName: this.state.userDisplayName}} moderatorName={this.state.usersInAttendance.moderator} />
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