import React from 'react';
import Axios from 'axios';
import isMobile from 'react-device-detect';
import AllUsersList from './AllUsersList';
import VotingTopicsGrid from './VotingTopicsGrid';

class VotingPage extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      activeTab: 'TOPICS'
    }
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
    if(isMobile !== true && this.props.width > 652) {
      let nextSectionButton = this.props.topics.discussionBacklogTopics !== undefined && this.props.topics.discussionBacklogTopics.length >= 2 && this.props.usersInAttendance.moderator.includes(this.props.userDisplayName) && this.props.isNameModalOpen === false
        ? <div class="nextSectionButton">
            <button class="button" onClick={this.transitionToDiscussion}>End voting and go to next section</button>
          </div>
        : null;

      return (
        <div class="session-grid-container">

          <VotingTopicsGrid topics={this.props.topics} usersInAttendance={this.props.usersInAttendance}
            userDisplayName={this.props.userDisplayName} sessionStatus={this.props.sessionStatus}
            sessionId={this.props.sessionId} containerSizeVw={75}/>

          <div class="users-container">
            <AllUsersList usersInAttendance={this.props.usersInAttendance} userDisplayName={this.props.userDisplayName} toggleShareableLink={this.props.toggleShareableLink}/>
            {nextSectionButton}
          </div>

        </div>
      )
    } else {
      let nextSectionButton = this.props.topics.discussionBacklogTopics !== undefined && this.props.topics.discussionBacklogTopics.length >= 2 && this.props.usersInAttendance.moderator.includes(this.props.userDisplayName) && this.props.isNameModalOpen === false
        ? <div style={{gridRow: 1, gridColumn: 3}}>
            <button class="button" style={{width: '100%', height: '100%', color: '#bfa488'}} onClick={this.transitionToDiscussion}>End voting and go to next section</button>
          </div>
        : null;

      let activeTab = this.state.activeTab === 'USERS'
        ? <div style={{minWidth: '100vw', minHeight: '90vh', maxWidth: '100vw', maxHeight: '90vh', backgroundColor: '#1e5f74', textAlign: 'center'}}>
            <AllUsersList usersInAttendance={this.props.usersInAttendance} userDisplayName={this.props.userDisplayName} toggleShareableLink={this.props.toggleShareableLink}/>
          </div>
         : <VotingTopicsGrid topics={this.props.topics} usersInAttendance={this.props.usersInAttendance}
            userDisplayName={this.props.userDisplayName} sessionStatus={this.props.sessionStatus}
            sessionId={this.props.sessionId} containerSizeVw={96}/>;
    
      let usersColor = this.state.activeTab === 'USERS'
        ? '#fcdab7'
        : '#bfa488';
      let topicsColor = this.state.activeTab === 'TOPICS'
        ? '#fcdab7'
        : '#bfa488';

      let tabsDivBackgroundColor = this.state.activeTab === 'USERS' 
        ? '#1e5f74'
        : '#1d2d50';

      return (
        <div>
          {activeTab}

          <div style={{width: '100vw', height: '10vh', backgroundColor: tabsDivBackgroundColor}}/>

          <div style={{position: 'fixed', bottom: 0, width: '100vw', display: 'grid', height: '10vh', backgroundColor: tabsDivBackgroundColor, borderRadius: '20px 20px 0 0'}}>
            <div style={{gridRow: 1, gridColumn: 1}}>
              <button class="button" style={{width: '100%', height: '100%', color: topicsColor}} onClick={() => this.setState({activeTab: 'TOPICS'})}>Topics</button>
            </div>
            <div style={{gridRow: 1, gridColumn: 2}}>
              <button class="button" style={{width: '100%', height: '100%', color: usersColor}} onClick={() => this.setState({activeTab: 'USERS'})}>Users</button>
            </div>
            {nextSectionButton}
          </div>
        </div>
      )
    }
  }
}

export default VotingPage;