import java.io.IOException;

/**
 * Take a Spotify playlist and transfer it over into a Youtube
 * playlist, possibly on a loop.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        Transfer_Methods test = new Transfer_Methods();
        String trackNames = test.get_playlist(System.getProperty("playlist_id"));

        System.out.println(trackNames);

    }
}
