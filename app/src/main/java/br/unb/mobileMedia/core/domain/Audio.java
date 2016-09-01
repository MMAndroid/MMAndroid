package br.unb.mobileMedia.core.domain;

public class Audio {

    private Integer id;
    private String url;
    private Integer albumId;

    public Audio() {
    }

    public Audio(Integer id) {
        this.id = id;
    }

    public Audio(Integer id, String url, Integer albumId) {
        this.id = id;
        this.url = url;
        this.albumId = albumId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

 

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Integer albumId) {
        this.albumId = albumId;
    }

}
