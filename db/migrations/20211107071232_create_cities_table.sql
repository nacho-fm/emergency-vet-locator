-- migrate:up
create table cities (
  id uuid default  gen_random_uuid() primary key,
  name varchar(255) unique not null,
  created_at timestamp default now()
);
-- migrate:down
