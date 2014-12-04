package br.unb.mobileMedia.core.domain;

public class History {

    private Integer id;
    private Integer audioHistoryId;
    private Integer videoHistoryId;


    public History() {
    }

    public History(Integer id) {
        this.id = id;
    }

    public History(Integer id, Integer audioHistoryId, Integer videoHistoryId) {
        this.id = id;
        this.audioHistoryId = audioHistoryId;
        this.videoHistoryId = videoHistoryId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAudioHistoryId() {
        return audioHistoryId;
    }

    public void setAudioHistoryId(Integer audioHistoryId) {
        this.audioHistoryId = audioHistoryId;
    }

    public Integer getVideoHistoryId() {
        return videoHistoryId;
    }

    public void setVideoHistoryId(Integer videoHistoryId) {
        this.videoHistoryId = videoHistoryId;
    }


}
