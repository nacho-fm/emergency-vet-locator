# https://docs.google.com/spreadsheets/d/10SVXRatfiB4upYUrRqhRTFz9l1eIR54s2PmviHzN-Ow/edit#gid=0
import db.api as db_api
import extractor.extract_google_sheet as egs
import json
from kafka import KafkaProducer

if __name__ == '__main__':
    extracted_google_sheet_json = json.dumps(egs.extract_from_google_sheet(), indent=2)
    print(extracted_google_sheet_json)

    # Fill these in...
    # TODO: Make this not awful.
    db_api.ingest_json_to_postgres(extracted_google_sheet_json, '#DBNAME#', '#USERNAME#', '#PASSWORD#', 'localhost', 5432)

    # TODO: Message Queue
    # producer = KafkaProducer(value_serializer=lambda v: json.dumps(v).encode('utf-8'))
    # producer.send('foobar', egs.extract_from_google_sheet())
