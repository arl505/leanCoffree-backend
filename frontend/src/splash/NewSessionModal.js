import React from 'react';
import { Button, Modal, ModalHeader, ModalBody, ModalFooter } from 'reactstrap';
import { CopyToClipboard } from 'react-copy-to-clipboard';

class NewSessionModal extends React.Component {

  render() {
    return (
      <div>
        <Modal isOpen={this.props.isOpen} toggle={this.props.toggle}>
          <ModalHeader toggle={this.props.toggle}>Launch a new Lean Coffree session</ModalHeader>
          <ModalBody>
            Your meeting link is: https://localhost:3000/new-session/1234
            <CopyToClipboard text="https://localhost:3000/new-session/1234" onCopy={() => this.setState({copied: true})}>
              <button>Copy to clipboard</button>
            </CopyToClipboard>
          </ModalBody>
          <ModalFooter>
            <Button color="primary" onClick={this.props.toggle}>Start lean coffee session</Button>
            <Button color="secondary" onClick={this.props.toggle}>Cancel</Button>
          </ModalFooter>
        </Modal>
      </div>
    );
  }
}

export default NewSessionModal;