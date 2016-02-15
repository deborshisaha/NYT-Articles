package design.semicolon.fastnewyorker.listener;

import java.util.List;

import design.semicolon.fastnewyorker.exception.NoNetworkConnectionException;
import design.semicolon.fastnewyorker.model.Article;

/**
 * Created by dsaha on 2/13/16.
 */
public interface OnArticlesLoadedListener  {
    public void onArticlesLoaded(List<Article> articles);
}

