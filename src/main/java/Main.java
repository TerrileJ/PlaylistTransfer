import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Take a Spotify playlist and transfer it over into a Youtube
 * playlist, possibly on a loop.
 *
 */
public class Main {
    public static void main(String[] args) throws IOException, GeneralSecurityException {
        Transfer_Methods test = new Transfer_Methods();
        test.get_playlist(System.getProperty("playlist_id"));
        // System.out.println(trackNames);

        //test.create_playlist();
    }
}
