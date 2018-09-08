package okechukwu.nwagba.ng.com.photoblog;


import java.util.Date;

public class Blogpost {
  private String imageUrl;
  private String description;
  private String user_id;
  private Date time;



    public Blogpost() {
    }

    public Blogpost(String imageUrl, String description, String user_id,Date time) {
        this.imageUrl = imageUrl;
        this.description = description;
        this.user_id = user_id;
        this.time = time;

    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
