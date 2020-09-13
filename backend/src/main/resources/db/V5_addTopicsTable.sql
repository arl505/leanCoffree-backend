create table topics (
  session_id varchar(100),
  topic_text varchar(500),
  created_timestamp datetime,
  primary key (session_id, topic_text),
  foreign key (session_id) references sessions(id)
);