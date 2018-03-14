package com.george.mydeliciousoven;

/**
 * Created by farmaker1 on 12/03/2018.
 */

public class Steps {

    String id, shortDescription, description, videoURL, thumbnailURL;

    public Steps(String string, String string2, String string3, String string4, String string5) {
        id = string;
        shortDescription = string2;
        description = string3;
        videoURL = string4;
        thumbnailURL = string5;
    }


    public String getId() {
        return id;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }


}
