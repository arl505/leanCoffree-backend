import React from 'react';
import BaseSplashPage from './BaseSplashPage'
import NewSessionModal from './NewSessionModal'
import JoinSessionModal from './JoinSessionModal'

class Splash extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      displayCreateNewSessionModal: false,
      displayJoinSessionModal: false,
    }
    this.toggleCreateNewSessionModal = this.toggleCreateNewSessionModal.bind(this);
    this.toggleJoinSessionModal = this.toggleJoinSessionModal.bind(this);
  }

  toggleCreateNewSessionModal() {
    this.setState({displayCreateNewSessionModal: !this.state.displayCreateNewSessionModal, displayJoinSessionModal: false})
  }

  toggleJoinSessionModal() {
    this.setState({displayJoinSessionModal: !this.state.displayJoinSessionModal, displayCreateNewSessionModal: false})
  }

  render() {    
    return (
      <div>
        <BaseSplashPage toggleCreateNewSessionModal={this.toggleCreateNewSessionModal} toggleJoinSessionModal={this.toggleJoinSessionModal}/>
        <NewSessionModal isOpen={this.state.displayCreateNewSessionModal} toggle={this.toggleCreateNewSessionModal}/>
        <JoinSessionModal isOpen={this.state.displayJoinSessionModal} toggle={this.toggleJoinSessionModal}/>
      </div>
    )
  }
}

export default Splash;
