package com.pfiev.englishcontest.model;

public class EmotionIconItem {
    public String id;
    public String pack_name;
    public String name;
    public String type;
    public String url;

    public EmotionIconItem() {
    }

    public EmotionIconItem(String pack_name, String name, String type, String url) {
        this.pack_name = pack_name;
        this.name = name;
        this.type = type;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPack_name() {
        return pack_name;
    }

    public void setPack_name(String pack_name) {
        this.pack_name = pack_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "EmotionIconItem{" +
                "id='" + id + '\'' +
                ", pack_name='" + pack_name + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}