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
          <ModalHeader toggle={this.props.toggle}>Create a new Lean Coffree session</ModalHeader>
          <ModalBody>
            Click below to launch a new Lean Coffree session!
          </ModalBody>
          <ModalFooter>
            <Button color="primary" onClick={this.redirectToSession}>Create lean coffee session</Button>
            <Button color="secondary" onClick={this.props.toggle}>Cancel</Button>
          </ModalFooter>
        </Modal>
      </div>
    );
  }
}

export default NewSessionModal;