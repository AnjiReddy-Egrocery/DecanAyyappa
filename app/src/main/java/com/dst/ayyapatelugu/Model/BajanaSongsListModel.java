package com.dst.ayyapatelugu.Model;

public class BajanaSongsListModel {

    private String songId;
    private String songTitle;
    private String singerName;
    private String writerName;

    private String songDescription;

    public BajanaSongsListModel() {

    }

    public BajanaSongsListModel(String songId, String songTitle, String singerName, String writerName, String songDescription) {
        this.songId = songId;
        this.songTitle = songTitle;
        this.singerName = singerName;
        this.writerName = writerName;
        this.songDescription = songDescription;
    }

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getSingerName() {
        return singerName;
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }

    public String getWriterName() {
        return writerName;
    }

    public void setWriterName(String writerName) {
        this.writerName = writerName;
    }

    public String getSongDescription() {
        return songDescription;
    }

    public void setSongDescription(String songDescription) {
        this.songDescription = songDescription;
    }
}
