import React from 'react';
import BaseSplashPage from './BaseSplashPage'
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
    let basePage = <BaseSplashPage toggleLaunchNewSessionModal={this.toggleLaunchNewSessionModal} toggleJoinSessionModal={this.toggleJoinSessionModal}/>;
    let newSessionModal = this.state.displayLaunchNewSessionModal === false 
      ? null
      : null;
    let joinSessionModal = this.state.displayLaunchNewSessionModal === false 
      ? null
      : null;
    
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
