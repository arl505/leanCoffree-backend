import React from 'react';
import { Button, Modal, ModalHeader, ModalBody, ModalFooter } from 'reactstrap';
import Axios from 'axios';

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
      let self = this;
      Axios.post(process.env.REACT_APP_BACKEND_BASEURL + '/verify-session/' + sessionGuid, null)
        .then(function (response) {
          if(self.isVerificationResponseValid(response, sessionGuid[0])) {
            window.location = process.env.REACT_APP_FRONTEND_BASEURL + '/session/' + sessionGuid;
          }
        })
        .catch(function (error) {
          console.log("Received an error while verifying session: " + error);
        });
    }
  }

  isVerificationResponseValid(response, sessionGuid) {
    return response.data.verificationStatus === "VERIFICATION_SUCCESS" && response.data.sessionDetails.sessionId === sessionGuid;
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
          <ModalHeader style={{backgroundColor: '#133b5c'}} toggle={this.props.toggle}>Join a Lean Coffree session</ModalHeader>
          <ModalBody style={{backgroundColor: '#1d2d50'}}>
            Session link:
            <br/> 
            <input style={{width: '95%'}} type="text" onChange={this.captureChange} placeholder={placeholder}></input>
            <br/>
            <br/>
            <Button color="primary" onClick={this.redirectToSession}>Join lean coffee session</Button>
            <text> </text>
            <Button color="secondary" onClick={this.props.toggle}>Cancel</Button>
          </ModalBody>
        </Modal>
      </div>
    )
  }
}

export default JoinSessionModal;