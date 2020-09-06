import React from 'react';
import BaseSplashPage from './BaseSplashPage'
import NewSessionModal from './NewSessionModal'
import JoinSessionModal from './JoinSessionModal'

class Splash extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      displayLaunchNewSessionModal: false,
      displayJoinSessionModal: false,
    }
    this.toggleLaunchNewSessionModal = this.toggleLaunchNewSessionModal.bind(this);
    this.toggleJoinSessionModal = this.toggleJoinSessionModal.bind(this);
  }

  toggleLaunchNewSessionModal() {
    this.setState({displayLaunchNewSessionModal: !this.state.displayLaunchNewSessionModal, displayJoinSessionModal: false})
  }

  toggleJoinSessionModal() {
    this.setState({displayJoinSessionModal: !this.state.displayJoinSessionModal, displayLaunchNewSessionModal: false})
  }

  render() {    
    return (
      <div>
        <BaseSplashPage toggleLaunchNewSessionModal={this.toggleLaunchNewSessionModal} toggleJoinSessionModal={this.toggleJoinSessionModal}/>
        <NewSessionModal isOpen={this.state.displayLaunchNewSessionModal} toggle={this.toggleLaunchNewSessionModal}/>
        <JoinSessionModal isOpen={this.state.displayJoinSessionModal} toggle={this.toggleJoinSessionModal}/>
      </div>
    )
  }
}

export default Splash;
