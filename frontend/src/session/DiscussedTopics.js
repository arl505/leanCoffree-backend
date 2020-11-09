import React from 'react';

class DiscussedTopics extends React.Component {

  getDiscussedCards() {
    let cardSize;
    if(window.innerWidth > 1100) {
      cardSize = '15vw';
    }
    else if(window.innerWidth > 900) {
      cardSize = '18.75vw';
    }
    else if(window.innerWidth > 652) {
      cardSize = '25vw';
    } else {
      return <div>ayo</div>
    }

    let topics = this.props.topics.discussedTopics;
    let allDiscussedTopicsElements = [];
    for(let i = 0; i <= topics.length; i++) {
      let buttons = this.props.usersInAttendance.moderator.includes(this.props.userDisplayName) && this.props.isUsernameModalOpen === false
        ? <div style={{backgroundColor: '#30475e', height: '25%', display: 'grid', alignItems: 'center'}}>
            <button class="button" style={{gridRow: 1, gridColumn: 2}} onClick={() => this.props.pullNewDiscussionTopic(topics[i].text, topics[i].authorDisplayName)}>Discuss</button>
            <button class="button" style={{gridRow: 1, gridColumn: 3}} onClick={() => this.props.deleteTopic(topics[i].text, topics[i].authorDisplayName)}>Delete</button>
          </div>
        : null;
      if(i === (topics.length)) {
        allDiscussedTopicsElements.push(<div key={i.toString()} style={{gridRow: 1, gridColumn: i + 1, width: '.01vw'}}/>)
      } else {
        let topicTextHeight = buttons === null 
          ? '100%'
          : '75%';
        allDiscussedTopicsElements.push(
          <div key={i.toString()} class="cardItem" style={{backgroundColor: '#2b2f36', width: cardSize, height: cardSize, gridRow: 1, gridColumn: i + 1}}>
            <p class="cardItemTopicText" style={{height: topicTextHeight, marginBottom: 0, backgroundColor: '#2b2f36'}}>{topics[i].text}</p>
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