package com.upimage.upimg.entity;

public class MailCode {
    int mail_code_id;
    String user_number;
    String code;
    String created_at;
    String updated_at;

    public int getMail_code_id() {
        return mail_code_id;
    }

    public void setMail_code_id(int mail_code_id) {
        this.mail_code_id = mail_code_id;
    }

    public String getUser_number() {
        return user_number;
    }

    public void setUser_number(String user_number) {
        this.user_number = user_number;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
}
