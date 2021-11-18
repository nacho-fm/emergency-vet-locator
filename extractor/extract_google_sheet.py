# https://docs.google.com/spreadsheets/d/10SVXRatfiB4upYUrRqhRTFz9l1eIR54s2PmviHzN-Ow/edit#gid=0
from __future__ import print_function
import os.path
from googleapiclient.discovery import build
from google_auth_oauthlib.flow import InstalledAppFlow
from google.auth.transport.requests import Request
from google.oauth2.credentials import Credentials

# If modifying these scopes, delete the file token.json.
SCOPES = ['https://www.googleapis.com/auth/spreadsheets.readonly']

SPREADSHEET_ID = '10SVXRatfiB4upYUrRqhRTFz9l1eIR54s2PmviHzN-Ow'
SAMPLE_RANGE_NAME = 'Sheet1!A3:E'


def extract_from_google_sheet():
    creds = None
    # The file token.json stores the user's access and refresh tokens, and is
    # created automatically when the authorization flow completes for the first
    # time.
    if os.path.exists('token.json'):
        creds = Credentials.from_authorized_user_file('token.json', SCOPES)
    # If there are no (valid) credentials available, let the user log in.
    if not creds or not creds.valid:
        if creds and creds.expired and creds.refresh_token:
            creds.refresh(Request())
        else:
            flow = InstalledAppFlow.from_client_secrets_file(
                'credentials.json', SCOPES)
            creds = flow.run_local_server(port=0)
        # Save the credentials for the next run
        with open('token.json', 'w') as token:
            token.write(creds.to_json())

    service = build('sheets', 'v4', credentials=creds)

    # Call the Sheets API
    sheet = service.spreadsheets()
    result = sheet.values().get(spreadsheetId=SPREADSHEET_ID,
                                range=SAMPLE_RANGE_NAME).execute()
    values = result.get('values', [])

    if not values:
        print('No data found.')
    else:
        vets = []
        current_city_idx = -1
        for row in values:
            if len(row) < 5 or not (row[1] or row[2] or row[3] or row[4]):
                vets.append({"City": str.strip(row[0]), "Vets": []})
                current_city_idx += 1
            else:
                vets[current_city_idx]["Vets"].append(
                    {"Vet": str.strip(row[0]),
                     "Date": str.strip(row[1]),
                     "Wait": str.strip(row[2]),
                     "Phone": str.strip(row[3]),
                     "Address": str.strip(row[4])})
        return vets
