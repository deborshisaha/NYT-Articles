package design.semicolon.fastnewyorker.restclient;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by dsaha on 2/13/16.
 */
public class NYArticleSearchRESTClient {

    private static final String NYTIMES_SEARCH_BASE_URL = "http://api.nytimes.com/svc/search/v2/articlesearch";
    private static final String NYTIMES_BASE_URL = "http://nytimes.com/";
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String responseFormat, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getSearchBaseUrl(responseFormat), params, responseHandler);
    }

    public static String getBaseUrl() {
        return NYTIMES_BASE_URL;
    }

    private static String getSearchBaseUrl(String responseFormat) {
        return NYTIMES_SEARCH_BASE_URL + '.' + responseFormat;
    }
}