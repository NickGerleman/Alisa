package model;

public class Photo extends JsonModel {
    private String id;
    private  String userId;
    private boolean main;
    private String url640;
    private String url320;
    private String url172;
    private String url84;

    public Photo(String id, String userId, boolean main, String url640, String url320, String url172, String url84){
        this.id=id;
        this.userId=userId;
        this.main=main;
        this.url640=url640;
        this.url320=url320;
        this.url172=url172;
        this.url84=url84;
    }

    public String getId() {
        return id;
    }
    public String getUrl640() {
        return url640;
    }

    public boolean isMain() {
        return main;
    }

    public String getUrl320() {
        return url320;
    }

    public String getUrl172() {
        return url172;
    }

    public String getUrl84() {
        return url84;
    }

}
