package br.unb.mobileMedia.core.domain;

/**
 * This class represents a single audio file.
 *
 * It's a simple pojo, just a data container.
 *
 */
public class Audio {

    private Integer id;
    private String filePath;
    private Integer albumId;

    public Audio() {
    	
    }

    public Audio(Integer id) {
        this.id = id;
    }

    public Audio(Integer id, String url, Integer albumId) {
        this.id = id;
        this.filePath = url;
        this.albumId = albumId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return filePath;
    }

    public void setUrl(String url) {
        this.filePath = url;
    }

    public Integer getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Integer albumId) {
        this.albumId = albumId;
    }

}
