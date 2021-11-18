import json
from db import postgres as pg


def ingest_json_to_postgres(json_data, db_name, db_user, db_password, db_host, db_port):
    connection = pg.create_connection(db_name, db_user, db_password, db_host, db_port)
    cursor = connection.cursor()

    ingest_data = json.loads(json_data)
    for ingest_datum in ingest_data:
        cursor.execute("INSERT INTO cities (name) VALUES (%s);", (ingest_datum['City'], ))
        for vet in ingest_datum['Vets']:
            cursor.execute("WITH vet_id AS (WITH city_id AS (SELECT id FROM cities where cities.name = (%s)) INSERT INTO vets (name, phone, address, city) VALUES (%s, %s, %s, city_id) RETURNING id) INSERT INTO vet_status (vet, status) VALUES (vet_id, %s)",
                           (ingest_datum['City'], vet['Vet'], vet['Phone'], vet['Address'], vet['Wait']))

    connection.commit()

    print(cursor.rowcount)
