import React from 'react';
import Axios from 'axios';
import AllUsersList from './AllUsersList'

class VotingPage extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      topicSubmissionText: '',
      votesLeft: 3,
    }
    this.sumbitTopic = this.sumbitTopic.bind(this);
    this.transitionToDiscussion = this.transitionToDiscussion.bind(this);
  }

  sumbitTopic() {
    if(this.state.topicSubmissionText !== "") {
      Axios.post(process.env.REACT_APP_BACKEND_BASEURL + "/submit-topic", {submissionText: this.state.topicSubmissionText, sessionId: this.props.sessionId, displayName: this.props.userDisplayName})
        .then((response) => {
          if(response.data.status === "SUCCESS") {
            this.setState({topicSubmissionText: ""})
          } else {
            alert(response.data.error);
          }
        })
        .catch((error) => 
          alert("Unable to subit discussion topic\n" + error)
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

    Axios.post(process.env.REACT_APP_BACKEND_BASEURL + "/post-vote", {command: commandType, sessionId: this.props.sessionId, text: topicText, voterDisplayName: this.props.userDisplayName, authorDisplayName: authorDisplayName})
      .then((response) => {
        if(response.data.status !== "SUCCESS") {
          alert(response.data.error);
        }
      })
      .catch((error) => 
        alert("Unable to submit vote\n" + error)
      );
  }

  deleteTopic(topic) {
    if(window.confirm("Confirm if you'd like to delete the following topic: " + topic.text)) {
      Axios.post(process.env.REACT_APP_BACKEND_BASEURL + '/delete-topic', {sessionId: this.props.sessionId, topicText: topic.text, authorName: topic.authorDisplayName})
        .then((response) => {
          if(response.data.status !== "SUCCESS") {
            alert(response.data.error);
          } else {
            let votesLeft = this.state.votesLeft + 1;
            this.setState({votesLeft: votesLeft});
          }
        })
        .catch((error) => 
          alert("Unable to delete topic\n" + error)
        );
    }
  }

  transitionToDiscussion() {
    if(this.props.topics.discussionBacklogTopics.length >= 2) {
      Axios.post(process.env.REACT_APP_BACKEND_BASEURL + "/transition-to-discussion/" + this.props.sessionId, {})
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

  populateCards() {
    let topicsElements = [];

    let allTopics = this.props.topics.discussionBacklogTopics;
    if(allTopics !== undefined) {
      for(let i = 0; i < allTopics.length; i++) {
        let text = allTopics[i].text;
        let votes = allTopics[i].voters.length;
        let votingButton;
        if(allTopics[i].voters.includes(this.props.userDisplayName)) {
          votingButton = <button onClick={() => this.postVoteForTopic(text, 'UNCAST', allTopics[i].authorDisplayName)}>UnVote</button>;
        } else if(this.state.votesLeft !== 0) {
          votingButton = <button style={{maxHeight: '100%'}} onClick={() => this.postVoteForTopic(text, 'CAST', allTopics[i].authorDisplayName)}>Vote</button>;
        }
        let deleteButton = this.props.usersInAttendance.moderator.includes(this.props.userDisplayName) && !this.props.sessionStatus.includes("ASK_FOR_USERNAME")
          ? <button onClick={() => this.deleteTopic(allTopics[i])} style={{maxHeight: '100%'}}>Delete</button>
          : null;

        let columnNum = ((i + 1) % 5) + 1;
        let rowNum = Math.floor((i + 1) / 5) + 1;
        topicsElements.push(
          <div key={i.toString()} class="cardItem" style={{gridColumn: columnNum, gridRow: rowNum}}>
            <p id="topicText">{text}</p>
            <p id="votesText">Votes: {votes}</p>
            <div style={{fontSize: '50%', height: '10%', position: "absolute", bottom: '2.5%', right: '2.5%'}}>
              {deleteButton}
              <text> </text>
              {votingButton}
            </div>
          </div>
        );
      }
    }

    return (
      <div class="cards-grid-container">
        <div class="cardItem composeCard" style={{gridRow: 1, gridColumn: 1}}>
          <textarea id="composeTextArea" value={this.state.topicSubmissionText} onChange={(event) => this.setState({topicSubmissionText: event.target.value})} placeholder="Submit a discussion topic!"/>
          <button id="cardButton" onClick={this.sumbitTopic}>Submit</button>
        </div>

        {topicsElements}
      </div>
    )    
  }
  
  render() {
    let nextSectionButton = this.props.topics.discussionBacklogTopics !== undefined && this.props.topics.discussionBacklogTopics.length >= 2 && this.props.usersInAttendance.moderator.includes(this.props.userDisplayName) && this.props.isNameModalOpen === false
      ? <div class="nextSectionButton">
          <button class="button" onClick={this.transitionToDiscussion}>End voting and go to next section</button>
        </div>
      : null;
    return (
      <div>
        <div class="session-grid-container">
          <div class="session-grid-item cardsSection">
            {this.populateCards()}
          </div>
          <div class="session-grid-item usersSection">
            <AllUsersList usersInAttendance={this.props.usersInAttendance} userDisplayName={this.props.userDisplayName} toggleShareableLink={this.props.toggleShareableLink}/>
            {nextSectionButton}
          </div>
        </div>
      </div>
    )
  }
}

export default VotingPage;