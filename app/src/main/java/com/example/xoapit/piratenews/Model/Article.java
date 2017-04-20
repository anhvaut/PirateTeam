package com.example.xoapit.piratenews.Model;

import java.io.Serializable;

/**
 * Created by Xoapit on 3/30/2017.
 */

public class Article implements Serializable{
    private String title;
    private String img;
    private String link;
    private String time;
    private String substance;

    public Article(String title, String substance, String img, String link, String time) {
        this.title = title;
        this.substance = substance;
        this.img = img;
        this.link = link;
        this.time = time;
    }

    public String getSubstance() {
        return substance;
    }

    public void setSubstance(String substance) {
        this.substance = substance;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
