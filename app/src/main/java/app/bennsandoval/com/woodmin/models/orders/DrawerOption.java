package app.bennsandoval.com.woodmin.models.orders;

public class DrawerOption {

    private String section;
    private int icon;
    private int count = 0;

    public DrawerOption(String section, int icon, int count) {
        this.section = section;
        this.icon = icon;
        this.count = count;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
