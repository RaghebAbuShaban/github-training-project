package com.example.myapplication;


import android.graphics.Bitmap;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "users")
public class User implements Serializable {
        @PrimaryKey(autoGenerate = true)
        private int id;
        private String username;
        private String password;
        private String type;
        private String category;
        private Bitmap photo;

        public User(String username, String password, String type, String category, Bitmap photo) {
            this.username = username;
            this.password = password;
            this.type = type;
            this.category = category;
            this.photo = photo;
        }

        @Ignore
        public User(String username, String password, String type, String category) {
            this(username, password, type, category, null);
        }

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public Bitmap getPhoto() { return photo; }
        public void setPhoto(Bitmap photo) { this.photo = photo; }
    }
