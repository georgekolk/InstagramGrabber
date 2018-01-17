import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InstagramHelper {

    private String charset = "UTF-8";
    private String currentPostID = "";
    private String currentCreateUnixTime;

    public InstagramPost lastPost(String blog)throws Exception{
        URL getPhotos = new URL("https://www.instagram.com/" + blog + "/media/");

        HttpURLConnection connection = (HttpURLConnection) getPhotos.openConnection();
        StringBuilder content = new StringBuilder();

        connection.setRequestProperty("Accept-Charset", charset);
        connection.setUseCaches(false);
        connection.setRequestProperty("User-Agent", "jHateSMM");

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        String line;

        while ((line = bufferedReader.readLine()) != null)
        {
            content.append(line + "\n");
        }
        bufferedReader.close();

        JSONParser pars = new JSONParser();

        try {
            JSONObject obj = (JSONObject) pars.parse(content.toString());
            JSONArray items = (JSONArray) obj.get("items");
            boolean moreAvailable = (boolean) obj.get("more_available");
            JSONObject photosObj = (JSONObject) items.get(0);

            currentPostID = (String) photosObj.get("id");
            currentCreateUnixTime = (String) photosObj.get("created_time");

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new InstagramPost(Long.parseLong(currentCreateUnixTime), currentPostID);
    }

    public ArrayList recentInstargamPosts(String blog, String lastPost)throws Exception{
        URL getPhotos;

        boolean moreAvailable = true;
        ArrayList<InstagramPost> instagramPostsList = new ArrayList<>();

        if (lastPost.length() == 0){
            getPhotos = new URL("https://www.instagram.com/" + blog + "/media/");
        }else {
            getPhotos = new URL("https://www.instagram.com/" + blog + "/media/?max_id=" + lastPost);
        }

        HttpURLConnection connection = (HttpURLConnection) getPhotos.openConnection();
        StringBuilder content = new StringBuilder();

        connection.setRequestProperty("Accept-Charset", charset);
        connection.setUseCaches(false);
        connection.setRequestProperty("User-Agent", "jHateSMM");

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        String line;

        while ((line = bufferedReader.readLine()) != null)
        {
            content.append(line + "\n");
        }
        bufferedReader.close();

        JSONParser pars = new JSONParser();

        try {
            JSONObject obj = (JSONObject) pars.parse(content.toString());
            JSONArray items = (JSONArray) obj.get("items");
            moreAvailable = (boolean) obj.get("more_available");

            for (int i = 0; i < items.size(); i++) {
                JSONObject photosObj = (JSONObject) items.get(i);
                currentPostID = (String) photosObj.get("id");
                currentCreateUnixTime = (String) photosObj.get("created_time");
                ArrayList<String> media = new ArrayList<>();

                String returnTagsString = "";

                if (photosObj.get("caption") != null) {
                    JSONObject tags = (JSONObject) photosObj.get("caption");
                    Matcher m = Pattern.compile("#[A-Za-zА-Яа-я_!0-9]*").matcher(tags.toString());
                    while (m.find()) {
                        returnTagsString = returnTagsString + m.group() + " ";
                    }
                }

                switch ((String)photosObj.get("type")) {
                    case "video":
                        JSONObject video = (JSONObject)photosObj.get("videos");
                        JSONObject videoStandardResolution = (JSONObject) video.get("standard_resolution");
                        String videoStandardResolutionURL = (String) videoStandardResolution.get("url");
                        media.add(videoStandardResolutionURL);
                        break;
                    case "carousel":
                        JSONArray carouselMedia = (JSONArray)photosObj.get("carousel_media");

                        for (int z = 0; z < carouselMedia.size(); z++){
                            JSONObject carouselItem = (JSONObject)carouselMedia.get(z);

                            if (carouselItem.containsKey("videos")){
                                JSONObject carouselVideo = (JSONObject)carouselItem.get("videos");
                                JSONObject carouselVideoStandardResolution = (JSONObject) carouselVideo.get("standard_resolution");
                                String carouselVideoStandardResolutionURL = (String) carouselVideoStandardResolution.get("url");
                                media.add(carouselVideoStandardResolutionURL);
                            }
                            if (carouselItem.containsKey("images")){
                                JSONObject carouselImage = (JSONObject)carouselItem.get("images");
                                JSONObject carouselImageStandardResolution = (JSONObject) carouselImage.get("standard_resolution");
                                String carouselImageStandardResolutionURL = (String)carouselImageStandardResolution.get("url");
                                media.add(carouselImageStandardResolutionURL);
                            }
                        }
                        break;
                    case "image":
                        JSONObject images = (JSONObject)photosObj.get("images");
                        JSONObject imageStandardResolution = (JSONObject) images.get("standard_resolution");
                        String imageStandardResolutionURL = (String) imageStandardResolution.get("url");
                        media.add(imageStandardResolutionURL);
                        break;
                }
                instagramPostsList.add(new InstagramPost(Long.parseLong(currentCreateUnixTime),currentPostID, media,returnTagsString));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (!moreAvailable){
            instagramPostsList.add(null);
        }
        return instagramPostsList;
    }

    public InstagramPost newPostInstagram (String blog, String lastPost)throws Exception{

        ArrayList<InstagramPost> newSourcePostListInstagram = new ArrayList();
        ArrayList<InstagramPost> newPostsList = new ArrayList<>();

        boolean foundLast = false;
        String lastPostId = "";

        while(foundLast == false) {

            newSourcePostListInstagram = this.recentInstargamPosts(blog, lastPostId);

            for (int i = 0; i < newSourcePostListInstagram.size(); i++) {

                if (newSourcePostListInstagram.get(i).createdTime() > Long.valueOf(lastPost)){
                    newPostsList.add(newSourcePostListInstagram.get(i));
                    lastPostId = String.valueOf(newSourcePostListInstagram.get(i).createdTime());
                }else if (newSourcePostListInstagram.get(i).createdTime() == Long.valueOf(lastPost)){
                    //System.out.println("NO NEW POSTS");
                }else if((newSourcePostListInstagram.get(i).createdTime() < Long.valueOf(lastPost))){
                    foundLast = true;
                }
            }
        }

        int newPostsSize = newPostsList.size();

        if (newPostsSize > 0) {
            return newPostsList.get(newPostsSize - 1);
        }else {
            return null;
        }
    }
}


