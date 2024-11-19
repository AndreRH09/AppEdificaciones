package com.example.appedificaciones.model.ent;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class UserEntity {
    @PrimaryKey(autoGenerate = true)
    private int userId;

    public String user;
    public String password;
    public String email;
    public String phone;
    public String photo;

    public UserEntity(){

    }

    // Constructor privado para el Builder
    private UserEntity(Builder builder) {
        this.userId = builder.userId;
        this.user = builder.user;
        this.password = builder.password;
        this.email = builder.email;
        this.phone = builder.phone;
        this.photo = builder.photo;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getUserId() {
        return userId;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPhoto() {
        return photo;
    }

    // Clase Builder interna
    public static class Builder {
        private int userId;
        private String user;
        private String password;
        private String email;
        private String phone;
        public String photo;

        public Builder setUserId(int userId) {
            this.userId = userId;
            return this;
        }

        public Builder setUser(String user) {
            this.user = user;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setPhone(String phone) {
            this.phone = phone;
            return this;
        }
        public Builder setPhoto(String photo) {
            this.photo = photo;
            return this;
        }


        public UserEntity build() {
            return new UserEntity(this);
        }
    }


}
