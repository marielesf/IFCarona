package br.edu.ifrs.canoas.ifcaronasolidaria.model;

/**
 * Created by pc on 25/09/2017.
 */

public class UsuarioMoodle {

    public String token = "";
    private String username;
    private String fullname;
    private String userpictureurl;

    public UsuarioMoodle(String token, String username, String fullname, String userpictureurl) {
        this.token = token;
        this.username = username;
        this.fullname = fullname;
        this.userpictureurl = userpictureurl;
    }

    public UsuarioMoodle() { }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String toString() {
        return "=> " + username + " " + fullname + " " + userpictureurl;
    }

    public String getUserid() {
        return username;
    }

    public void setUserid(String userid) {
        this.username = userid;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getUserpictureurl() {
        return userpictureurl;
    }

    public void setUserpictureurl(String userpictureurl) {
        this.userpictureurl = userpictureurl;
    }
}
