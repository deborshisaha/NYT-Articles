package design.semicolon.fastnewyorker.dao;

import android.content.Context;

import design.semicolon.fastnewyorker.exception.NoNetworkConnectionException;
import design.semicolon.fastnewyorker.listener.OnArticlesLoadedListener;
import design.semicolon.fastnewyorker.objects.SearchCriteria;

/**
 * Created by dsaha on 2/13/16.
 */
public interface ArticleDao {
    public void searchArticles(SearchCriteria searchCriteria, OnArticlesLoadedListener onArticlesLoadedListener);
    public void fetchArticles(final SearchCriteria searchCriteria, Context context,int page, OnArticlesLoadedListener onArticlesLoadedListener, int cachingStrategy) throws NoNetworkConnectionException;

    public class CachingStrategy {
        public static final int NetworkOnly = 1;
        public static final int CacheOnly = NetworkOnly << 1;
        public static final int CacheThenNetwork = NetworkOnly << 2;
    }
}
