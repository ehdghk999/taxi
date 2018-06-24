package "패키지 네임";

import android.print.PrinterId;

import java.io.Serializable;

public class PostList implements Serializable {
    private String post_num;
    private String start_loc;
    private String end_loc;
    private String start_time;
    private String user_id;
    private String post_date;

    public String getPost_num() {
        return post_num;
    }

    public void setPost_num(String post_num) {
        this.post_num = post_num;
    }

    public String getStart_loc() {
        return start_loc;
    }

    public void setStart_loc(String start_loc) {
        this.start_loc = start_loc;
    }

    public String getEnd_loc() {
        return end_loc;
    }

    public void setEnd_loc(String end_loc) {
        this.end_loc = end_loc;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPost_date() {
        return post_date;
    }

    public void setPost_date(String post_date) {
        this.post_date = post_date;
    }
}