import React from 'react';
import { Button, Modal, ModalHeader, ModalBody } from 'reactstrap';
import Axios from 'axios';

class UsernamePromptModal extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      localDisplayName: ''
    }
    this.submitUsername = this.submitUsername.bind(this);
  }

  submitUsername() {
    let self = this;
    if(this.state.localDisplayName !== "" && this.props.sessionId !== "") {
      Axios.post(process.env.REACT_APP_BACKEND_BASEURL + "/refresh-users", {displayName: self.state.localDisplayName, sessionId: self.props.sessionId, command: "ADD", websocketUserId: self.props.websocketUserId})
      .then((response) => {
        if(response.data.status === "SUCCESS") {
          self.props.setUserDisplayNameAndSessionStatusAndShareableLink(self.state.localDisplayName, response.data.sessionStatus, response.data.showShareableLink);
        } else {
          alert(response.data.error);
        }
      })
      .catch((error) => {
        alert("Error while adding displayname to backend\n" + error)
      }); 
    }
  }

  render() {
    return (
      <Modal isOpen={this.props.isNameModalOpen}>
        <ModalHeader style={{backgroundColor: '#133b5c'}}>Enter your name</ModalHeader>
        <ModalBody style={{backgroundColor: '#1d2d50'}}>
          <input name="displayName" placeholder="Johnny C." onChange={(event) => this.setState({localDisplayName: event.target.value})}></input>
          <br/>
          <br/>
          <Button color="primary" onClick={this.submitUsername}>Submit</Button>
        </ModalBody>
      </Modal>
    )
  }
}

export default UsernamePromptModal;