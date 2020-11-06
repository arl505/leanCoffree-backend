import React from 'react';
import Axios from 'axios';
import { Button, Modal, ModalHeader, ModalBody, ModalFooter } from 'reactstrap';

class NewSessionModal extends React.Component {

  redirectToSession() {
    Axios.post(process.env.REACT_APP_BACKEND_BASEURL + '/create-session', null)
      .then((response) => {
        window.location = process.env.REACT_APP_FRONTEND_BASEURL + '/session/' + response.data.id.toString();
      })
      .catch(function (error) {
        console.log("Received an error while creating new session: " + error);
      });
  }

  render() {
    return (
      <div>
        <Modal isOpen={this.props.isOpen} toggle={this.props.toggle}>
          <ModalHeader style={{backgroundColor: '#133b5c'}} toggle={this.props.toggle}>Create a new Lean Coffree session</ModalHeader>
          <ModalBody style={{backgroundColor: '#1d2d50'}}>
            Click below to launch a new Lean Coffree session!
            <br/>
            <br/>
            <Button color="primary" onClick={this.redirectToSession}>Create lean coffee session</Button>
            <text> </text>
            <Button color="secondary" onClick={this.props.toggle}>Cancel</Button>
          </ModalBody>
        </Modal>
      </div>
    );
  }
}

export default NewSessionModal;