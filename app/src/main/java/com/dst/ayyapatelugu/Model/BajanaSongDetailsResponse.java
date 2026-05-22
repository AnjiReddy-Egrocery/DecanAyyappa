package com.dst.ayyapatelugu.Model;

import java.util.List;

public class BajanaSongDetailsResponse {
    public String status;
    public String errorCode;
    public List<BajanaSongItem> result;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public List<BajanaSongItem> getResult() {
        return result;
    }

    public void setResult(List<BajanaSongItem> result) {
        this.result = result;
    }

    public class BajanaSongItem {
        public String songId;
        public String songTitle;
        public String singerName;
        public String songDescription;

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

        public String getSongDescription() {
            return songDescription;
        }

        public void setSongDescription(String songDescription) {
            this.songDescription = songDescription;
        }
    }
}
