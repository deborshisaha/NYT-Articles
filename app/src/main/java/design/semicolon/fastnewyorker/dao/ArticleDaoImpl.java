package design.semicolon.fastnewyorker.dao;

import android.content.ClipData;
import android.content.Context;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import design.semicolon.fastnewyorker.activity.HomeActivity;
import design.semicolon.fastnewyorker.exception.NoNetworkConnectionException;
import design.semicolon.fastnewyorker.helper.Util;
import design.semicolon.fastnewyorker.listener.OnArticlesLoadedListener;
import design.semicolon.fastnewyorker.model.Article;
import design.semicolon.fastnewyorker.objects.ArticleSearchParams;
import design.semicolon.fastnewyorker.objects.SearchCriteria;
import design.semicolon.fastnewyorker.restclient.NYArticleSearchRESTClient;

/**
 * Created by dsaha on 2/13/16.
 */
public class ArticleDaoImpl implements ArticleDao {

    private final String CLIENT_ID = "5ddcb1d046bd8a239ca229da0c9f575a:17:74346414";
    private final String RESPONSE_FORMAT_JSON = "json";

    @Override
    public void searchArticles(final SearchCriteria searchCriteria, final OnArticlesLoadedListener onArticlesLoadedListener) {

        ArticleSearchParams filterParams = new ArticleSearchParams(searchCriteria);
        filterParams.add("api-key", CLIENT_ID);

        NYArticleSearchRESTClient.get(RESPONSE_FORMAT_JSON, null, filterParams, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                onArticlesLoadedListener.onArticlesLoaded(processArticleResponse(response));
            }
        });
    }

    @Override
    public void fetchArticles(final SearchCriteria searchCriteria, final Context context, final int page, final OnArticlesLoadedListener onArticlesLoadedListener, final int cachingStrategy) throws NoNetworkConnectionException {

        ArticleSearchParams filterParams = new ArticleSearchParams(searchCriteria);
        filterParams.add("api-key", CLIENT_ID);
        filterParams.add("page", Integer.toString(page));

        if (cachingStrategy == CachingStrategy.CacheOnly || cachingStrategy == CachingStrategy.CacheThenNetwork) {
            List<Article> list = Article.all();
            onArticlesLoadedListener.onArticlesLoaded(list);
        }

        if (cachingStrategy == CachingStrategy.CacheOnly) {
            return;
        }

        if ( !Util.isNetworkAvailable(context) ) {
            throw new NoNetworkConnectionException();
        }

        NYArticleSearchRESTClient.get(RESPONSE_FORMAT_JSON, null, filterParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                if (page == 0){
                    Article.deleteAll();
                }
                onArticlesLoadedListener.onArticlesLoaded(processArticleResponse(response));
            }
        });
    }

    private List<Article> processArticleResponse (JSONObject response) {

        List<Article> articles = new ArrayList<Article>();
        JSONArray articlesJSONArray = null;

        try {
            if (response.optJSONObject("response") == null) {
                return null;
            }

            if (response.getJSONObject("response").optJSONArray("docs") == null) {
                return null;
            }

            articlesJSONArray = response.getJSONObject("response").getJSONArray("docs");

            ActiveAndroid.beginTransaction();
            try {
                for (int i = 0; i < articlesJSONArray.length(); i++) {

                    JSONObject articleJSONObject = articlesJSONArray.getJSONObject(i);
                    final Article article = new Article(articleJSONObject);
                    article.save();
                    articles.add(article);
                }
                ActiveAndroid.setTransactionSuccessful();
            }
            finally {
                ActiveAndroid.endTransaction();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return articles;
    }
}