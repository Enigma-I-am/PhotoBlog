package okechukwu.nwagba.ng.com.photoblog;

public class Usermap {
    String Username;
    String Imageurl;


    public Usermap() {
    }

    public Usermap(String username, String imageurl) {
        Username = username;
        Imageurl = imageurl;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getImageurl() {
        return Imageurl;
    }

    public void setImageurl(String imageurl) {
        Imageurl = imageurl;
    }
}
