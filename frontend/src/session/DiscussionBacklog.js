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
  border: solid black 1px;
  width: 15vw;
  height: 15vw;
  position: relative;`;

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

  getTopicCardModeratorButtons(text, author) {
    if(this.props.userDisplayName === this.props.usersInAttendance.moderator && this.props.isUsernameModalOpen === false) {
      return (
        <div>
          <button  onClick={() => this.props.pullNewDiscussionTopic(text, author)}>Discuss</button>
          <button onClick={() => this.props.deleteTopic(text, author)}>Delete</button>
        </div>
      )
    }
    return null;
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

      if(this.props.userDisplayName === this.props.usersInAttendance.moderator && this.props.isUsernameModalOpen === false && this.props.topics.discussionBacklogTopics.length > 1) {
        return topics.length === 0
        ? null
        : <div style={{gridRow: '1 / span 2', width: '20vw', gridColumn: '1',  minHeight: '100vh', maxHeight: '100vh', overflow: 'hidden', borderRight: 'solid black 1px'}}>
            <p style={{marginLeft: '2.5vw', marginRight: '2.5vw'}}>Drag and drop topic cards to reorder the discussion queue</p>
            <DragDropContext onDragEnd={this.onDragEnd}>
              <Droppable droppableId="droppable">
                {(provided, snapshot) => (
                  <div {...provided.droppableProps} class="discussCards-container" style={{paddingBottom: '7.5vw'}} ref={provided.innerRef}>
                    {topics.map((item, index) => (
                      <Draggable key={index.toString()}  draggableId={'draggable' + index.toString()} index={index}>
                        {(provided) => (
                          <Container ref={provided.innerRef} {...provided.draggableProps} {...provided.dragHandleProps}>
                            <p class="topicText">{item.text}</p>
                            <p class="votesText">Votes: {item.votes}</p>
                            <button  onClick={() => this.props.pullNewDiscussionTopic(item.text, item.author)}>Discuss</button>
                            <button onClick={() => this.props.deleteTopic(item.text, item.author)}>Delete</button>
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
          <div class="discussCards-container" style={{gridRow: '1 / span 2', gridColumn: 1, borderRight: 'solid black 1px', minHeight: '100vh', maxHeight: '100vh', overflow: 'scroll'}}>
            {topics.map((item, index) => (
              <div key={index.toString()} class="cardItem discussionCardItem" style={{gridRow: index + 1, marginLeft: '2.5vw', marginRight: '2.5vw'}}>
                <p class="topicText">{item.text}</p>
                <p class="votesText">Votes: {item.votes}</p>
                {this.getTopicCardModeratorButtons(item.text, item.author)}
              </div>
            ))}
          </div>
        );
      }
    }
  }
}

export default DiscussionBacklog;