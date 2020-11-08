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
          <div style={this.props.usersInAttendance.moderator.includes(username)  ? {marginTop: '1vw'} : {}}>
            <img alt="moderator crown icon" src={moderatorIcon} style={{width: "1em", verticalAlign: 'top', visibility: crownVisibility}}></img>
            <text> </text>
            <li key={i.toString()} style={{display: 'inline-block'}} class="usernameList">{usernameElement}</li>
          </div>);
      } 
      allHereListItems.push(<div style={{width: '10vw', marginLeft: '.5vw', marginRight:'2.5vw', textAlign: "center"}}><button class="button" style={{marginTop: "1em"}} onClick={this.props.toggleShareableLink}>Invite more</button></div>);
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