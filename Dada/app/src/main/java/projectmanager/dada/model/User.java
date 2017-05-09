package projectmanager.dada.model;

import java.io.Serializable;

public class User implements Serializable{

    private int    userId;
    private String phone;
    private String username;
    private String password;
    private int    credit;
    private int    sex;
    private String avatar;
    private String bio;

    public User(int userId, String phone, String username) {
        this.userId = userId;
        this.phone = phone;
        this.username = username;
    }

    public User(int userId, String phone, String username, String password, int credit, int sex, String avatar, String bio) {
        this.userId = userId;
        this.phone = phone;
        this.username = username;
        this.password = password;
        this.credit = credit;
        this.sex = sex;
        this.avatar = avatar;
        this.bio = bio;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
