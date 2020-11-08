import React from 'react';
import { Button, Modal, ModalHeader, ModalBody } from 'reactstrap';
import { CopyToClipboard } from 'react-copy-to-clipboard';

class ShareableLinkModal extends React.Component {

  render() {
    let newSessionUrl = process.env.REACT_APP_FRONTEND_BASEURL + '/session/' + this.props.sessionId;

    return (
      <div>
        <Modal backdrop={true} isOpen={this.props.showShareableLink} toggle={this.props.toggleShareableLink}>
          <ModalHeader style={{backgroundColor: '#30475e'}} toggle={this.props.toggleShareableLink}>Shareable Link</ModalHeader>
          <ModalBody style={{backgroundColor: '#222831'}}>
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