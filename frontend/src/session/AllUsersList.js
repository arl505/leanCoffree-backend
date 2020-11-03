import React from 'react';

class AllUsersList extends React.Component {

  render() {
    let allHereListItems = [];
    if(this.props.usersInAttendance.displayNames !== undefined) {
      for(let i = 0; i < this.props.usersInAttendance.displayNames.length; i++) {
        let username = this.props.usersInAttendance.displayNames[i];
        if(this.props.userDisplayName === username) {
          if(username === this.props.usersInAttendance.moderator) {
            allHereListItems.push(<li key={i.toString()} style={{color:'#d4af37'}} class="usernameList"><b>{username}</b></li>);
          } else {
            allHereListItems.push(<li key={i.toString()} class="usernameList"><b>{username}</b></li>);
          }
        } else {
          if(username === this.props.usersInAttendance.moderator) {
            allHereListItems.push(<li key={i.toString()} style={{color:'#d4af37'}} class="usernameList">{username}</li>);
          } else {
            allHereListItems.push(<li key={i.toString()} class="usernameList">{username}</li>);
          }
        }
      }
      allHereListItems.push(<button onClick={this.props.toggleShareableLink}>Invite more</button>);
    }
    return (
      <ul class="usernameList">
        {allHereListItems}
      </ul>
    )
  }
}

export default AllUsersList;