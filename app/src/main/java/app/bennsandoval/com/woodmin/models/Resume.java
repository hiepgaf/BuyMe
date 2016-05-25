package app.bennsandoval.com.woodmin.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mackbook on 1/21/15.
 */
public class Resume {

    private String title;
    private List<DataResume> data = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<DataResume> getData() {
        return data;
    }

    public void setData(List<DataResume> data) {
        this.data = data;
    }
}
