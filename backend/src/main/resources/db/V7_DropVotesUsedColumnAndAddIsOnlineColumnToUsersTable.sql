alter table users
add column is_online boolean;

alter table users
drop column votes_used;