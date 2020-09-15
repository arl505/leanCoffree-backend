import React from 'react';
import Axios from 'axios';

class DiscussionPage extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      topics: props.topics,
      userDisplayName: props.userInfo.displayName,
      currentTopicSecondsRemaining: -1,
      finished: false
    }
  }

  componentDidMount() {
    if(this.state.currentTopicSecondsRemaining === -1 && this.state.topics !== {}) {
      let endSeconds = Math.round(new Date(this.state.topics.currentDiscussionItem.endTime).getTime() / 1000);
      let nowSeconds = Math.round(new Date().getTime() / 1000);
      this.setState({currentTopicSecondsRemaining: Math.max(0, endSeconds - nowSeconds)})
    }
    
    
    setInterval(() => {
      let endSeconds = Math.round(new Date(this.state.topics.currentDiscussionItem.endTime).getTime() / 1000);
      let nowSeconds = Math.round(new Date().getTime() / 1000);
      if(Math.max(0, endSeconds - nowSeconds) !== 0) {
        this.setState({currentTopicSecondsRemaining: Math.max(0, endSeconds - nowSeconds)})
      } else if(this.state.finished === false) {
        let body;
        if(this.state.topics.discussionBacklogTopics.length !== 0) {
          body = {command: "NEXT", sessionId: this.props.sessionId, currentTopicText: this.state.topics.currentDiscussionItem.text, nextTopicText: this.state.topics.discussionBacklogTopics[0].text, currentTopicAuthorDisplayName: this.state.topics.currentDiscussionItem.authorDisplayName, nextTopicAuthorDisplayName: this.state.topics.discussionBacklogTopics[0].authorDisplayName};
        } else {
          body = {command: "FINISH", sessionId: this.props.sessionId, currentTopicText: this.state.topics.currentDiscussionItem.text, displayName: this.state.userDisplayName, currentTopicAuthorDisplayName: this.state.topics.currentDiscussionItem.authorDisplayName};
        }
        Axios.post(process.env.REACT_APP_BACKEND_BASEURL + "/refresh-topics", body)
          .then((response) => {
            if(response.data.status !== "SUCCESS") {
              if(response.data.error === "Mocked finish!") {
                this.setState({finished: true})
              }
              alert(response.data.error);
            }
          })
          .catch((error) => 
            alert("Unable to submit vote\n" + error)
          );
        }
    }, 500);
  }

  componentDidUpdate(prevProps) {
    if(prevProps.topics !== this.props.topics) {
      this.setState({topics: this.props.topics});
    }
  }

  getAllTopicCards() {
    let topicsElements = [];

    let allTopics = this.state.topics.discussionBacklogTopics;
    if(allTopics !== undefined) {
      for(let i = 0; i < allTopics.length; i++) {
        let text = allTopics[i].text;
        let votes = allTopics[i].voters.length;
        topicsElements.push(
          <div key={i.toString()} class="cardItem discussionCardItem" style={{gridRow: i}}>
            <p class="topicsText">{text}</p>
            <p class="votesText">Votes: {votes}</p>
          </div>
        );
      }
    }
    return topicsElements;
  }

  render() {  
    let countdown;
    if(this.state.currentTopicSecondsRemaining !== -1) {
      let minutesNum = Math.floor(this.state.currentTopicSecondsRemaining / 60);
      let secondsNum = this.state.currentTopicSecondsRemaining % 60;
      if(secondsNum < 10) {
        secondsNum = ("0" + secondsNum).slice(-2);
      }
      countdown = <h5 class="countdown">{minutesNum} : {secondsNum}</h5>
    }
    
    let currentDiscussionItem = this.state.topics.currentDiscussionItem === undefined
      ? null
      : this.state.topics.currentDiscussionItem.text;
    return (
      <div class="session-grid-container">
        <div class="discussCards-grid-container">
          {this.getAllTopicCards()}
        </div>
          <div class="currentDiscussionItem">
            <h5 class="currentTopicHeader">Current discussion item</h5>
            <h2 class="currentTopicHeader">{currentDiscussionItem}</h2>
            {countdown}
          </div>

          <div class="session-grid-item usersSection column3">
            <div>All here:</div>
            <div>{this.props.getAllHere()}</div>
          </div>
      </div>
    )
  }
}

export default DiscussionPage;