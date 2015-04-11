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


    @Override
    public String toString() {
        return "Photo{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", main=" + main +
                ", url640='" + url640 + '\'' +
                ", url320='" + url320 + '\'' +
                ", url172='" + url172 + '\'' +
                ", url84='" + url84 + '\'' +
                '}';
    }
}
