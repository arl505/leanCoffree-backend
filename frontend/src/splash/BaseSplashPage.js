import React from 'react';

class BaseSplashPage extends React.Component {

  render() {
    return (
      <div>
        <h1>Welcome to Lean Coffree, a free lean coffee discussion tool</h1>
        <button onClick={this.props.toggleLaunchNewSessionModal}>Launch a new Lean Coffree session!</button>
        <br/>
        <br/>
        <button onClick={this.props.toggleJoinSessionModal}>Join a Lean Coffree session!</button>
      </div>
    )
  }
}

export default BaseSplashPage;