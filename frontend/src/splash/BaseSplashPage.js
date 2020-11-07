import React from 'react';
import {isMobile} from 'react-device-detect';
import websitePicture from './website.png';
import './BaseSplashPage.css';

class BaseSplashPage extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      width: window.innerWidth
    }
  }

  componentWillMount() {
    window.addEventListener('resize', () => this.setState({width: window.innerWidth}));
  }

  render() {
    let isMobileString = isMobile === true || this.state.width < 700
      ? "_mobile"
      : "";
    return (
      <div>
        <p class={isMobile === true ? "baseHeader_mobile" : "baseHeader"}>Lean Coffree, a <b>free</b> lean coffee discussion tool</p>
        <br/>
        <img class={"websitePicture" + isMobileString} src={websitePicture} alt="demo of lean coffree session"/>
        <div class={"joinSessionDiv" + isMobileString}>
          <p class={"subHeader" + isMobileString}>Join or create a lean coffree session, for free!</p>
          <button class={"button"} onClick={this.props.toggleCreateNewSessionModal}><b>Create a new Lean Coffree session</b></button>
          <br/>
          <br/>
          <button class={"button"} onClick={this.props.toggleJoinSessionModal}><b>Join a Lean Coffree session</b></button>
        </div>
      </div>
    )
  }
}

export default BaseSplashPage;