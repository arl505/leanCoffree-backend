import React from 'react';
import moderatorIcon from './moderatorIcon.png'

class AllUsersList extends React.Component {

  render() {
    let allHereListItems = [];
    if(this.props.usersInAttendance.displayNames !== undefined) {
      for(let i = 0; i < this.props.usersInAttendance.displayNames.length; i++) {
        let username = this.props.usersInAttendance.displayNames[i];
        if(this.props.userDisplayName === username) {
          if(this.props.usersInAttendance.moderator.includes(username)) {
            allHereListItems.push(<div style={{marginTop: '1vw'}}>
              <img src={moderatorIcon} style={{height: "3vh", verticalAlign: 'top'}}></img>
              <text> </text>
              <li key={i.toString()} style={{display: 'inline-block'}} class="usernameList"><b>{username}</b></li>
            </div>);
          } else {
            allHereListItems.push(<li key={i.toString()} class="usernameList"><b>{username}</b></li>);
          }
        } else {
          if(this.props.usersInAttendance.moderator.includes(username)) {
            allHereListItems.push(<div style={{marginTop: '1vw'}}>
              <img src={moderatorIcon} style={{height: "3vh", verticalAlign: 'top'}}></img>
              <text> </text>
              <li key={i.toString()} style={{display: 'inline-block'}} class="usernameList">{username}</li>
            </div>);
          } else {
            allHereListItems.push(<li key={i.toString()} class="usernameList">{username}</li>);
          }
        }
      }
      allHereListItems.push(<button onClick={this.props.toggleShareableLink}>Invite more</button>);
    }
    return (
      <div>
        <p style={{fontWeight: 100, margin: 0}}>All here:</p>
        <ul class="usernameList">
          {allHereListItems}
        </ul>
      </div>
    )
  }
}

export default AllUsersList;