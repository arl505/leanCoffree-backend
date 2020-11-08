import React from 'react';
import Axios from 'axios';
import isMobile from 'react-device-detect';
import AllUsersList from './AllUsersList';
import VotingTopicsGrid from './VotingTopicsGrid';

class VotingPage extends React.Component {

  constructor(props) {
    super(props);
    this.transitionToDiscussion = this.transitionToDiscussion.bind(this);
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

  render() {
    if (isMobile === true || this.props.width < 652) {
      return <div>ayo</div>
    }

    let nextSectionButton = this.props.topics.discussionBacklogTopics !== undefined && this.props.topics.discussionBacklogTopics.length >= 2 && this.props.usersInAttendance.moderator.includes(this.props.userDisplayName) && this.props.isNameModalOpen === false
      ? <div class="nextSectionButton">
          <button class="button" onClick={this.transitionToDiscussion}>End voting and go to next section</button>
        </div>
      : null;
    
    return (
      <div class="session-grid-container">

        <VotingTopicsGrid topics={this.props.topics} usersInAttendance={this.props.usersInAttendance}
          userDisplayName={this.props.userDisplayName} sessionStatus={this.props.sessionStatus}
          sessionId={this.props.sessionId}/>

        <div class="users-container">
          <AllUsersList usersInAttendance={this.props.usersInAttendance} userDisplayName={this.props.userDisplayName} toggleShareableLink={this.props.toggleShareableLink}/>
          {nextSectionButton}
        </div>

      </div>
    )
  }
}

export default VotingPage;