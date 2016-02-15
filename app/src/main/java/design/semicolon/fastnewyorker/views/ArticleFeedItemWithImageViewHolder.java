package design.semicolon.fastnewyorker.views;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.Bind;
import butterknife.ButterKnife;
import design.semicolon.fastnewyorker.R;
import design.semicolon.fastnewyorker.activity.NewsArticleActivity;
import design.semicolon.fastnewyorker.model.Article;
import design.semicolon.fastnewyorker.objects.Favorite;

public class ArticleFeedItemWithImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Article mArticle;

    // View bindings
    @Bind(R.id.article_thumbnail_iv)
    ImageView articleImageView;

    @Bind(R.id.article_headline_kicker_w_image_tv)
    TextView headlineTextView;

    @Bind(R.id.article_snippet_w_image_tv)
    TextView snippetTextView;

    @Bind(R.id.article_originals_w_image_tv)
    TextView originalTextView;

    @Bind(R.id.seperator_line)
    View seperatorLineView;

    @Bind(R.id.article_favorite_icon_w_image_iv)
    ImageView favoriteIconImageView;

    // Private
    private Context context;

    public ArticleFeedItemWithImageViewHolder(View itemView, Context context) {
        super(itemView);

        this.context = context;

        ButterKnife.bind(this, itemView);

        itemView.setOnClickListener(this);
    }

    public void configureViewWithArticle(Article article) {

        if (article == null) {return;}

        mArticle = article;

        if (Favorite.getInstance(context).articleUserFavorite(article)) {
            favoriteIconImageView.setVisibility(View.VISIBLE);
        } else {
            favoriteIconImageView.setVisibility(View.GONE);
        }

        String absoluteStringForThumbnail = article.getArticleThumbnailAbsoluteURL();

        if (absoluteStringForThumbnail != null || absoluteStringForThumbnail.length() !=0 ) {
            Glide.with(context).load(absoluteStringForThumbnail).placeholder(R.drawable.placeholder).into(articleImageView);
        }

        if (article.getArticleHeadlineKicker() == null ||
                article.getArticleHeadlineKicker().length() == 0) {
            this.headlineTextView.setText(article.getArticleHeadlineMain());
        } else {
            this.headlineTextView.setText(article.getArticleHeadlineKicker());
        }

        if (article.getSnippet() != null && article.getSnippet().length() != 0) {
            this.snippetTextView.setText(article.getSnippet());
        }

        if (article.getAuthors() != null && article.getAuthors().length() != 0) {
            this.originalTextView.setVisibility(View.VISIBLE);
            this.seperatorLineView.setVisibility(View.VISIBLE);
            this.originalTextView.setText(article.getAuthors());
        } else {
            this.originalTextView.setVisibility(View.GONE);
            this.seperatorLineView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        showDetails(view);
    }

    private void showDetails (View view) {
        Context context = view.getContext();
        Intent intent = new Intent(context, NewsArticleActivity.class);
        intent.putExtra(Article.ARTICLE, mArticle);
        context.startActivity(intent);
    }
}
