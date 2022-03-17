package nacho.fm.evl.extractor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.*;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class GoogleSheetSource {
    private static class City {
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Vet> getVets() {
            return vets;
        }

        public void setVets(List<Vet> vets) {
            this.vets = vets;
        }

        private String name;
        private List<Vet> vets = new ArrayList<>();

        private static City create() { return new City(); }
        private City name(String name) { this.name = name; return this; }
        private City vets(List<Vet> vets) { this.vets = vets; return this; }
        private City vet(Vet vet) { this.vets.add(vet); return this; }

        @Override
        public String toString() {
            return name + " ===> " + vets.toString();
        }
    }

    private static class Vet {
        private String name;
        private Date date;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public String getWaitStatus() {
            return waitStatus;
        }

        public void setWaitStatus(String waitStatus) {
            this.waitStatus = waitStatus;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        private String waitStatus;
        private String phone;
        private String address;

        private static Vet create() { return new Vet(); }

        private Vet name(String name) { this.name = name; return this; }
        private Vet date(Date date) { this.date = date; return this; }
        private Vet waitStatus(String waitStatus) { this.waitStatus = waitStatus; return this; }
        private Vet phone(String phone) { this.phone = phone; return this; }
        private Vet address(String address) { this.address = address; return this; }

        @Override
        public String toString() {
            return name + " | " + waitStatus + " | " + phone + " | " + address;
        }
    }
    private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = GoogleSheetSource.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public static void main(String... args) throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        final String spreadsheetId = "10SVXRatfiB4upYUrRqhRTFz9l1eIR54s2PmviHzN-Ow";
        final String range = "Sheet1!A3:E";
        final Set<String> knownCities =
                new HashSet<>(Arrays.asList("POULSBO/SILVERDALE", "GIG HARBOR", "LAKEWOOD/TACOMA", "OLYMPIA/LACEY",
                        "KIRKLAND", "SEATTLE/SHORELINE", "RENTON", "LYNNWOOD", "AUBURN", "BELLEVUE",
                        "REDMOND", "SNOHOMISH", "SUMNER", "EVERETT", "MOUNT VERNON", "BOTHELL",
                        "VANCOUVER", "YAKIMA", "PASCO", "BELLINGHAM"));
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy", Locale.ENGLISH);
        System.out.println("Starting extraction...");
        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
        ValueRange response = service.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();
        List<List<Object>> allVets = response.getValues();

        if (allVets == null || allVets.isEmpty()) {
            System.out.println("No data found.");
        } else {
            Map<String, City> vetPOJOs = new HashMap<>();
            vetPOJOs.put("", City.create());
            Optional<String> currentCity = Optional.empty();
            for (List<Object> vetData : allVets) {
                if (vetData.size() > 0 && (vetData.size() == 1 || knownCities.contains(vetData.get(0)))) {
                    currentCity = Optional.of(vetData.get(0).toString().trim());
                    vetPOJOs.put(vetData.get(0).toString().trim(), City.create().name(vetData.get(0).toString().trim()));
                }
                else {
                    Vet newVet = Vet.create();
                    try {
                        newVet.name(vetData.get(0).toString().trim())
                              .waitStatus(vetData.get(2).toString().trim())
                              .phone(vetData.get(3).toString().trim())
                              .address(vetData.get(4).toString().trim())
                              .date(formatter.parse(vetData.get(1).toString().trim()));
                        vetPOJOs.get(currentCity.orElse("")).vet(newVet);
                    }
                    catch (NullPointerException | IndexOutOfBoundsException exc) {
                        exc.printStackTrace();
                        vetPOJOs.get(currentCity.orElse("")).vet(newVet);
                    }
                    catch (ParseException e) {
                        System.err.println("Vet date parsing failed for " + newVet.name);
                        System.err.println("Failed date string: " + vetData.get(1).toString());
                        vetPOJOs.get(currentCity.orElse("")).vet(newVet);
                    }
                }
            }
            ObjectMapper jsonMapper = new ObjectMapper();
            System.out.println(jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(vetPOJOs.values()));
        }
    }
}
