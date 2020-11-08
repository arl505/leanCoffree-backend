import React from 'react';
import Axios from 'axios';
import { Button, Modal, ModalHeader, ModalBody } from 'reactstrap';

class DiscussionVotingModal extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      moreTimeValue: '1m',
    }
    this.addTime = this.addTime.bind(this);
  }

  castVote(voteType) {
    Axios.post(process.env.REACT_APP_BACKEND_BASEURL + "/discussion-vote", {voteType: voteType, sessionId: this.props.sessionId, userDisplayName: this.props.userDisplayName})
      .then((response) => {
        if(response.data.status !== "SUCCESS") {
          alert(JSON.stringify(response.data));
        }
      })
      .catch((error) => 
        alert("Unable to refresh topics\n" + error)
      );
  }

  addTime() {
    let increment = this.state.moreTimeValue.toUpperCase();
    let suffix = increment[increment.length - 1]
    increment = increment.slice(0, increment.length - 1);
    increment = suffix + increment;
    Axios.post(process.env.REACT_APP_BACKEND_BASEURL + '/add-time', {increment: increment, sessionId: this.props.sessionId})
      .then((response) => {
        if(response.data.status !== "SUCCESS") {
          alert(JSON.stringify(response.data));
        } else {
          this.props.toggleVotingModal()
        }
      })
      .catch((error) => 
        alert("Unable to refresh topics\n" + error)
      );
  }

  render() {
    let moreTimeVoteCount = this.props.discussionVotes.moreTimeVotesCount === undefined
      ? 0
      : this.props.discussionVotes.moreTimeVotesCount;

    let finishTopicVoteCount = this.props.discussionVotes.finishTopicVotesCount === undefined
      ? 0
      : this.props.discussionVotes.finishTopicVotesCount;

    let modalFooter = this.props.usersInAttendance.moderator === undefined || (this.props.usersInAttendance.moderator.includes(this.props.userDisplayName) && this.props.isUsernameModalOpen === false)
      ? (
        <div>
          <br/>
          <hr/>
          <p style={{textAlign: 'center'}}>Moderator final say</p>
          <div style={{textAlign: 'center'}}>
            <select value={this.state.moreTimeValue} onChange={(event) => this.setState({moreTimeValue: event.target.value})}>
              <option value="30s">30s</option>
              <option selected="selected" value="1m">1m</option>
              <option value="3m">3m</option>
              <option value="5m">5m</option>
              <option value="10m">10m</option>
              <option value="15m">15m</option>
              <option value="30m">30m</option>
              <option value="1h">1h</option>
            </select>
            <text> </text>
            <Button color="success" onClick={this.addTime}>Add {this.state.moreTimeValue} More time</Button>
            <text> </text>
            <Button color="primary" onClick={this.props.loadNextTopic}>Finish Topic</Button>
          </div>
        </div>
      )
      : null;

    return (
      <Modal style={{fontWeight: 100}} isOpen={this.props.currentTopicSecondsRemaining < 2 && this.props.isUsernameModalOpen === false && this.props.isVotingModalOpen} toggle={this.props.toggleDiscussionVotingModal}>
        <ModalHeader style={{backgroundColor: '#30475e'}}>Vote: More Time or Finish Topic</ModalHeader>
        <ModalBody style={{backgroundColor: '#222831'}}>
          <div style={{marginBottom: '5vh'}}>
            More Time Votes: {moreTimeVoteCount}
            <Button style={{display: 'inline-block', float: 'right'}} color="success" onClick={() => this.castVote('MORE_TIME')}>More time</Button>
          </div>
          <div>
            Finish Topic Votes: {finishTopicVoteCount}
            <Button style={{display: 'inline-block', float: 'right'}} color="primary" onClick={() => this.castVote('FINISH_TOPIC')}>Finish Topic</Button>
          </div>
          {modalFooter}
        </ModalBody>
      </Modal>
    )
  }
}

export default DiscussionVotingModal;