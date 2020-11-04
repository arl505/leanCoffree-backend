import React from 'react';
import { Button, Modal, ModalHeader, ModalBody, ModalFooter } from 'reactstrap';
import { CopyToClipboard } from 'react-copy-to-clipboard';

class ShareableLinkModal extends React.Component {

  render() {
    let newSessionUrl = process.env.REACT_APP_FRONTEND_BASEURL + '/session/' + this.props.sessionId;

    return (
      <div>
        <Modal backdrop={true} isOpen={this.props.showShareableLink} toggle={this.props.toggleShareableLink}>
          <ModalHeader toggle={this.props.toggleShareableLink}>Shareable Link</ModalHeader>
          <ModalBody>
            Your meeting link is: {newSessionUrl}
            <br/>
            <CopyToClipboard text={newSessionUrl}>
              <button>Copy to clipboard</button>
            </CopyToClipboard>
          </ModalBody>
          <ModalFooter>
            <Button color="primary" onClick={this.props.toggleShareableLink}>Close</Button>
          </ModalFooter>
        </Modal>
      </div>
    )
  }
}

export default ShareableLinkModal;