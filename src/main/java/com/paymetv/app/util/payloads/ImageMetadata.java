package com.paymetv.app.util.payloads;

import com.paymetv.app.domain.Users;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public class ImageMetadata {
    @Transient
    private Users user;

    @Transient
    private String description;

    @Transient
    private String filename;

    public ImageMetadata() {
    }
    public ImageMetadata(Users user, String description, String filename) {
        this.user = user;
        this.description = description;
        this.filename = filename;
    }

    public Users getUser() {
        return user;
    }
    public void setUser(Users user) {
        this.user = user;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getFilename() {
        return filename;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }
}
