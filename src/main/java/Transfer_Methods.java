import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Take a Spotify playlist and transfer it over into a Youtube
 * playlist, possibly on a loop.
 */
public class Transfer_Methods {
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
        System.out.print(status);
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
     * Log into Youtube.
     */
    public void get_Youtube_client() {

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