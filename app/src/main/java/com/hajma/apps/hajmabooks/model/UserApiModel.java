package com.hajma.apps.hajmabooks.model;

public class UserApiModel {

    private String name;
    private String email;
    private String mobile;
    private String username;
    private boolean verified;
    private String profile;
    private String following;
    private int follow_request_count;
    private int follower_count;
    private int following_count;
    private int bookCount;
    private int present_count;

    public int getPresent_count() {
        return present_count;
    }

    public void setPresent_count(int present_count) {
        this.present_count = present_count;
    }

    public int getBookCount() {
        return bookCount;
    }

    public void setBookCount(int bookCount) {
        this.bookCount = bookCount;
    }

    public String getFollowing() {
        return following;
    }

    public void setFollowing(String following) {
        this.following = following;
    }

    public int getFollow_request_count() {
        return follow_request_count;
    }

    public void setFollow_request_count(int follow_request_count) {
        this.follow_request_count = follow_request_count;
    }

    public int getFollower_count() {
        return follower_count;
    }

    public void setFollower_count(int follower_count) {
        this.follower_count = follower_count;
    }

    public int getFollowing_count() {
        return following_count;
    }

    public void setFollowing_count(int following_count) {
        this.following_count = following_count;
    }

    public UserApiModel(String name, String email, String mobile, String username, boolean verified, String profile) {
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.username = username;
        this.verified = verified;
        this.profile = profile;
    }

    public UserApiModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
