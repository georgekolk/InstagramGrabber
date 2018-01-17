import java.io.File;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args)throws Exception {

        boolean moreAvailable = true;
        InstagramHelper instagramHelper = new InstagramHelper();

        ArrayList<InstagramPost> recentInstargamPosts = new ArrayList<>();
        InstagramPost lastPost = null;
        int recentInstargamPostsSize;
        String lastPostId = "";

        String blogToDownload = "trr";
        String tmpDir = "D://temp";
        File f = new File(tmpDir + "//" + blogToDownload);
        f.mkdir();


        while (moreAvailable) {

            recentInstargamPosts.addAll(instagramHelper.recentInstargamPosts(blogToDownload, lastPostId));
            recentInstargamPostsSize = recentInstargamPosts.size();

            try {
                lastPost = recentInstargamPosts.get(recentInstargamPostsSize - 1);
                lastPostId = lastPost.id();
            }catch (NullPointerException npe){
                npe.printStackTrace();
            }

            if (lastPost == null){
                moreAvailable = false;
            }
        }

        for (InstagramPost instagramPosts : recentInstargamPosts) {
            instagramPosts.downloadAll(tmpDir + "//" + blogToDownload);
        }

    }
}
