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
    if(this.state.topics !== undefined) {
      if(this.state.currentTopicSecondsRemaining === -1 && this.state.topics !== {}) {
        let endSeconds = Math.round(new Date(this.state.topics.currentDiscussionItem.endTime).getTime() / 1000);
        let nowSeconds = Math.round(new Date().getTime() / 1000);
        this.setState({currentTopicSecondsRemaining: Math.max(0, endSeconds - nowSeconds)})
      }
      
      setInterval(() => {
        if(this.state.topics !== undefined) {
          let endSeconds = Math.round(new Date(this.state.topics.currentDiscussionItem.endTime).getTime() / 1000);
          let nowSeconds = Math.round(new Date().getTime() / 1000);
          if(Math.max(0, endSeconds - nowSeconds) !== 0) {
            this.setState({currentTopicSecondsRemaining: Math.max(0, endSeconds - nowSeconds)})
          } else if(this.state.finished === false) {
            let body;
            if(this.state.topics.discussionBacklogTopics.length !== 0) {
              body = {command: "NEXT", sessionId: this.props.sessionId, currentTopicText: this.state.topics.currentDiscussionItem.text, nextTopicText: this.state.topics.discussionBacklogTopics[0].text, currentTopicAuthorDisplayName: this.state.topics.currentDiscussionItem.authorDisplayName, nextTopicAuthorDisplayName: this.state.topics.discussionBacklogTopics[0].authorDisplayName};
            } else {
              body = {command: "FINISH", sessionId: this.props.sessionId, currentTopicText: this.state.topics.currentDiscussionItem.text, currentTopicAuthorDisplayName: this.state.topics.currentDiscussionItem.authorDisplayName};
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
          }
      }, 500);
    }
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
          <div key={i.toString()} class="cardItem discussionCardItem" style={{gridRow: i + 1}}>
            <p class="topicText">{text}</p>
            <p class="votesText">Votes: {votes}</p>
          </div>
        );
      }
    }
    return topicsElements;
  }

  getDiscussedCards(isFinished) {
    if(this.state.topics.discussedTopics !== undefined && this.state.topics.discussedTopics.length !== 0) {
      let allDiscussedTopicsElements = [];
      for(let i = 0; i <= this.state.topics.discussedTopics.length; i++) {
        if(i === (this.state.topics.discussedTopics.length)) {
          allDiscussedTopicsElements.push(
            <div key={i.toString()} class="finalSpacer row1" style={{gridColumn: i + 1}}/>
          )
        } else {
          allDiscussedTopicsElements.push(
            <div key={i.toString()} class="cardItem row1" style={{gridColumn: i + 1}}>
              <p class="topicText">{this.state.topics.discussedTopics[i].text}</p>
            </div>
          )
        }
      }

      if(!isFinished) {
        return (
          <div class="discussedItemsSection">
            {allDiscussedTopicsElements}
          </div>
        )
      } else {
        return (
          <div class="discussedItemsSection column1">
            {allDiscussedTopicsElements}
          </div>
        )
      }
    }
  }

  render() {  
    let countdown;
    if(this.state.currentTopicSecondsRemaining !== -1) {
      let minutesNum = Math.floor(this.state.currentTopicSecondsRemaining / 60);
      let secondsNum = this.state.currentTopicSecondsRemaining % 60;
      if(!isNaN(minutesNum) && !isNaN(secondsNum)) {
        if(secondsNum < 10) {
          secondsNum = ("0" + secondsNum).slice(-2);
        }
        countdown = <h5 class="countdown">{minutesNum} : {secondsNum}</h5>
      }
    }

    let currentDiscussionItemHeader = this.state.topics.currentDiscussionItem === undefined || this.state.topics.currentDiscussionItem.text === undefined
      ? null
      : "Current discussion item";
    
    let currentDiscussionItem = this.state.topics.currentDiscussionItem === undefined || this.state.topics.currentDiscussionItem.text === undefined
      ? "Session completed!"
      : this.state.topics.currentDiscussionItem.text;

    let allTopicCards = this.getAllTopicCards();
    let allTopicCardsContainer = allTopicCards.length === 0
      ? null
      : <div class="discussCards-grid-container">{allTopicCards}</div>;

    let currentDiscussionItemContainer = allTopicCardsContainer === null 
      ? <div class="currentDiscussionItem column1">
          <h5 class="currentTopicHeader">{currentDiscussionItemHeader}</h5>
          <h2 class="currentTopicHeader">{currentDiscussionItem}</h2>
          {countdown}
        </div>
      : <div class="currentDiscussionItem">
          <h5 class="currentTopicHeader">{currentDiscussionItemHeader}</h5>
          <h2 class="currentTopicHeader">{currentDiscussionItem}</h2>
          {countdown}
        </div>;
      
    return (
      <div class="session-grid-container">
        {allTopicCardsContainer}
        {currentDiscussionItemContainer}
        {this.getDiscussedCards(allTopicCardsContainer === null)}

        <div class="session-grid-item usersSection column3">
          <div>All here:</div>
          <div>{this.props.getAllHere()}</div>
        </div>
      </div>
    )
  }
}

export default DiscussionPage;