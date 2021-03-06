import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;



/**
 * Take a Spotify playlist and transfer it over into a Youtube
 * playlist, possibly on a loop.
 */
public class Transfer_Methods {
    private static final String CLIENT_SECRETS= "client_secret.json";
    private static final Collection<String> SCOPES =
            Arrays.asList("https://www.googleapis.com/auth/youtube.force-ssl");

    private static final String APPLICATION_NAME = "PlaylistTransfer";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    public Transfer_Methods(){}

    /**
     * Transfers a spotify playlist into Youtube.
     *
     * @param SP_playlist_id - id of spotify playlist to take songs from
     * @return void.
     */
    public void transfer_playlist(String SP_playlist_id) throws GeneralSecurityException, IOException {
        // build youtube client
        YouTube youtubeService = get_Youtube_client();

        // create empty youtube playlist and get ID
        String YT_playlist_id = create_playlist(youtubeService);

        // get Spotify track names
        ArrayList<String> tracks = get_playlist(SP_playlist_id);
        System.out.println(tracks);


        for(String track: tracks){
            // searches for song and adds top result to playlist
            String songID = search_song(youtubeService, track);
            add_song(youtubeService, YT_playlist_id, songID);
        }



    }

    /**
     * Grab desired playlist to transfer.
     *
     * @param playlist_id - name of the playlist.
     * @return ArrayList - List of song names.
     */
    public ArrayList<String> get_playlist(String playlist_id) throws IOException {
        // System Property for user's Oauth token
        String Oauth_value = System.getProperty("Oauth_token");
        //System.out.println(playlist_id);
        //System.out.println(Oauth_value);

        // java http request to Spotify Web api
        URL url = new URL("https://api.spotify.com/v1/playlists/" + playlist_id + "/tracks?fields=items(track(name))");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        //System.out.println(url);

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

        // parse json
        JSONObject response = new JSONObject(content.toString());
        JSONArray trackItems = (JSONArray) response.get("items");

        ArrayList<String> list = new ArrayList<String>();
        for(int i = 0; i < trackItems.length(); i++){
            list.add(trackItems.getJSONObject(i).getJSONObject("track").getString("name"));
        }

        //System.out.println(list);
        return list;


    }

    /**
     * Log into Youtube and give permissions.
     * Derived from Youtube Java Quickstart Docs.
     *
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
     *
     * Taken from Youtube Data Api.
     *
     * @param youtubeService - Youtube client.
     * @return String - Youtube playlist id.
     */
    public String create_playlist(YouTube youtubeService) throws GeneralSecurityException, IOException {
        // Define the Playlist object, which will be uploaded as the request body.
        Playlist playlist = new Playlist();

        // Add the snippet object property to the Playlist object.
        PlaylistSnippet snippet = new PlaylistSnippet();
        snippet.setDefaultLanguage("en");
        snippet.setDescription("Playlist taken from user's spotify");
        String[] tags = {
                "sample playlist",
                "API call",
        };
        snippet.setTags(Arrays.asList(tags));
        snippet.setTitle("Spotify Playlist");
        playlist.setSnippet(snippet);

        // Add the status object property to the Playlist object.
        PlaylistStatus status = new PlaylistStatus();
        status.setPrivacyStatus("private");
        playlist.setStatus(status);

        // Define and execute the API request
        YouTube.Playlists.Insert request = youtubeService.playlists()
                .insert("snippet,status", playlist);
        Playlist response = request.execute();
        System.out.println(response);

        // parse JSON response for playlist id
        JSONObject playlistResponse = new JSONObject(response.toString());
        return playlistResponse.getString("id");
    }

    /**
     * Search Youtube for song from spotify playlist to get
     * video/resource ID.
     *
     * @param youtubeService - Youtube client.
     * @param songName - song to be searched for.
     * @return String - videoID for song on Youtube.
     */
    public String search_song(YouTube youtubeService, String songName) throws GeneralSecurityException, IOException, GoogleJsonResponseException {
        // Define and execute the API request for each track
        YouTube.Search.List request = youtubeService.search()
                .list("snippet");
        SearchListResponse response = request.setMaxResults(1L)
                .setQ(songName)
                .setType("video")
                .execute();

        // parse json response
        JSONObject YoutubeSearch = new JSONObject(response.toString());
        JSONArray searchResults = (JSONArray) YoutubeSearch.get("items");

        // video id for first song result
        String videoID = searchResults.getJSONObject(0).getJSONObject("id").getString("videoId");

        return videoID;

    }

    /**
     * Add song into youtube playlist.
     *
     * @param youtubeService - Youtube client.
     * @param youtubePlayID - id of playlist to add song to.
     * @param videoID - id of video to be added.
     * @return void
     */
    public void add_song(YouTube youtubeService, String youtubePlayID, String videoID) throws GeneralSecurityException, IOException, GoogleJsonResponseException {
        // Define the PlaylistItem object, which will be uploaded as the request body.
        PlaylistItem playlistItem = new PlaylistItem();

        // Add the snippet object property to the PlaylistItem object.
        PlaylistItemSnippet snippet = new PlaylistItemSnippet();
        snippet.setPlaylistId(youtubePlayID);
        snippet.setPosition(0L);
        ResourceId resourceId = new ResourceId();
        resourceId.setKind("youtube#video");
        resourceId.setVideoId(videoID);
        snippet.setResourceId(resourceId);
        playlistItem.setSnippet(snippet);

        // Define and execute the API request
        YouTube.PlaylistItems.Insert request = youtubeService.playlistItems()
                .insert("snippet", playlistItem);
        PlaylistItem response = request.execute();

    }


}