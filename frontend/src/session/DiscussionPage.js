import React from 'react';
import Axios from 'axios';
import { Button, Modal, ModalHeader, ModalBody } from 'reactstrap';
import styled from "styled-components";
import { DragDropContext, Droppable, Draggable } from "react-beautiful-dnd";
import AllUsersList from './AllUsersList';

const Container = styled.div`
grid-column: 1;
margin: 1vw;
margin-left: 2.5vw;
margin-right: 2.5vw;
overflow: scroll;
border: solid black 1px;
width: 15vw;
height: 15vw;
position: relative;`;

class DiscussionPage extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      topics: props.topics,
      userDisplayName: props.userDisplayName,
      votes: props.discussionVotes,
      currentTopicSecondsRemaining: -1,
      finished: false,
      isVotingModalOpen: false,
      moreTimeValue: '1m',
    }
    this.onDragEnd = this.onDragEnd.bind(this);
    this.loadNextTopic = this.loadNextTopic.bind(this);
    this.addTime = this.addTime.bind(this);
    this.endSession = this.endSession.bind(this);
  }

  componentDidMount() {
    if(this.state.topics !== undefined) {
      if(this.state.currentTopicSecondsRemaining === -1 && this.state.topics !== {} && this.state.topics.currentDiscussionItem !== undefined) {
        let endSeconds = Math.round(new Date(this.state.topics.currentDiscussionItem.endTime).getTime() / 1000);
        let nowSeconds = Math.round(new Date().getTime() / 1000);
        this.setState({currentTopicSecondsRemaining: Math.max(0, endSeconds - nowSeconds)})
      }
      
      setInterval(() => {
        if(this.state.topics !== undefined && this.state.topics.currentDiscussionItem !== undefined) {
          let endSeconds = Math.round(new Date(this.state.topics.currentDiscussionItem.endTime).getTime() / 1000);
          let nowSeconds = Math.round(new Date().getTime() / 1000);
          if(Math.max(0, endSeconds - nowSeconds) !== 0) {
            this.setState({currentTopicSecondsRemaining: Math.max(0, endSeconds - nowSeconds)})
          } else if(this.state.finished === false && this.state.isVotingModalOpen !== true) {
            this.setState({isVotingModalOpen: true})
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

  onDragEnd(result) {
    // dropped outside the list
    if (!result.destination) {
      return;
    }

    let topics = this.state.topics.discussionBacklogTopics;

    let topic = topics[result.source.index];
    topics.splice(result.source.index, 1);
    topics.splice(result.destination.index, 0, topic);

    let allTopics = this.state.topics;
    allTopics.discussionBacklogTopics = topics;
    this.setState({topics: allTopics});
    
    Axios.post(process.env.REACT_APP_BACKEND_BASEURL + '/reorder', {sessionId: this.props.sessionId, text: topic.text, newIndex: result.destination.index})
      .then((response) => {
        if(response.data.status !== "SUCCESS") {
          alert(response.data.error);
        }
      })
      .catch((error) => 
        alert("Unable to reorder topic\n" + error)
      );
  }

  deleteTopic(topicText, author) {
    if(window.confirm("Confirm if you'd like to delete the following topic: " + topicText)) {
      Axios.post(process.env.REACT_APP_BACKEND_BASEURL + '/delete-topic', {sessionId: this.props.sessionId, topicText: topicText, authorName: author})
        .then((response) => {
          if(response.data.status !== "SUCCESS") {
            alert(response.data.error);
          }
        })
        .catch((error) => 
          alert("Unable to delete topic\n" + error)
        );
    }
  }

  pullNewDiscussionTopic(text, author) {
    let body;
    let confirmationMessage;
    if(this.state.topics.currentDiscussionItem.text === undefined) {
      body = {command: "REVERT_TO_DISCUSSION", sessionId: this.props.sessionId, nextTopicText: text, nextTopicAuthorDisplayName: author};
      confirmationMessage = "Confirm you'd like to pull the following topic for discussion: " + text;
    } else {
      body = {command: "NEXT", sessionId: this.props.sessionId, currentTopicText: this.state.topics.currentDiscussionItem.text, nextTopicText: text, currentTopicAuthorDisplayName: this.state.topics.currentDiscussionItem.authorDisplayName, nextTopicAuthorDisplayName: author};
      confirmationMessage = "Pulling this topic for discussion will conclude the current discussion topic, proceed?"
    }
    if (window.confirm(confirmationMessage)) {
      Axios.post(process.env.REACT_APP_BACKEND_BASEURL + "/refresh-topics", body)
        .then((response) => {
          if(response.data.status !== "SUCCESS") {
            alert(response.data.error);
          }
        })
        .catch((error) => 
          alert("Unable to pull topic for discussion\n" + error)
        );
    } 
  }

  getAllTopicCards() {
    let allTopics = this.state.topics.discussionBacklogTopics;
    if(allTopics !== undefined) {
      let topics = [];
      for(let i = 0; i < allTopics.length; i++) {
        let text = allTopics[i].text;
        let votes = allTopics[i].voters.length;
        let author = allTopics[i].authorDisplayName
        topics.push({votes: votes, text: text, author: author});
      }

      if(this.props.userDisplayName === this.props.usersInAttendance.moderator && this.props.isUsernameModalOpen === false && this.state.topics.discussionBacklogTopics.length > 1) {
        return topics.length === 0
        ? null
        : <div style={{gridRow: '1 / span 2', width: '20vw', gridColumn: 1, borderRight: 'solid black 1px', minHeight: '100vh', maxHeight: '100vh', overflow: 'hidden'}}>
            <p style={{marginLeft: '2.5vw', marginRight: '2.5vw'}}>Drag and drop topic cards to reorder the discussion queue</p>
            <DragDropContext onDragEnd={this.onDragEnd}>
              <Droppable droppableId="droppable">
                {(provided, snapshot) => (
                  <div {...provided.droppableProps} class="discussCards-container" style={{paddingBottom: '7.5vw'}} ref={provided.innerRef}>
                    {topics.map((item, index) => (
                      <Draggable key={index.toString()}  draggableId={'draggable' + index} index={index}>
                        {(provided) => (
                          <Container ref={provided.innerRef} {...provided.draggableProps} {...provided.dragHandleProps}>
                            <p class="topicText">{item.text}</p>
                            <p class="votesText">Votes: {item.votes}</p>
                            <button  onClick={() => this.pullNewDiscussionTopic(item.text, item.author)}>Discuss</button>
                            <button onClick={() => this.deleteTopic(item.text, item.author)}>Delete</button>
                          </Container>
                        )}
                      </Draggable>
                    ))}
                    {provided.placeholder}
                  </div>
                )}
              </Droppable>
            </DragDropContext>
          </div>;
      } else if(this.props.userDisplayName === this.props.usersInAttendance.moderator && this.props.isUsernameModalOpen === false) {
        return topics.length === 0
        ? null
        : (
          <div class="discussCards-container" style={{gridRow: '1 / span 2', gridColumn: 1, borderRight: 'solid black 1px', minHeight: '100vh', maxHeight: '100vh', overflow: 'scroll'}}>
            {topics.map((item, index) => (
              <div key={index.toString()} class="cardItem discussionCardItem" style={{gridRow: index + 1, marginLeft: '2.5vw', marginRight: '2.5vw'}}>
                <p class="topicText">{item.text}</p>
                <p class="votesText">Votes: {item.votes}</p>
                <button  onClick={() => this.pullNewDiscussionTopic(item.text, item.author)}>Discuss</button>
                <button onClick={() => this.deleteTopic(item.text, item.author)}>Delete</button>
              </div>
            ))}
          </div>
        );
      } else {
        return topics.length === 0
        ? null
        : (
          <div class="discussCards-container" style={{gridRow: '1 / span 2', gridColumn: 1, borderRight: 'solid black 1px', minHeight: '100vh', maxHeight: '100vh', overflow: 'scroll'}}>
            {topics.map((item, index) => (
              <div key={index.toString()} class="cardItem discussionCardItem" style={{gridRow: index + 1, marginLeft: '2.5vw', marginRight: '2.5vw'}}>
                <p class="topicText">{item.text}</p>
                <p class="votesText">Votes: {item.votes}</p>
              </div>
            ))}
          </div>
        );
      }
    }
    return null;
  }

  getDiscussedCards(isFinished) {
    if(this.state.topics.discussedTopics !== undefined && this.state.topics.discussedTopics.length !== 0) {
      let topics = this.state.topics.discussedTopics;
      let allDiscussedTopicsElements = [];
      for(let i = 0; i <= topics.length; i++) {
        let buttons = this.props.userDisplayName === this.props.usersInAttendance.moderator && this.props.isUsernameModalOpen === false
          ? <div>
              <button  onClick={() => this.pullNewDiscussionTopic(topics[i].text, topics[i].authorDisplayName)}>Discuss</button>
              <button onClick={() => this.deleteTopic(topics[i].text, topics[i].authorDisplayName)}>Delete</button>
            </div>
          : null;
        if(i === (topics.length)) {
          allDiscussedTopicsElements.push(
            <div key={i.toString()} class="finalSpacer row1" style={{gridColumn: i + 1}}/>
          )
        } else {
          allDiscussedTopicsElements.push(
            <div key={i.toString()} class="cardItem row1" style={{gridColumn: i + 1}}>
              <p class="topicText">{topics[i].text}</p>
              {buttons}
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

  loadNextTopic() {
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
        } else {
          this.setState({isVotingModalOpen: false})
        }
      })
      .catch((error) => 
        alert("Unable to refresh topics\n" + error)
      );
  }

  castVote(voteType) {
    Axios.post(process.env.REACT_APP_BACKEND_BASEURL + "/discussion-vote", {voteType: voteType, sessionId: this.props.sessionId, userDisplayName: this.props.userDisplayName})
      .then((response) => {
        if(response.data.status !== "SUCCESS") {
          alert(JSON.stringify(response.data));
        }
      })
      .catch((error) => 
        alert("Unable to refresh topics\n" + error)
      );
  }

  addTime() {
    let increment = this.state.moreTimeValue.toUpperCase();
    let suffix = increment[increment.length - 1]
    increment = increment.slice(0, increment.length - 1);
    increment = suffix + increment;
    Axios.post(process.env.REACT_APP_BACKEND_BASEURL + '/add-time', {increment: increment, sessionId: this.props.sessionId})
      .then((response) => {
        if(response.data.status !== "SUCCESS") {
          alert(JSON.stringify(response.data));
        } else {
          this.setState({isVotingModalOpen: false})
        }
      })
      .catch((error) => 
        alert("Unable to refresh topics\n" + error)
      );
  }

  endSession() {
    if(window.confirm("Confirm you'd like to end session. All session data will be immediately deleted")) {
      Axios.post(process.env.REACT_APP_BACKEND_BASEURL + '/end-session/' + this.props.sessionId, {})
        .then((response) => {
          if(response.data.status !== "SUCCESS") {
            alert(response.data.error);
          } else {
            return window.location = process.env.REACT_APP_FRONTEND_BASEURL;
          }
        })
        .catch((error) => 
          alert("Unable to end session\n" + error)
        );
    }
  }

  getButtons() {
    if(this.props.userDisplayName !== this.props.usersInAttendance.moderator || this.props.isUsernameModalOpen !== false) {
      return null;
    }

    let finishTopicButton = this.state.topics.currentDiscussionItem !== undefined && this.state.topics.currentDiscussionItem.text !== undefined
      ? <button onClick={() => {if(window.confirm("Confirm you'd like to finish the current topic below\n" + this.state.topics.currentDiscussionItem.text)) this.loadNextTopic()}}>Finish Topic</button>
      : null;

    return (
      <div style={{gridColumn: 3, gridRow: 2, position: 'relative'}}>
        <div style={{textAlign: 'center', position: 'absolute', bottom: 0, left: 0, right: 0}}>
          {finishTopicButton}
          <button style={{marginTop: '1vh', marginBottom: '1vh'}} onClick={this.endSession}>End Session</button>
        </div>
      </div>
    );
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

    let allTopicCardsContainer = this.getAllTopicCards();

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

    let modalFooter = this.props.userDisplayName === this.props.usersInAttendance.moderator && this.props.isUsernameModalOpen === false
      ? (
        <div>
          <br/>
          <hr/>
          <p style={{textAlign: 'center'}}><b>Moderator final say</b></p>
          <div style={{textAlign: 'center'}}>
            <select value={this.state.moreTimeValue} onChange={(event) => this.setState({moreTimeValue: event.target.value})}>
              <option value="30s">30s</option>
              <option selected="selected" value="1m">1m</option>
              <option value="3m">3m</option>
              <option value="5m">5m</option>
              <option value="10m">10m</option>
              <option value="15m">15m</option>
              <option value="30m">30m</option>
              <option value="1h">1h</option>
            </select>
            <text> </text>
            <Button color="success" onClick={this.addTime}>Add {this.state.moreTimeValue} More time</Button>
            <text> </text>
            <Button color="primary" onClick={this.loadNextTopic}>Finish Topic</Button>
          </div>
        </div>
      )
      : null;

  let moreTimeVoteCount = this.props.discussionVotes.moreTimeVotesCount === undefined
      ? 0
      : this.props.discussionVotes.moreTimeVotesCount;

    let finishTopicVoteCount = this.props.discussionVotes.finishTopicVotesCount === undefined
    ? 0
    : this.props.discussionVotes.finishTopicVotesCount;

    let votingModal = (
      <Modal isOpen={this.state.currentTopicSecondsRemaining < 2 && this.props.isUsernameModalOpen === false && this.state.isVotingModalOpen} toggle={this.props.toggle}>
          <ModalHeader>Vote: More Time or Finish Topic</ModalHeader>
          <ModalBody>
            <div style={{marginBottom: '5vh'}}>
              More Time Votes: {moreTimeVoteCount}
              <Button style={{display: 'inline-block', float: 'right'}} color="success" onClick={() => this.castVote('MORE_TIME')}>More time</Button>
            </div>
            <div>
              Finish Topic Votes: {finishTopicVoteCount}
              <Button style={{display: 'inline-block', float: 'right'}} color="primary" onClick={() => this.castVote('FINISH_TOPIC')}>Finish Topic</Button>
            </div>
            {modalFooter}
          </ModalBody>
        </Modal>
      );

    let sessionControlButtons = this.getButtons();
      
    return (
      <div class="session-grid-container">
        {votingModal}
        {allTopicCardsContainer}
        {currentDiscussionItemContainer}
        {this.getDiscussedCards(allTopicCardsContainer === null)}

        <div class="session-grid-item usersSection column3">
          <div>All here:</div>
          <div>
            <AllUsersList usersInAttendance={this.props.usersInAttendance} userDisplayName={this.props.userDisplayName} toggleShareableLink={this.props.toggleShareableLink}/>
          </div>
        </div>
        {sessionControlButtons}
      </div>
    )
  }
}

export default DiscussionPage;