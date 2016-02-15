package design.semicolon.fastnewyorker.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import design.semicolon.fastnewyorker.R;
import design.semicolon.fastnewyorker.model.Article;
import design.semicolon.fastnewyorker.views.ArticleFeedItemWithImageViewHolder;
import design.semicolon.fastnewyorker.views.ArticleFeedItemWithoutImageViewHolder;

/**
 * Created by dsaha on 2/13/16.
 */

public class ArticlesFeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private List<Article> mArticles;
    Context mContext;

    private final int ARTICLE_WITH_IMAGE = 1;
    private final int ARTICLE_WITHOUT_IMAGE = 2;
    private HashMap<String,Boolean> mArticleInRecyclerViewMap = new HashMap<String,Boolean>();

    public ArticlesFeedAdapter(List<Article> articles, Context context) {
        this.mArticles = articles;
        this.mContext = context;
    }

    public ArticlesFeedAdapter(Context context) {
        this.mContext = context;
    }

    public void addArticles(List<Article> articles) {

        if (mArticles == null) {
            mArticles = new ArrayList<Article>();
        }

        for (Article article: articles) {
            if (!mArticleInRecyclerViewMap.containsKey(article.getArticleId())) {
                // Not in the list, add it
                mArticleInRecyclerViewMap.put(article.getArticleId(), true);
                mArticles.add(article);
            }
        }
    }

    public void clearArticles() {

        if (mArticles != null && mArticles.size() != 0) {
            mArticles.removeAll(mArticles);
        }

        if (mArticleInRecyclerViewMap != null && mArticleInRecyclerViewMap.size() != 0) {
            mArticleInRecyclerViewMap.clear();
        }
    }

    @Override
    public int getItemViewType(int position) {

        Article article = mArticles.get(position);

        if (article.hasThumbnail()) {
            return ARTICLE_WITH_IMAGE;
        }

        return ARTICLE_WITHOUT_IMAGE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case ARTICLE_WITH_IMAGE:
                View itemArticleWithImageView = inflater.inflate(R.layout.item_article_with_image, viewGroup, false);
                viewHolder = new ArticleFeedItemWithImageViewHolder(itemArticleWithImageView, this.mContext);
                break;
            default:
                View itemArticleWithoutImageView = inflater.inflate(R.layout.item_article_without_image, viewGroup, false);
                viewHolder = new ArticleFeedItemWithoutImageViewHolder(itemArticleWithoutImageView, this.mContext);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {

        Article article = null;

        switch (viewHolder.getItemViewType()) {
            case ARTICLE_WITHOUT_IMAGE:{
                ArticleFeedItemWithoutImageViewHolder articleFeedItemWithoutImageViewHolder = (ArticleFeedItemWithoutImageViewHolder) viewHolder;

                if (position < getItemCount()) {
                    article = mArticles.get(position);
                }

                articleFeedItemWithoutImageViewHolder.configureViewWithoutArticle(article);

                break;
            }
            case ARTICLE_WITH_IMAGE:{
                ArticleFeedItemWithImageViewHolder articleFeedItemWithImageViewHolder = (ArticleFeedItemWithImageViewHolder) viewHolder;

                if (position < getItemCount()) {
                    article = mArticles.get(position);
                }

                articleFeedItemWithImageViewHolder.configureViewWithArticle(article);

                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        if ( mArticles != null) {
            return mArticles.size();
        }
        return 0;
    }

}