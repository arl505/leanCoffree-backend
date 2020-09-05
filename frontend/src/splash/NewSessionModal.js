import React from 'react';
import { Button, Modal, ModalHeader, ModalBody, ModalFooter } from 'reactstrap';

class NewSessionModal extends React.Component {

  render() {
    return (
      <div>
        <Modal modalTransition isOpen={this.props.isOpen} toggle={this.props.toggle}>
          <ModalHeader>Launch a new Lean Coffree session</ModalHeader>
          <ModalBody>
            Here is where I will give details on the session (ie here is the link to send out, or enter emails here to invite)
          </ModalBody>
          <br/>
          <ModalFooter>
            <Button onClick={this.props.toggle}>Start lean coffee session</Button>{' '}
            <Button onClick={this.props.toggle}>Cancel</Button>
          </ModalFooter>
        </Modal>
      </div>
    );
  }
}

export default NewSessionModal;