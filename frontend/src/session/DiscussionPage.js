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
        <div key={i.toString()} class="cardItem" style={{gridRow: i, gridColumn: 1, marginLeft: '2.5vw', marginRight: '2.5vw'}}>
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
            <h5 style={{textAlign: "center"}}>Current discussion item</h5>
            <h2 style={{textAlign: "center"}}>{this.state.topics[0].text}</h2>
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