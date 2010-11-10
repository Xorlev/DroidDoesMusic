package util;


/**
 * A class to interface with Android.MediaStore.Playlists
 * and and make easy ways for us to work with playlists.
 * 
 * It is a singleton class because we should only need one instance.
 */



public class PlaylistHandler {
	private static PlaylistHandler instance = null;

	
	/**
	 * Protected constructor due to singleton design principle.
	 */
	protected PlaylistHandler(){
		
	}
	
	/**
	 * Returns the instance of the Playlist Handler
	 * if one does not exist yet it creates one.
	 * @return PlaylistHandler
	 */
	public static PlaylistHandler getPlaylistHandler(){
		if (instance == null)
			instance = new PlaylistHandler();
		return instance;
	}
}
