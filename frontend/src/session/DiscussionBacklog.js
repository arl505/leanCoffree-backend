import React from 'react';
import Axios from 'axios';
import { DragDropContext, Droppable, Draggable } from "react-beautiful-dnd";
import styled from "styled-components";

const Container = styled.div`
  grid-column: 1;
  margin: 1vw;
  margin-left: 2.5vw;
  margin-right: 2.5vw;
  overflow: scroll;
  border: solid #ececec 1px;
  position: relative;
  border-radius: 10px;
  background-color: #30475e;
  @media (min-width: 1100px) {
    width: 15vw;
    height: 15vw;
  }
  @media (max-width: 1099px) and (min-width: 900px) {
    width: 18.75vw;
    height: 18.75vw;
  }
  @media (max-width: 899px) {
    width: 25vw;
    height: 25vw;
  }`;

  class DiscussionBacklog extends React.Component {

    constructor(props) {
      super(props);
      this.onDragEnd = this.onDragEnd.bind(this);
    }

    onDragEnd(result) {
      // dropped outside the list
      if (!result.destination) {
        return;
      }
  
      let topics = this.props.topics.discussionBacklogTopics;
  
      let topic = topics[result.source.index];
      topics.splice(result.source.index, 1);
      topics.splice(result.destination.index, 0, topic);
  
      let allTopics = this.props.topics;
      allTopics.discussionBacklogTopics = topics;
      this.props.setTopics(allTopics);
      
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

  getBottomDiv(votes, text, author) {
    if(this.props.usersInAttendance.moderator.includes(this.props.userDisplayName) && this.props.isUsernameModalOpen === false) {
      return (
        <div class="backlogBottomDiv">
          <p class="votesText" style={{margin: 0, gridRow: 1, gridColumn: 1}}>Votes: {votes}</p>
          <button class="button" style={{gridRow: 1, gridColumn: 2}} onClick={() => this.props.pullNewDiscussionTopic(text, author)}>Discuss</button>
          <button class="button" style={{gridRow: 1, gridColumn: 3}} onClick={() => this.props.deleteTopic(text, author)}>Delete</button>
        </div>
      )
    }
    return (
    <div class="backlogBottomDiv">
      <p class="votesText" style={{margin: 0}}>Votes: {votes}</p>
    </div>);
  }

  render() {
    let allTopics = this.props.topics.discussionBacklogTopics;
    if(allTopics !== undefined) {
      let topics = [];
      for(let i = 0; i < allTopics.length; i++) {
        let text = allTopics[i].text;
        let votes = allTopics[i].voters.length;
        let author = allTopics[i].authorDisplayName
        topics.push({votes: votes, text: text, author: author});
      }

      let columnSize, cardSize;
      if(window.innerWidth > 1100) {
        cardSize = '15vw';
        columnSize = '20vw'
      }
      else if(window.innerWidth > 900) {
        cardSize = '18.75vw';
        columnSize = '23.75vw'
      }
      else if(window.innerWidth > 652) {
        cardSize = '25vw';
        columnSize = '30vw'
      } else {
        return <div>ayo</div>
      }

      if(this.props.usersInAttendance.moderator.includes(this.props.userDisplayName) && this.props.isUsernameModalOpen === false && this.props.topics.discussionBacklogTopics.length > 1) {
        return topics.length === 0
        ? null
        : <div class="dragAndDropWrapper" style={{width: columnSize}}>
            <p class="dragAndDropHeader">Drag and drop topic cards to reorder the discussion queue</p>
            <DragDropContext onDragEnd={this.onDragEnd}>
              <Droppable droppableId="droppable">
                {(provided, snapshot) => (
                  <div {...provided.droppableProps} class="discussCards-container" style={{width: columnSize, paddingBottom: '7.5vw'}} ref={provided.innerRef}>
                    {topics.map((item, index) => (
                      <Draggable key={index.toString()} draggableId={'draggable' + index.toString()} index={index}>
                        {(provided) => (
                          <Container ref={provided.innerRef} {...provided.draggableProps} {...provided.dragHandleProps}>
                            <p class="cardItemTopicText" style={{height: '75%', backgroundColor: '#233145'}}>{item.text}</p>
                            {this.getBottomDiv(item.votes, item.text, item.author)}
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
      } else {
        return topics.length === 0
        ? null
        : (
          <div class="discussCards-container" style={{borderRadius: '0 30px 30px 0', borderRight: 'solid #ececec 1px', width: columnSize}}>
            {topics.map((item, index) => (
              <div key={index.toString()} class="cardItem" style={{backgroundColor: '#233145', width: cardSize, height: cardSize, gridRow: index + 1, marginLeft: '2.5vw', marginRight: '2.5vw'}}>
                <p class="cardItemTopicText" style={{height: "75%", backgroundColor: '#233145'}}>{item.text}</p>
                {this.getBottomDiv(item.votes, item.text, item.author)}
              </div>
            ))}
          </div>
        );
      }
    }
    else {
      return <div></div>
    }
  }
}

export default DiscussionBacklog;