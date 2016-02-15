package design.semicolon.fastnewyorker.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import design.semicolon.fastnewyorker.helper.Util;
import design.semicolon.fastnewyorker.restclient.NYArticleSearchRESTClient;

/**
 * Created by dsaha on 2/13/16.
 */
@Table(name = "Article", id = "_id")
public class Article extends Model implements Serializable {

    @Column(name = "web_url")
    private String webURL;

    @Column(name = "snippet")
    private String snippet;

    @Column(name = "headline_kicker")
    private String headlineKicker;

    @Column(name = "headline_main")
    private String headlineMain;

    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "pub_date")
    private Date publishedDate;

    //String publishedDate;

    List<Multimedia> multiMedias;

    @Column(name = "id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private String id;

    @Column(name = "authors")
    private String authors;

    private final String WEB_URL = "web_url";
    private final String SNIPPET = "snippet";
    private final String HEADLINE = "headline";
    private final String ID = "_id";
    private final String MULTIMEDIA = "multimedia";
    private final String BYLINE = "byline";
    private final String ORIGINAL = "original";
    private final String PUBLISH_DATE = "pub_date";

    public static String ARTICLE = "article";

    public String getSnippet() {
        return snippet;
    }

    public String getAuthors() {
        return authors;
    }

    public String getWebURL() {
        return webURL;
    }

    public Article() {}

    public String getArticleId() {
        return id;
    }

    public Article(JSONObject articleJSONObject) throws JSONException {
        super();

        Log.d("Article", "JSON Object :");

        try {

            this.webURL = articleJSONObject.getString(WEB_URL);
            this.id = articleJSONObject.getString(ID);
            this.snippet = articleJSONObject.getString(SNIPPET);

            if (articleJSONObject.optJSONObject(HEADLINE) != null) {
                Headline headline = new Headline(articleJSONObject.getJSONObject(HEADLINE));
                this.headlineKicker = headline.getKicker();
                this.headlineMain = headline.getMain();
            }

            String target = articleJSONObject.getString(PUBLISH_DATE);
            Log.d("NOTE", "Date: "+target);
            //2016-02-17T00:00:00Z
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
            this.publishedDate = Util.transformToDate(target, df);
            Log.d("NOTE", "Converted Date: "+ this.publishedDate);

            if (articleJSONObject.optJSONObject(BYLINE) != null  &&
                    articleJSONObject.optJSONObject(BYLINE).getString(ORIGINAL) != null) {
                this.authors = articleJSONObject.getJSONObject(BYLINE).getString(ORIGINAL);
            }

            if (articleJSONObject.optJSONArray(MULTIMEDIA) != null) {
                JSONArray multimediaJSONArray = (JSONArray) articleJSONObject.getJSONArray(MULTIMEDIA);
                this.multiMedias = new ArrayList<Multimedia>();

                for (int i = 0; i < multimediaJSONArray.length(); i++) {

                    JSONObject multimediaJSONObject = multimediaJSONArray.getJSONObject(i);
                    Multimedia multimedia = new Multimedia(multimediaJSONObject, this);
                    multimedia.save();
                    this.multiMedias.add(multimedia);
                }
            }

            this.createDate = Calendar.getInstance().getTime();

            Log.d("Article", "ID :"+ this.getArticleId());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getArticleHeadlineKicker(){
        return headlineKicker;
    }

    public String getArticleHeadlineMain(){
        return headlineMain;
    }

    public boolean hasThumbnail() {

        if (findThumbnailMultimedia() != null) {
            return true;
        }

        return false;
    }

    public String getArticleThumbnailAbsoluteURL() {
        Multimedia multimedia = findThumbnailMultimedia();

        if (multimedia != null){
            return NYArticleSearchRESTClient.getBaseUrl()+multimedia.getMediaURL();
        }
        return "";
    }

    public String getArticleGoodQualityPicAbsoluteURL() {
        Multimedia multimedia = findGoodQualityMultimedia();

        if (multimedia != null){
            return NYArticleSearchRESTClient.getBaseUrl()+multimedia.getMediaURL();
        }
        return "";
    }

    private Multimedia findThumbnailMultimedia() {

        if (multiMedias == null) {
            multiMedias = medias();
        }

        for (Multimedia multimedia:multiMedias) {
            if (multimedia.hasThumbnailImage()) {
                return multimedia;
            }
        }
        return null;
    }

    private Multimedia findGoodQualityMultimedia() {

        if (multiMedias == null) {
            multiMedias = medias();
        }

        for (Multimedia multimedia:multiMedias) {
            if (multimedia.hasGoodQualityImage()) {
                return multimedia;
            }
        }
        return null;
    }

    public static void deleteAll() {
        ( new Thread() { public void run() {
            new Delete().from(Article.class).execute();
        } } ).start();
    }

    public List<Multimedia> medias() {
        return getMany(Multimedia.class, "Article");
    }

    public static List<Article> all() {
        return new Select().from(Article.class).orderBy("pub_date DESC").execute();
    }
}

class Headline implements Serializable {
    public String getMain() {
        return main;
    }
    public String getKicker() {
        return kicker;
    }

    private String main;
    private String kicker;

    private final String KICKER = "kicker";
    private final String MAIN = "main";

    public Headline(JSONObject headlineJSONObject) throws JSONException {

        try {
            if (headlineJSONObject.optString(MAIN) != null ||
                    headlineJSONObject.optString(MAIN).length() > 0) {
                this.main = headlineJSONObject.getString(MAIN);
            }

            if (headlineJSONObject.optString(KICKER) != null ||
                    headlineJSONObject.optString(KICKER).length() > 0) {
                this.kicker = headlineJSONObject.getString(KICKER);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

@Table(name = "Multimedia", id = "_id")
class Multimedia extends Model implements Serializable {

    @Column(name = "article")
    private Article mArticle;

    @Column(name = "type")
    private String type;

    @Column(name = "subtype")
    private String subtype;

    @Column(name = "media_url", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private String mediaURL;

    public String getMediaURL() {
        return mediaURL;
    }

    public Multimedia() {super();}

    public Multimedia(JSONObject multimediaJSONObject, Article article) throws JSONException {

        try {
            this.type = multimediaJSONObject.getString(TYPE);
            Log.d("Multimedia", "type :"+this.type);
            this.subtype = multimediaJSONObject.getString(SUB_TYPE);
            this.mediaURL = multimediaJSONObject.getString(MEDIA_URL);
            this.mArticle = article;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean hasThumbnailImage(){
        return subtype.equals("thumbnail");
    }
    public boolean hasGoodQualityImage(){
        return subtype.contains("large");
    }

    /**
     * Private
     */
    private final String TYPE = "type";
    private final String SUB_TYPE = "subtype";
    private final String MEDIA_URL = "url";
}