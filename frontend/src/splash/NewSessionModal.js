import React from 'react';
import Axios from 'axios';
import { Button, Modal, ModalHeader, ModalBody, ModalFooter } from 'reactstrap';
import { CopyToClipboard } from 'react-copy-to-clipboard';

class NewSessionModal extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      newSessionId: "",
    }
    this.componentDidUpdate = this.componentDidUpdate.bind(this);
  }

  componentDidUpdate() {
    if(this.props.isOpen === true && this.state.newSessionId === "") {
      var self = this;
      console.log("Calling backend url: " + process.env.REACT_APP_BACKEND_BASEURL);
      Axios.post(process.env.REACT_APP_BACKEND_BASEURL + '/create-session', null)
      .then(function (response) {
          console.log("received success response from backend: " + response.data.id);
          self.setState({newSessionId: response.data.id.toString()});
        })
        .catch(function (error) {
          console.log("Received an error while creating new session: " + error);
        });
      }
    }

  render() {
    let newSessionUrl = process.env.REACT_APP_FRONTEND_BASEURL + '/session/' + this.state.newSessionId;
    return (
      <div>
        <Modal backdrop="static" isOpen={this.props.isOpen} toggle={this.props.toggle}>
          <ModalHeader toggle={this.props.toggle}>Create a new Lean Coffree session</ModalHeader>
          <ModalBody>
            Your meeting link is: {newSessionUrl}
            <CopyToClipboard text={newSessionUrl} onCopy={() => this.setState({copied: true})}>
              <button>Copy to clipboard</button>
            </CopyToClipboard>
          </ModalBody>
          <ModalFooter>
            <Button color="primary" onClick={this.props.toggle}>Create lean coffee session</Button>
            <Button color="secondary" onClick={this.props.toggle}>Cancel</Button>
          </ModalFooter>
        </Modal>
      </div>
    );
  }
}

export default NewSessionModal;