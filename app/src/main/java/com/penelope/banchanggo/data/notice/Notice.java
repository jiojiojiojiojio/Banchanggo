package com.penelope.banchanggo.data.notice;

import java.util.Objects;

public class Notice {

    private String id;
    private String title;
    private String content;
    private long created;

    public Notice() {
    }

    public Notice(String title, String content) {
        this.title = title;
        this.content = content;
        this.created = System.currentTimeMillis();
        this.id = "" + created;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public long getCreated() {
        return created;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreated(long created) {
        this.created = created;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notice notice = (Notice) o;
        return created == notice.created && id.equals(notice.id) && title.equals(notice.title) && content.equals(notice.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, content, created);
    }
}
