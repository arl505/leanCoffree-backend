import React from 'react';
import { Button, Modal, ModalHeader, ModalBody, ModalFooter } from 'reactstrap';

class JoinSessionModal extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      sessionUrl: "",
    }
    this.captureChange = this.captureChange.bind(this);
    this.redirectToSession = this.redirectToSession.bind(this);
  }

  redirectToSession() {
    let sessionGuid = this.getSessionGuidFromUrlOrReturnNullIfInvalid(this.state.sessionUrl);
    if(sessionGuid !== null) {
      window.location = process.env.REACT_APP_FRONTEND_BASEURL + '/session/' + sessionGuid;
    }
  }

  getSessionGuidFromUrlOrReturnNullIfInvalid(sessionUrl) {
    let matchedUrl = sessionUrl.match(process.env.REACT_APP_SESSION_REGEX);
    return matchedUrl === null
      ? null
      : sessionUrl.match("[0-9, a-f]{8}-[0-9, a-f]{4}-[0-9, a-f]{4}-[0-9, a-f]{4}-[0-9, a-f]{12}");
  }

  captureChange(event) {
    this.setState({sessionUrl: event.target.value});
  }

  render() {
    let placeholder = process.env.REACT_APP_FRONTEND_BASEURL + "/session/67caf957-d01a-4bf2-85db-a4d4bb0fb80e";
    return (
      <div>
        <Modal isOpen={this.props.isOpen} toggle={this.props.toggle}>
          <ModalHeader toggle={this.props.toggle}>Join a Lean Coffree session</ModalHeader>
          <ModalBody>
            Session link:
            <br/> 
            <input type="text" onChange={this.captureChange} placeholder={placeholder}></input>
          </ModalBody>
          <ModalFooter>
            <Button color="primary" onClick={this.redirectToSession}>Join lean coffee session</Button>
            <Button color="secondary" onClick={this.props.toggle}>Cancel</Button>
          </ModalFooter>
        </Modal>
      </div>
    )
  }
}

export default JoinSessionModal;