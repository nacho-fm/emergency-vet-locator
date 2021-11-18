-- migrate:up
create table vet_status (
  id uuid default gen_random_uuid() primary key,
  vet uuid references vets(id),
  last_updated timestamp,
  status varchar(255),
  created_at timestamp default now()
)
-- migrate:down
