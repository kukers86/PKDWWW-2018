package beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.servlet.ServletContext;

import entity.Song;
import entity.Album;
import db.DatabaseOperations;

@ManagedBean
@SessionScoped
public class AlbumBean {
	private EntityManager manager;
	private String title;
	private List<Album> albumsList;
	private String editAlbumId;

	public AlbumBean() {
		ServletContext sc = (ServletContext)
				FacesContext
				.getCurrentInstance()
				.getExternalContext()
				.getContext();
		EntityManagerFactory emf = (EntityManagerFactory)
						sc.getAttribute("emf");
		manager = emf.createEntityManager();
	}
	
	public String getEditAlbumId() {
		return editAlbumId;
	}

	public void setEditAlbumId(String editAlbumId) {
		this.editAlbumId = editAlbumId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	private void loadDataList() {
		albumsList = DatabaseOperations.getAllAlbumsDetails();
    }

    public List<Album> getAlbumsList() {
        if (FacesContext.getCurrentInstance().getRenderResponse()) {
            loadDataList(); // Reload to get most recent data.
        }
        return albumsList;
    }

    public String addNewAlbum(AlbumBean album) {
		return DatabaseOperations.createNewAlbum(album.getTitle());		
	}
	
	public String deleteAlbumDetailsById(int albumId) {
		return DatabaseOperations.deleteObjectDetails(albumId,Album.class);		
	}
	
	public String editAlbumDetailsById() {
		editAlbumId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("selectedAlbumId");		
		return "edit_album.xhtml";
	}
	
	public String updateAlbumDetails(AlbumBean albumBean) {
		return DatabaseOperations.updateAlbumDetails(Integer.parseInt(albumBean.getEditAlbumId()), albumBean.getTitle());		
	}
	
	public String showSongsById(String editAlbumId)
	{
		this.editAlbumId = editAlbumId;
		return "view_album_songs.xhtml";
	}
	
	public List<Song> getAlbumSongsList(){
		Album album = new Album();
		album = DatabaseOperations.getAlbumById(Integer.parseInt(editAlbumId));
		return DatabaseOperations.getAlbumSongs(album);
	}
	
	public String addSongById(int songId)
	{
		Song song = new Song();
		song = DatabaseOperations.getSongById(songId);
		Album album = new Album();
		album = DatabaseOperations.getAlbumById(Integer.parseInt(editAlbumId));
		return DatabaseOperations.createAlbumSong(album, song);
	}
	
	public String remSongById(int songId)
	{
		Song song = new Song();
		song = DatabaseOperations.getSongById(songId);
		Album album = new Album();
		album = DatabaseOperations.getAlbumById(Integer.parseInt(editAlbumId));
		return DatabaseOperations.deleteAlbumSong(album, song);
	}
}
