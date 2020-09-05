import React from 'react';
import { Button, Modal, ModalHeader, ModalBody, ModalFooter } from 'reactstrap';

class JoinSessionModal extends React.Component {

  render() {
    return (
      <div>
        <Modal modalTransition isOpen={this.props.isOpen} toggle={this.props.toggle}>
          <ModalHeader>Join a Lean Coffree session</ModalHeader>
          <ModalBody>
            Here is where I will ask for details on the session to join: link or id
          </ModalBody>
          <br/>
          <ModalFooter>
            <Button onClick={this.props.toggle}>Join lean coffee session</Button>{' '}
            <Button onClick={this.props.toggle}>Cancel</Button>
          </ModalFooter>
        </Modal>
      </div>
    )
  }
}

export default JoinSessionModal;