-- migrate:up
create table vets (
  id uuid default gen_random_uuid() primary key,
  name varchar(255),
  phone varchar(255),
  address varchar(255),
  city uuid references cities(id),
  created_at timestamp default now()
);
-- migrate:down
