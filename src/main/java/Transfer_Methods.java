import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import com.google.api.services.youtube.YouTube;


import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Take a Spotify playlist and transfer it over into a Youtube
 * playlist, possibly on a loop.
 */
public class Transfer_Methods {
    private static final String CLIENT_SECRETS= "client_secret.json";
    private static final Collection<String> SCOPES =
            Arrays.asList("https://www.googleapis.com/auth/youtube.force-ssl");

    private static final String APPLICATION_NAME = "API code samples";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    public Transfer_Methods(){}

    /**
     * Log into spotify.
     */
    public void get_Spotify_client() {
    }

    /**
     * Grab desired playlist to transfer.
     *
     * @param playlist_id - name of the playlist.
     * @return String - Song names unparsed.
     */
    public String get_playlist(String playlist_id) throws IOException {
        // System Property for user's Oauth token
        String Oauth_value = System.getProperty("Oauth_token");
        //System.out.println(playlist_id);
        //System.out.println(Oauth_value);

        // java http request to Spotify Web api
        URL url = new URL("https://api.spotify.com/v1/playlists/" + playlist_id + "/tracks?fields=items(track(name))");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        // setting headers
        con.setRequestProperty("Accept", "application-json");
        con.setRequestProperty("Authorization", " Bearer " + Oauth_value);
        con.setRequestProperty("Content-Type", "application-json");

        int status = con.getResponseCode();
        String statusM = con.getResponseMessage();
        System.out.println(status);
        System.out.println(statusM);

        // read in input
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        // close the connection
        con.disconnect();
        return content.toString();

    }

    /**
     * Log into Youtube and give permissions.
     *
     * Derived from Youtube Java Quickstart Docs.
     * @return Youtube - Youtube user auth api client.
     */
    public YouTube get_Youtube_client() throws GeneralSecurityException, IOException {

        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        // Load client secrets.
        InputStream in = Transfer_Methods.class.getResourceAsStream(CLIENT_SECRETS);
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                        .build();
        Credential credential =
                new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");

        return new YouTube.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();

    }

    /**
     * Create a youtube playlist.
     */
    public void create_playlist() {

    }

    /**
     * Search Youtube for song from spotify playlist.
     */
    public void search_song() {

    }

    /**
     * Add song into youtube playlist.git
     */
    public void add_song() {

    }


}