create table users (
  display_name varchar(20),
  session_id varchar(100),
  primary key (display_name, session_id)
);