import React from 'react';
import Axios from 'axios';

class VotingTopicsGrid extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      topicSubmissionText: '',
      votesLeft: 3,
    }
    this.sumbitTopic = this.sumbitTopic.bind(this);
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

  populateCards(columnCount) {
    let size = this.props.containerSizeVw / columnCount + 'vw';

    let topicsElements = [];
    let allTopics = this.props.topics.discussionBacklogTopics;
    if(allTopics !== undefined) {

      for(let i = 0; i < allTopics.length; i++) {
        let text = allTopics[i].text;
        let votes = allTopics[i].voters.length;

        let deleteButton = this.props.usersInAttendance.moderator.includes(this.props.userDisplayName) && !this.props.sessionStatus.includes("ASK_FOR_USERNAME")
        ? <div class="cardItemDeleteButtonItem">
            <button class="button" onClick={() => this.deleteTopic(allTopics[i])}>Delete</button>
          </div>
        : null;
        
        let votingButtonColumnNum = deleteButton === null ? '2' : '3';

        let votingButton;
        if(allTopics[i].voters.includes(this.props.userDisplayName)) {
          votingButton = <button class="button" style={{marginRight: '5%'}} onClick={() => this.postVoteForTopic(text, 'UNCAST', allTopics[i].authorDisplayName)}>UnVote</button>;
        } else if(this.state.votesLeft !== 0) {
          votingButton = <button class="button" style={{marginRight: '5%'}} onClick={() => this.postVoteForTopic(text, 'CAST', allTopics[i].authorDisplayName)}>Vote</button>;
        }
        votingButton = <div class="cardItemVoteButtonItem" style={{gridColumn: {votingButtonColumnNum}}}>{votingButton}</div>

        let columnNum = ((i + 1) % columnCount) + 1;
        let rowNum = Math.floor((i + 1) / columnCount) + 1;
        
        topicsElements.push(
          <div key={i.toString()} class="cardItem" style={{gridRow: rowNum, gridColumn: columnNum, width: size, height: size}}>
            <p class="cardItemTopicText">{text}</p>
            <div class="cardItemButtonsGrid-container">
              <div class="cardItemVotesItem">Votes: {votes}</div>
              {deleteButton}
              {votingButton}
            </div>
          </div>);
      }
    }
    return topicsElements;
  }

  render() {
    let columnCount;
    let size;
    if(window.innerWidth > 1100) {
      columnCount = 5;
      size = (this.props.containerSizeVw / columnCount) + 'vw';
    }
    else if(window.innerWidth > 900) {
      columnCount = 4;
      size = (this.props.containerSizeVw / columnCount) + 'vw';
    }
    else {
      columnCount = 3;
      size = (this.props.containerSizeVw / columnCount) + 'vw';
    }

    return (
      <div class="votingCards-container">          
        
        <div class="cardItem composeCard" style={{width: size, height: size}}>
          <textarea id="composeTextArea" value={this.state.topicSubmissionText} onChange={(event) => this.setState({topicSubmissionText: event.target.value})} placeholder="Submit a discussion topic!"/>
          <div class="composeCardBottomDiv">
            <button class="button" style={{marginRight: '1%', padding: '.25vw'}} onClick={this.sumbitTopic}>Submit</button>
          </div>
        </div>

        {this.populateCards(columnCount)}
      </div>
    )
  }
}

export default VotingTopicsGrid;