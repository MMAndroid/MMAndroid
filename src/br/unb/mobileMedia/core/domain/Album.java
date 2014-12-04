package br.unb.mobileMedia.core.domain;


public class Album {

    private Integer id;
    private String name;
    private byte[] Image;
    private Integer authorId;


    public Album() {
    }

    public Album(Integer id) {
        this.id = id;
    }

    public Album(Integer id, String name, byte[] Image, Integer authorId) {
        this.id = id;
        this.name = name;
        this.Image = Image;
        this.authorId = authorId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getImage() {
        return Image;
    }

    public void setImage(byte[] Image) {
        this.Image = Image;
    }

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }


}
