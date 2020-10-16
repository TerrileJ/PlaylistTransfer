# PlaylistTransfer
Takes a public Spotify playlist and transfers it over to Youtube. An alternative approach for those without spotify premium, allowing them to relisten to a few songs, pick and choose what to play, etc.

# Install
Install latest version of JSON at https://mvnrepository.com/artifact/org.json/json. Add to classpath and update version dependency in build.gradle file if needed. 

# Setup 
Spotify Oauth - Go to https://developer.spotify.com/console/get-playlist-tracks/?playlist_id=21THa8j9TaSGuXYNBU5tsC&user_id=spotify_espa%C3%B1a to get a spotify Oauth token with the default scopes. 

Spotify playlistID = Login at https://open.spotify.com/, navigate to desired playlist, and copy down ID at end of the url. 

Youtube - Follow instructions for Step 1, Step 2 #4  at https://developers.google.com/youtube/v3/quickstart/java. Move client_secret_CLIENTID.json file to a new directory src/main/resources. 

Warning: Youtube uses a quota system to keep track of its usage, so if you go over the free limit for its applications, you may be charged. Be sure to check on your quota usage before use. 

# API Reference 
https://developer.spotify.com/documentation/web-api/reference/playlists/get-playlist/

https://developers.google.com/youtube/v3/docs

# How to use 
Run command gradle -Dplaylist_id="Your Spotify playlist ID" -DOauth_token="Your Spotify oauth token" -q run. Follow any prompts and check for success. 
