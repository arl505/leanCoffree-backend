import React from 'react';
import Axios from 'axios';
import AllUsersList from './AllUsersList';
import CurrentDiscussionItem from './CurrentDiscussionItem'
import DiscussionBackog from './DiscussionBacklog';
import DiscussionVotingModal from './DiscussionVotingModal';
import DiscussedTopics from './DiscussedTopics';

class DiscussionPage extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      topics: props.topics,
      userDisplayName: props.userDisplayName,
      votes: props.discussionVotes,
      currentTopicSecondsRemaining: -1,
      finished: false,
      isVotingModalOpen: false,
    }
    this.loadNextTopic = this.loadNextTopic.bind(this);
    this.toggleVotingModal = this.toggleVotingModal.bind(this);
    this.endSession = this.endSession.bind(this);
    this.pullNewDiscussionTopic = this.pullNewDiscussionTopic.bind(this);
    this.deleteTopic = this.deleteTopic.bind(this);
  }

  componentDidMount() {
    if(this.state.topics !== undefined) {
      if(this.state.currentTopicSecondsRemaining === -1 && this.state.topics !== {} && this.state.topics.currentDiscussionItem !== undefined) {
        let endSeconds = Math.round(new Date(this.state.topics.currentDiscussionItem.endTime).getTime() / 1000);
        let nowSeconds = Math.round(new Date().getTime() / 1000);
        this.setState({currentTopicSecondsRemaining: Math.max(0, endSeconds - nowSeconds)})
      }
      
      setInterval(() => {
        if(this.state.topics !== undefined && this.state.topics.currentDiscussionItem !== undefined) {
          let endSeconds = Math.round(new Date(this.state.topics.currentDiscussionItem.endTime).getTime() / 1000);
          let nowSeconds = Math.round(new Date().getTime() / 1000);
          if(Math.max(0, endSeconds - nowSeconds) !== 0) {
            this.setState({currentTopicSecondsRemaining: Math.max(0, endSeconds - nowSeconds)})
          } else if(this.state.finished === false && this.state.isVotingModalOpen !== true) {
            this.setState({isVotingModalOpen: true})
          }
        }
      }, 500);
    }
  }

  componentDidUpdate(prevProps) {
    if(prevProps.topics !== this.props.topics) {
      this.setState({topics: this.props.topics});
    }
  }

  deleteTopic(topicText, author) {
    if(window.confirm("Confirm if you'd like to delete the following topic: " + topicText)) {
      Axios.post(process.env.REACT_APP_BACKEND_BASEURL + '/delete-topic', {sessionId: this.props.sessionId, topicText: topicText, authorName: author})
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

  pullNewDiscussionTopic(text, author) {
    let body;
    let confirmationMessage;
    if(this.state.topics.currentDiscussionItem.text === undefined) {
      body = {command: "REVERT_TO_DISCUSSION", sessionId: this.props.sessionId, nextTopicText: text, nextTopicAuthorDisplayName: author};
      confirmationMessage = "Confirm you'd like to pull the following topic for discussion: " + text;
    } else {
      body = {command: "NEXT", sessionId: this.props.sessionId, currentTopicText: this.state.topics.currentDiscussionItem.text, nextTopicText: text, currentTopicAuthorDisplayName: this.state.topics.currentDiscussionItem.authorDisplayName, nextTopicAuthorDisplayName: author};
      confirmationMessage = "Pulling this topic for discussion will conclude the current discussion topic, proceed?"
    }
    if (window.confirm(confirmationMessage)) {
      Axios.post(process.env.REACT_APP_BACKEND_BASEURL + "/refresh-topics", body)
        .then((response) => {
          if(response.data.status !== "SUCCESS") {
            alert(response.data.error);
          }
        })
        .catch((error) => 
          alert("Unable to pull topic for discussion\n" + error)
        );
    } 
  }

  loadNextTopic() {
    let body;
    if(this.state.topics.discussionBacklogTopics.length !== 0) {
      body = {command: "NEXT", sessionId: this.props.sessionId, currentTopicText: this.state.topics.currentDiscussionItem.text, nextTopicText: this.state.topics.discussionBacklogTopics[0].text, currentTopicAuthorDisplayName: this.state.topics.currentDiscussionItem.authorDisplayName, nextTopicAuthorDisplayName: this.state.topics.discussionBacklogTopics[0].authorDisplayName};
    } else {
      body = {command: "FINISH", sessionId: this.props.sessionId, currentTopicText: this.state.topics.currentDiscussionItem.text, currentTopicAuthorDisplayName: this.state.topics.currentDiscussionItem.authorDisplayName};
    }
    Axios.post(process.env.REACT_APP_BACKEND_BASEURL + "/refresh-topics", body)
      .then((response) => {
        if(response.data.status !== "SUCCESS") {
          if(response.data.error === "Mocked finish!") {
            this.setState({finished: true})
          }
          alert(response.data.error);
        } else {
          this.setState({isVotingModalOpen: false})
        }
      })
      .catch((error) => 
        alert("Unable to refresh topics\n" + error)
      );
  }

  endSession() {
    if(window.confirm("Confirm you'd like to end session. All session data will be immediately deleted")) {
      Axios.post(process.env.REACT_APP_BACKEND_BASEURL + '/end-session/' + this.props.sessionId, {})
        .then((response) => {
          if(response.data.status !== "SUCCESS") {
            alert(response.data.error);
          } else {
            return window.location = process.env.REACT_APP_FRONTEND_BASEURL;
          }
        })
        .catch((error) => 
          alert("Unable to end session\n" + error)
        );
    }
  }

  toggleVotingModal() {
    let isModalOpen = !this.state.isVotingModalOpen;
    this.setState({isVotingModalOpen: isModalOpen});
  }

  sessionControlButtons() {
    if(!(this.props.usersInAttendance.moderator.includes(this.props.userDisplayName)) || this.props.isUsernameModalOpen !== false) {
      return null;
    }

    let finishTopicButton = this.state.topics.currentDiscussionItem !== undefined && this.state.topics.currentDiscussionItem.text !== undefined
      ? <button onClick={() => {if(window.confirm("Confirm you'd like to finish the current topic below\n" + this.state.topics.currentDiscussionItem.text)) this.loadNextTopic()}}>Finish Topic</button>
      : null;

    return (
      <div style={{textAlign: 'center', position: 'absolute', bottom: '1vh'}}>
        {finishTopicButton}
        <button style={{marginTop: '1vh'}} onClick={this.endSession}>End Session</button>
      </div>
    );
  }

  render() {  
    return (
      <div class="session-grid-container">

        <CurrentDiscussionItem currentTopicSecondsRemaining={this.state.currentTopicSecondsRemaining} topics={this.props.topics}/>

        <DiscussionBackog userDisplayName={this.props.userDisplayName} usersInAttendance={this.props.usersInAttendance} 
          isUsernameModalOpen={this.props.isUsernameModalOpen} pullNewDiscussionTopic={this.pullNewDiscussionTopic}
          deleteTopic={this.deleteTopic} topics={this.props.topics} 
          setTopics={this.props.setTopics} sessionId={this.props.sessionId}/>

        <DiscussedTopics isBacklogOpen={this.state.topics.discussionBacklogTopics === undefined || this.state.topics.discussionBacklogTopics.length <= 0}
          topics={this.props.topics} isUsernameModalOpen={this.props.isUsernameModalOpen}
          userDisplayName={this.props.userDisplayName} usersInAttendance={this.props.usersInAttendance}
          pullNewDiscussionTopic={this.pullNewDiscussionTopic} deleteTopic={this.deleteTopic}/>

        <div class="session-grid-item usersSection column3">
          <AllUsersList usersInAttendance={this.props.usersInAttendance} userDisplayName={this.props.userDisplayName} toggleShareableLink={this.props.toggleShareableLink}/>
          {this.sessionControlButtons()}
        </div>

        <DiscussionVotingModal loadNextTopic={this.loadNextTopic} toggleVotingModal={this.toggleVotingModal} 
          sessionId={this.props.sessionId} userDisplayName={this.props.userDisplayName} 
          discussionVotes={this.props.discussionVotes} currentTopicSecondsRemaining={this.state.currentTopicSecondsRemaining} 
          isUsernameModalOpen={this.props.isUsernameModalOpen} isVotingModalOpen={this.state.isVotingModalOpen} 
          usersInAttendance={this.props.usersInAttendance}/>
        
      </div>
    )
  }
}

export default DiscussionPage;