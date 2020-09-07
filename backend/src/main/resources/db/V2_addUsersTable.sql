create table users (
  display_name varchar(20),
  session_id varchar(100),
  votes_used TINYINT,
  primary key (display_name, session_id)
);