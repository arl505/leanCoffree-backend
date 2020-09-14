import React from 'react';

class DiscussionPage extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      topics: props.topics,
      userDisplayName: props.userInfo.displayName,
    }
  }

  getAllTopicCards() {
    let topicsElements = [];

    let allTopics = this.state.topics;
    allTopics.sort((a,b) => {
      let aLength = a.voters.length;
      let bLength = b.voters.length;
      return bLength - aLength;
    });
    for(let i = 1; i <= allTopics.length - 1; i++) {
      let text = allTopics[i].text;
      let votes = allTopics[i].voters.length;
      topicsElements.push(
        <div key={i.toString()} class="cardItem discussionCardItem" style={{gridRow: i}}>
          <p class="topicsText">{text}</p>
          <p class="votesText">Votes: {votes}</p>
        </div>
      );
    }
    return topicsElements;
  }

  render() {
    return (
      <div class="session-grid-container">
        <div class="discussCards-grid-container">
          {this.getAllTopicCards()}
        </div>
          <div class="currentDiscussionItem">
            <h5 class="currentTopicHeader">Current discussion item</h5>
            <h2 class="currentTopicHeader">{this.state.topics[0].text}</h2>
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