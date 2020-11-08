import React from 'react';

class DiscussedTopics extends React.Component {

  getDiscussedCards() {
    let topics = this.props.topics.discussedTopics;
    let allDiscussedTopicsElements = [];
    for(let i = 0; i <= topics.length; i++) {
      let buttons = this.props.usersInAttendance.moderator.includes(this.props.userDisplayName) && this.props.isUsernameModalOpen === false
        ? <div>
            <button  onClick={() => this.props.pullNewDiscussionTopic(topics[i].text, topics[i].authorDisplayName)}>Discuss</button>
            <button onClick={() => this.props.deleteTopic(topics[i].text, topics[i].authorDisplayName)}>Delete</button>
          </div>
        : null;
      if(i === (topics.length)) {
        allDiscussedTopicsElements.push(<div key={i.toString()} style={{gridRow: 1, gridColumn: i + 1, width: '.01vw'}}/>)
      } else {
        allDiscussedTopicsElements.push(
          <div key={i.toString()} class="cardItem" style={{gridRow: 1, gridColumn: i + 1}}>
            <p class="topicText">{topics[i].text}</p>
            {buttons}
          </div>
        )
      }
    }
    return allDiscussedTopicsElements;
  }

  render() {
    if(this.props.topics.discussedTopics === undefined || this.props.topics.discussedTopics.length === 0) {
      return null;
    }

    let classNames = !this.props.isBacklogOpen
      ? "discussedItemsSection"
      : "discussedItemsSection fullSizeSection"
    return (
      <div class={classNames}>
        {this.getDiscussedCards()}
      </div>
    )
  }
}

export default DiscussedTopics;