import React from 'react';

class DiscussionPage extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      topics: props.topics,
      userDisplayName: props.userInfo.displayName,
    }
  }


  render() {
    return (
      <div>
        Discussion Page
      </div>
    )
  }
}

export default DiscussionPage;