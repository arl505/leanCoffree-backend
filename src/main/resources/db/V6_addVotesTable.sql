create table votes (
  display_name varchar(20),
  session_id varchar(100),
  topic_text varchar(500),
  primary key (display_name, session_id, topic_text),
  foreign key (display_name) references users(display_name),
  foreign key (session_id, topic_text) references topics(session_id, topic_text)
);