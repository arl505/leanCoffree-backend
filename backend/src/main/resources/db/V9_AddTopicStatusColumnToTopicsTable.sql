drop table votes;
drop table topics;
drop table users;
drop table sessions;

create table sessions (
  id varchar(100),
  status varchar(100) not null,
  created_timestamp datetime not null,
  updated_timestamp datetime not null,
  current_topic_end_time timestamp null default null,
  primary key (id)
);

create table users (
  session_id varchar(100),
  display_name varchar(100),
  websocket_user_id varchar(100),
  is_online boolean not null,
  primary key (session_id, display_name),
  foreign key (session_id) references sessions(id)
);

create table topics (
  text varchar(500),
  display_name varchar(100),
  session_id varchar(100),
  created_timestamp datetime not null,
  status varchar(20) not null,
  primary key (text, display_name, session_id),
  foreign key (session_id, display_name) references users(session_id, display_name)
);

create table votes (
  id int unsigned auto_increment,
  topic_text varchar(500),
  voter_session_id varchar(100),
  voter_display_name varchar(100),
  topic_author_session_id varchar(100),
  topic_author_display_name varchar(100),
  primary key (id),
  foreign key (topic_text, topic_author_display_name, topic_author_session_id) references topics(text, display_name, session_id),
  foreign key (voter_session_id, voter_display_name) references users(session_id, display_name)
);