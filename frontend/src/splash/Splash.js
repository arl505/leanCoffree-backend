import React from 'react';
import BaseSplashPage from './BaseSplashPage'
import NewSessionModal from './NewSessionModal'
import JoinSessionModal from './JoinSessionModal'

class Splash extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      displayBasePage: true,
      displayLaunchNewSessionModal: false,
      displayJoinSessionModal: false,
    }
    this.toggleBasePage = this.toggleBasePage.bind(this);
    this.toggleLaunchNewSessionModal = this.toggleLaunchNewSessionModal.bind(this);
    this.toggleJoinSessionModal = this.toggleJoinSessionModal.bind(this);
  }

  toggleBasePage() {
    this.setState({displayBasePage: !this.state.displayBasePage})
  }

  toggleLaunchNewSessionModal() {
    this.setState({displayLaunchNewSessionModal: !this.state.displayLaunchNewSessionModal, displayJoinSessionModal: false})
  }

  toggleJoinSessionModal() {
    this.setState({displayJoinSessionModal: !this.state.displayJoinSessionModal, displayLaunchNewSessionModal: false})
  }

  render() {
    let basePage = this.state.displayBasePage === false
      ? null
      : <BaseSplashPage toggleLaunchNewSessionModal={this.toggleLaunchNewSessionModal} toggleJoinSessionModal={this.toggleJoinSessionModal}/>;
    let newSessionModal = this.state.displayLaunchNewSessionModal === false 
      ? null
      : <NewSessionModal isOpen={this.state.displayLaunchNewSessionModal} toggle={this.toggleLaunchNewSessionModal}/>;
    let joinSessionModal = this.state.displayJoinSessionModal === false 
      ? null
      : <JoinSessionModal isOpen={this.state.displayJoinSessionModal} toggle={this.toggleJoinSessionModal}/>;
    
    return (
      <div>
        {basePage}
        {newSessionModal}
        {joinSessionModal}
      </div>
    )
  }
}

export default Splash;
