package com.pfiev.englishcontest.model;

import com.pfiev.englishcontest.roomdb.entity.EmotionIcon;

public class EmotionIconItem {
    public int id;
    public String pack_name;
    public int packId;
    public String name;
    public String type;
    public String url;

    public EmotionIconItem() {
    }

    public EmotionIconItem(EmotionIcon icon) {
        this.id = icon.id;
        this.packId = icon.packId;
        this.name = icon.name;
        this.type = icon.type;
        this.url = icon.url;
    }


    public EmotionIconItem(String pack_name, String name, String type, String url) {
        this.pack_name = pack_name;
        this.name = name;
        this.type = type;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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