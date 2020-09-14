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
    for(let i = 0; i < allTopics.length; i++) {
      let text = allTopics[i].text;
      let votes = allTopics[i].voters.length;
      topicsElements.push(
        <div class="cardItem" style={{gridRow: i, gridColumn: 1}}>
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
            {this.state.topics[0].text}
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