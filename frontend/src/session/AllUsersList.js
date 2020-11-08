import React from 'react';
import moderatorIcon from './moderatorIcon.png'

class AllUsersList extends React.Component {

  render() {
    let allHereListItems = [];
    if(this.props.usersInAttendance.displayNames !== undefined) {

      for(let i = 0; i < this.props.usersInAttendance.displayNames.length; i++) {
        let username = this.props.usersInAttendance.displayNames[i];
        let usernameElement = this.props.userDisplayName === username
          ? <b>{username}</b>
          : username;
        let crownVisibility = this.props.usersInAttendance.moderator.includes(username) 
          ? 'visible'
          : 'hidden'

        allHereListItems.push(
          <div>
            <img alt="moderator crown icon" class="moderatorCrownIcon" src={moderatorIcon} style={{visibility: crownVisibility}}/>
            <text> </text>
            <li key={i.toString()} class="usernameListItem">{usernameElement}</li>
          </div>);
      } 
      allHereListItems.push(<button class="button" onClick={this.props.toggleShareableLink}>Invite more</button>);
    }
    return (
      <div>
        <p class="allHereHeading">All here:</p>
        <ul class="usernameList">
          {allHereListItems}
        </ul>
      </div>
    )
  }
}

export default AllUsersList;