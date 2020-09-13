alter table sessions
add primary key (id);

alter table users
add foreign key (session_id) references sessions(id);