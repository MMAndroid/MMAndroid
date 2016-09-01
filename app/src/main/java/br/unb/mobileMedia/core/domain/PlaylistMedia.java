package br.unb.mobileMedia.core.domain;

public class PlaylistMedia {

    private Integer id;
    private Integer FK_Media_Id;
    private Integer FK_PlaylistId;

    public PlaylistMedia() {
    }

    public PlaylistMedia(Integer id) {
        this.id = id;
    }

    public PlaylistMedia(Integer id, Integer FK_Media_Id, Integer FK_PlaylistId) {
        this.id = id;
        this.FK_Media_Id = FK_Media_Id;
        this.FK_PlaylistId = FK_PlaylistId;
    }

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getFK_Media_Id() {
		return FK_Media_Id;
	}

	public void setFK_Media_Id(Integer fK_Media_Id) {
		FK_Media_Id = fK_Media_Id;
	}

	public Integer getFK_PlaylistId() {
		return FK_PlaylistId;
	}

	public void setFK_PlaylistId(Integer fK_PlaylistId) {
		FK_PlaylistId = fK_PlaylistId;
	}

    

}
