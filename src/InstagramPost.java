import java.util.ArrayList;

public class InstagramPost {

    private long createdTime;
    private String id;
    private String tags;
    private ArrayList<String> postContent;

    public InstagramPost(long createdTime, String id) {
        this.createdTime = createdTime;
        this.id = id;
    }

    public InstagramPost(long createdTime, String id, ArrayList postContent, String tags) {
        this.createdTime = createdTime;
        this.id = id;
        this.postContent = postContent;
        this.tags = tags;
    }

    public long createdTime() {
        return createdTime;
    }

    public String id() {
        return id;
    }
    public String tags() {
        return tags;
    }

    public ArrayList postContent() {
        return postContent;
    }

    public void showAll() {
        System.out.println("createdTime: " + createdTime);
        System.out.println("Id: " + id);
        System.out.println("Array Size: " + postContent.size());
        System.out.println("Tags: " + tags);

        for (int i = 0; i < postContent.size(); i++) {
            System.out.println(i + ": " + postContent.get(i));
        }
    }

    public void downloadAll(String path){
        for (int i = 0; i < postContent.size(); i++) {
            try {
                HttpDownloadUtility.downloadFile(postContent.get(i), path);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

}
