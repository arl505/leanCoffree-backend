create table discussion_votes (
  session_id varchar(100) not null,
  user_display_name varchar(100) not null,
  vote_type varchar(11) not null,
  primary key (session_id, user_display_name)
);