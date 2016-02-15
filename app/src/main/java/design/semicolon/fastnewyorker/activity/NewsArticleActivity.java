package design.semicolon.fastnewyorker.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import butterknife.Bind;
import butterknife.ButterKnife;
import design.semicolon.fastnewyorker.R;
import design.semicolon.fastnewyorker.helper.*;
import design.semicolon.fastnewyorker.model.Article;
import design.semicolon.fastnewyorker.objects.Favorite;

public class NewsArticleActivity extends AppCompatActivity {

    @Bind(R.id.news_article_webview)
    WebView wvNewsArticleWebView;

    private Article article;
    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_article);

        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            article = (Article) getIntent().getSerializableExtra(Article.ARTICLE);
        }

        if (article.getArticleHeadlineKicker() == null ||
                article.getArticleHeadlineKicker().length() == 0) {
            setTitle(article.getArticleHeadlineMain());
        } else {
            setTitle(article.getArticleHeadlineKicker());
        }

        wvNewsArticleWebView.getSettings().setAppCacheMaxSize(10 * 1024 * 1024);
        wvNewsArticleWebView.getSettings().setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
        wvNewsArticleWebView.getSettings().setAllowFileAccess(true);
        wvNewsArticleWebView.getSettings().setAppCacheEnabled(true);
        wvNewsArticleWebView.getSettings().setJavaScriptEnabled(true);
        wvNewsArticleWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        wvNewsArticleWebView.setWebViewClient(new WebViewClient());
        wvNewsArticleWebView.getSettings().setLoadsImagesAutomatically(true);

        if ( !Util.isNetworkAvailable(this) ) {
            wvNewsArticleWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }

        wvNewsArticleWebView.loadUrl( article.getWebURL() );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.detail_menu, menu);

        MenuItem shareItem = menu.findItem(R.id.menu_item_share);
        MenuItem favoriteItem = menu.findItem(R.id.menu_item_favorite);

        if (Favorite.getInstance(this).articleUserFavorite(article)) {
            favoriteItem.setIcon(R.drawable.menu_favorite_filled);
        }

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_item_share:
                doShare(createShare());
                return true;
            case R.id.menu_item_favorite:

                if (Favorite.getInstance(this).articleUserFavorite(article)) {
                    Favorite.getInstance(this).removeFromFavorite(article);
                    item.setIcon(R.drawable.menu_favorite_empty);
                } else {
                    Favorite.getInstance(this).addToFavorite(article);
                    item.setIcon(R.drawable.menu_favorite_filled);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Intent createShare() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, article.getWebURL());
        return shareIntent;
    }

    private void doShare(Intent shareIntent) {

        if (shareIntent == null) {
            return;
        }

        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }
}
