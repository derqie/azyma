package co.ke.daletsys.azyma.ui.home;

public class HomeHolder {
    String name;
    String description;
    String url;
    int drawable;

    public HomeHolder(String name, String description, String url,int drawable) {
        this.name = name;
        this.description = description;
        this.url = url;
        this.drawable = drawable;
    }
    // get name of android through this
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public int getDrawable() {
        return drawable;
    }
    public void setDrawable(int url) {
        this.drawable = drawable;
    }
}
