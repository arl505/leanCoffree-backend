import React from 'react';
import { Button, Modal, ModalHeader, ModalBody } from 'reactstrap';
import { CopyToClipboard } from 'react-copy-to-clipboard';

class ShareableLinkModal extends React.Component {

  render() {
    let newSessionUrl = process.env.REACT_APP_FRONTEND_BASEURL + '/session/' + this.props.sessionId;

    return (
      <div>
        <Modal backdrop={true} isOpen={this.props.showShareableLink} toggle={this.props.toggleShareableLink}>
          <ModalHeader style={{backgroundColor: '#133b5c'}} toggle={this.props.toggleShareableLink}>Shareable Link</ModalHeader>
          <ModalBody style={{backgroundColor: '#1d2d50'}}>
            Your meeting link is: {newSessionUrl}
            <br/>
            <CopyToClipboard text={newSessionUrl}>
              <button>Copy to clipboard</button>
            </CopyToClipboard>
            <br/>
            <br/>
            <Button color="primary" onClick={this.props.toggleShareableLink}>Close</Button>
          </ModalBody>
        </Modal>
      </div>
    )
  }
}

export default ShareableLinkModal;