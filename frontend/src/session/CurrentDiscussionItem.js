import React from 'react';

class CurrentDiscussionItem extends React.Component {

  render() {
    let countdown;
    
    if(this.props.currentTopicSecondsRemaining !== -1) {
      let minutesNum = Math.floor(this.props.currentTopicSecondsRemaining / 60);
      let secondsNum = this.props.currentTopicSecondsRemaining % 60;
      if(!isNaN(minutesNum) && !isNaN(secondsNum)) {
        if(secondsNum < 10) {
          secondsNum = ("0" + secondsNum).slice(-2);
        }
        countdown = <h5 class="countdown">{minutesNum} : {secondsNum}</h5>
      }
    }

    let currentDiscussionItemHeader = null;
    let currentDiscussionItem;
    if(this.props.topics.currentDiscussionItem === undefined || this.props.topics.currentDiscussionItem.text === undefined) {
      currentDiscussionItem = "Session completed!";
    } else {
      currentDiscussionItemHeader = "Current discussion item";
      currentDiscussionItem = this.props.topics.currentDiscussionItem.text;
    }

    let classNames = this.props.topics.discussionBacklogTopics === undefined || this.props.topics.discussionBacklogTopics.length <= 0
      ? "currentDiscussionItem fullSizeSection"
      : "currentDiscussionItem";

    return (
      <div class={classNames}>
        <h5 class="currentTopicHeader">{currentDiscussionItemHeader}</h5>
        <h2 class="currentTopicHeader">{currentDiscussionItem}</h2>
        {countdown}
      </div>
    )
  }
}

export default CurrentDiscussionItem;