import React from 'react';
import VotingPage from './VotingPage'
import DiscussionPage from './DiscussionPage'
import UsernamePromptModal from './UsernamePromptModal';
import ShareableLinkModal from './ShareableLinkModal';
import Axios from 'axios';
import './session.css'

let stompClient = null;
class Session extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      sessionId: '',
      sessionStatus: '',
      userDisplayName: '',
      topicSubmissionText: '',
      websocketUserId: '',
      isNameModalOpen: true,
      showShareableLink: false,
      topics: {},
      discussionVotes: {},
      usersInAttendance: [],
    }
    this.toggleShareableLink = this.toggleShareableLink.bind(this);
    this.setUserDisplayNameAndSessionStatusAndShareableLink = this.setUserDisplayNameAndSessionStatusAndShareableLink.bind(this);
    this.setTopics = this.setTopics.bind(this);
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
            if(payload.body === "") {
              if(!this.state.usersInAttendance.moderator.includes(this.state.userDisplayName)) {
                alert('The moderator has ended the session. All session data has been deleted. Click OK to be redirected or close the window to exit');
              }
              return window.location = process.env.REACT_APP_FRONTEND_BASEURL;
            }
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
        stompClient.subscribe('/topic/discussion-votes/session/' + this.state.sessionId,
          (payload) => {
            this.setState({discussionVotes: JSON.parse(payload.body)});

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

  setUserDisplayNameAndSessionStatusAndShareableLink(displayName, status, showShareableLink) {
    this.setState({userDisplayName: displayName, sessionStatus: status, showShareableLink: showShareableLink, isNameModalOpen: false});
  }

  toggleShareableLink() {
    this.setState({showShareableLink: !this.state.showShareableLink});
  }

  setTopics(topics) {
    this.setState({topics: topics})
  }

  render() {
    let votingPage = this.state.sessionStatus.includes("STARTED")
      ? <VotingPage userDisplayName={this.state.userDisplayName} sessionId={this.state.sessionId} topics={this.state.topics} usersInAttendance={this.state.usersInAttendance} isNameModalOpen={false} sessionStatus={this.state.sessionStatus} toggleShareableLink={this.toggleShareableLink}/>
      : null;

    let discussionPage = this.state.sessionStatus.includes("DISCUSSING")
    ? <DiscussionPage setTopics={this.setTopics} discussionVotes={this.state.discussionVotes} isUsernameModalOpen={this.state.isNameModalOpen} sessionId={this.state.sessionId} topics={this.state.topics} currentEndTime={this.state.currentTopicEndTime} userDisplayName={this.state.userDisplayName} usersInAttendance={this.state.usersInAttendance} />
    : null;

    return (
      <div>
        <UsernamePromptModal isNameModalOpen={this.state.isNameModalOpen} sessionId={this.state.sessionId} websocketUserId={this.state.websocketUserId} setUserDisplayNameAndSessionStatusAndShareableLink={this.setUserDisplayNameAndSessionStatusAndShareableLink}/>
        <ShareableLinkModal sessionId={this.state.sessionId} showShareableLink={this.state.showShareableLink} toggleShareableLink={this.toggleShareableLink}/>
        {votingPage}
        {discussionPage}
      </div>
    )
  }
}

export default Session;