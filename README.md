# emergency-vet-locator

Database
-------------

Postgres
# Start Postgres
docker run --rm -d -p 5432:5432 --network=$DOCKERNETWORK -e POSTGRES_PASSWORD=$PGPASSWORD postgres

# Start pgadmin
docker run -p 80:80 -e  'PGADMIN_DEFAULT_PASSWORD=$PGADMIN_DEFAULT_PASSWORD' -e 'PGADMIN_DEFAULT_EMAIL=$PGADMIN_DEFAULT_EMAIL' --name pgadmin4 -d dpage/pgadmin4


Migrations
dbmate
https://github.com/amacneil/dbmate

# Create Database
docker run --rm -it --network=<NETWORK> -e DATABASE_URL="postgres://<USER>:<PASSWORD>@localhost/<DATABASE>?sslmode=disable" amacneil/dbmate create

# Create a migration (named create_vets_table)
docker run -it --network=<NETWORK> -v <DOCKERMIGRATIONSLOCAL>:<DOCKERMIGRATIONSCONTAINER> -e DATABASE_URL="postgres://<PGUSER>:<PGPASSWORD>@<PGHOST>/<PGDATABASE>?sslmode=disable" amacneil/dbmate new create_vets_table

docker run --rm -it --network=host -v <DOCKERMIGRATIONSLOCAL>:<DOCKERMIGRATIONSCONTAINER> -e DATABASE_URL="postgres://<PGUSER>:<PGPASSWORD>@<PGHOST>/<PGDATABASE>?sslmode=disable" amacneil/dbmate up
