package db;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import entity.Album;
import entity.Playlist;
import entity.Song;

public class DatabaseOperations {
	private static final String PERSISTENCE_UNIT_NAME = "forum";	
	private static EntityManager entityMgrObj = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME).createEntityManager();
	private static EntityTransaction transactionObj = entityMgrObj.getTransaction();
	
	//Select All Objects
	
	@SuppressWarnings("unchecked")
	public static List<Playlist> getAllPlaylistsDetails() {
		Query queryObj = entityMgrObj.createQuery("SELECT s FROM Playlist s");
		List<Playlist> playlistsList = queryObj.getResultList();
		if (playlistsList != null && playlistsList.size() > 0) {			
			return playlistsList;
		} else {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static List<Song> getAllSongsDetails() {
		Query queryObj = entityMgrObj.createQuery("SELECT s FROM Song s");
		List<Song> songsList = queryObj.getResultList();
		if (songsList != null && songsList.size() > 0) {			
			return songsList;
		} else {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static List<Album> getAllAlbumsDetails() {
		Query queryObj = entityMgrObj.createQuery("SELECT s FROM Album s");
		List<Album> albumsList = queryObj.getResultList();
		if (albumsList != null && albumsList.size() > 0) {			
			return albumsList;
		} else {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static List<Song> getPlaylistSongs(Playlist playlist) {
		Query queryObj = entityMgrObj.createQuery("SELECT s FROM Song s WHERE :playlist_id MEMBER OF playlists");
		queryObj.setParameter("playlist_id", playlist);
		List<Song> songsList = queryObj.getResultList();
		if (songsList != null && songsList.size() > 0) {			
			return songsList;
		} else {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static List<Song> getAlbumSongs(Album album) {
		Query queryObj = entityMgrObj.createQuery("SELECT s FROM Song s WHERE s.album = :album_id");
		queryObj.setParameter("album_id", album);
		List<Song> songsList = queryObj.getResultList();
		if (songsList != null && songsList.size() > 0) {			
			return songsList;
		} else {
			return null;
		}
	}
	
	//Select Object by Id
	
	public static Playlist getPlaylistById(int playlistId) {
		if (!transactionObj.isActive()) {
			transactionObj.begin();
		}
			Query queryObj = entityMgrObj.createQuery("SELECT s FROM Playlist s WHERE s.id = :id");			
			queryObj.setParameter("id", playlistId);
			return (Playlist) queryObj.getSingleResult();
	}
	
	public static Song getSongById(int songId) {
		if (!transactionObj.isActive()) {
			transactionObj.begin();
		}
			Query queryObj = entityMgrObj.createQuery("SELECT s FROM Song s WHERE s.id = :id");			
			queryObj.setParameter("id", songId);
			return (Song) queryObj.getSingleResult();
	}
	
	public static Album getAlbumById(int albumId) {
		if (!transactionObj.isActive()) {
			transactionObj.begin();
		}
			Query queryObj = entityMgrObj.createQuery("SELECT s FROM Album s WHERE s.id = :id");			
			queryObj.setParameter("id", albumId);
			return (Album) queryObj.getSingleResult();
	}
	
	//Create Objects
	
	public static String createNewPlaylist(String title) {
		if(!transactionObj.isActive()) {
			transactionObj.begin();
		}
		Playlist playlist = new Playlist();
		playlist.setTitle(title);
		entityMgrObj.persist(playlist);
		transactionObj.commit();
		return "view_playlists.xhtml?faces-redirect=true";
	}
	
	public static String createNewSong(String title,String artist) {
		if(!transactionObj.isActive()) {
			transactionObj.begin();
		}
		Song song = new Song();
		song.setTitle(title);
		song.setArtist(artist);
		entityMgrObj.persist(song);
		transactionObj.commit();
		return "view_songs.xhtml?faces-redirect=true";
	}
	
	public static String createNewAlbum(String title) {
		if(!transactionObj.isActive()) {
			transactionObj.begin();
		}
		Album album = new Album();
		album.setTitle(title);
		entityMgrObj.persist(album);
		transactionObj.commit();
		return "view_albums.xhtml?faces-redirect=true";
	}
	
	public static String createPlaylistSong(Playlist playlist, Song song)
	{
		if(!transactionObj.isActive()) {
			transactionObj.begin();
		}
		List<Playlist> tmp = song.getPlaylist();
		if (tmp == null) tmp = new ArrayList<Playlist>();
		tmp.add(playlist);
		song.setPlaylist(tmp);
		entityMgrObj.persist(song);
		transactionObj.commit();	
		return "view_playlist_songs.xhtml?faces-redirect=true";
	}
	
	public static String createAlbumSong(Album album, Song song)
	{
		if(!transactionObj.isActive()) {
			transactionObj.begin();
		}
		song.setAlbum(album);
		entityMgrObj.persist(song);
		transactionObj.commit();	
		return "view_album_songs.xhtml?faces-redirect=true";
	}
	
	//Update Objects
	
	public static String updateSongDetails(int objId, String title, String artist) {
		if (!transactionObj.isActive()) {
			transactionObj.begin();
		}
		if(isObjPresent(objId,Song.class)) {
			Query queryObj = entityMgrObj.createQuery("UPDATE Song s SET s.title = :title, s.artist = :artist WHERE s.id = :id");			
			queryObj.setParameter("id", objId);
			queryObj.setParameter("title", title);
			queryObj.setParameter("artist", artist);
			int updateCount = queryObj.executeUpdate();
			if(updateCount > 0) {
				System.out.println("Record For Id: " + objId + " Is Updated");
			}
			Song song = new Song();
			song.setId(objId);
			song.setTitle(title);
			song.setArtist(artist);
		    entityMgrObj.merge(song);
		}
		transactionObj.commit();
		return "view_songs.xhtml?faces-redirect=true";
	}
	
	public static String updateAlbumDetails(int objId, String title) {
		if (!transactionObj.isActive()) {
			transactionObj.begin();
		}
		if(isObjPresent(objId,Album.class)) {
			Query queryObj = entityMgrObj.createQuery("UPDATE Album s SET s.title = :title WHERE s.id = :id");			
			queryObj.setParameter("id", objId);
			queryObj.setParameter("title", title);
			int updateCount = queryObj.executeUpdate();
			if(updateCount > 0) {
				System.out.println("Record For Id: " + objId + " Is Updated");
			}
			Album album = new Album();
			album.setId(objId);
			album.setTitle(title);
		    entityMgrObj.merge(album);
		}
		transactionObj.commit();
		return "view_albums.xhtml?faces-redirect=true";
	}
	
	public static String updatePlaylistDetails(int objId, String title) {
		if (!transactionObj.isActive()) {
			transactionObj.begin();
		}
		if(isObjPresent(objId,Playlist.class)) {
			Query queryObj = entityMgrObj.createQuery("UPDATE Playlist s SET s.title = :title WHERE s.id = :id");			
			queryObj.setParameter("id", objId);
			queryObj.setParameter("title", title);
			int updateCount = queryObj.executeUpdate();
			if(updateCount > 0) {
				System.out.println("Record For Id: " + objId + " Is Updated");
			}
			Playlist playlist = new Playlist();
			playlist.setId(objId);
			playlist.setTitle(title);
		    entityMgrObj.merge(playlist);
		}
		transactionObj.commit();
		return "view_playlists.xhtml?faces-redirect=true";
	}
	
	//Delete
	
	public static String deletePlaylistSong(Playlist playlist, Song song)
	{
		if(!transactionObj.isActive()) {
			transactionObj.begin();
		}
		song.setPlaylist(null);
		entityMgrObj.persist(song);
		transactionObj.commit();	
		return "view_playlist_songs.xhtml?faces-redirect=true";
	}
	
	public static String deleteAlbumSong(Album album, Song song)
	{
		if(!transactionObj.isActive()) {
			transactionObj.begin();
		}
		song.setAlbum(null);
		entityMgrObj.persist(song);
		transactionObj.commit();	
		return "view_album_songs.xhtml?faces-redirect=true";
	}
	
	public static String deleteObjectDetails(int objId,Class className) {
		String url = null;
		if (!transactionObj.isActive()) {
			transactionObj.begin();
		}

		Object deleteObj;
		try {
			deleteObj = className.newInstance();
			if(isObjPresent(objId,className)) {
				if ( deleteObj instanceof Song) {
					((Song)deleteObj).setId(objId);
					url = "view_songs.xhtml?faces-redirect=true";
				}
				if ( deleteObj instanceof Album) {
					((Album)deleteObj).setId(objId);
					url = "view_albums.xhtml?faces-redirect=true";
				}
				if ( deleteObj instanceof Playlist) {
					((Playlist)deleteObj).setId(objId);
					url = "view_playlists.xhtml?faces-redirect=true";
				}
				entityMgrObj.remove(entityMgrObj.merge(deleteObj));
			}		
			transactionObj.commit();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return url;
	}
	
	private static boolean isObjPresent(int objId,Class className) {
		boolean idResult = false;
		Query queryObj = entityMgrObj.createQuery("SELECT s FROM " + className.getName() + " s WHERE s.id = :id");
		queryObj.setParameter("id", objId);
		Object selectedObjId = queryObj.getSingleResult();
		if(selectedObjId != null) {
			idResult = true;
		}
		return idResult;
	}
}
