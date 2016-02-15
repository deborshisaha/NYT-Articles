package design.semicolon.fastnewyorker.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import design.semicolon.fastnewyorker.R;
import design.semicolon.fastnewyorker.adapters.ArticlesFeedAdapter;
import design.semicolon.fastnewyorker.dao.ArticleDao;
import design.semicolon.fastnewyorker.dao.ArticleDaoImpl;
import design.semicolon.fastnewyorker.exception.NoNetworkConnectionException;
import design.semicolon.fastnewyorker.fragment.FilterDialogFragment;
import design.semicolon.fastnewyorker.listener.OnArticlesLoadedListener;
import design.semicolon.fastnewyorker.model.Article;
import design.semicolon.fastnewyorker.objects.SearchCriteria;

/**
 * Created by dsaha on 2/10/16.
 */
public class HomeActivity extends AppCompatActivity {

    @Bind(R.id.feed)
    RecyclerView mArticleFeedRecyclerView;

    @Bind(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    private static final String TAG = "HomeActivity";
    private ArticlesFeedAdapter mArticlesFeedAdapter;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    private OnArticlesLoadedListener mOnArticlesLoadedListener;
    private ArticleDaoImpl mArticleDaoImplementor;
    private int page = 0;

    /**
     * http://stackoverflow.com/questions/29079478/how-to-implement-endless-scrolling-using-staggeredlayoutmanager
     */

    private boolean loading = true;
    private int pastVisibleItems, visibleItemCount, totalItemCount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_articles_feed);

        // Bundle
        ButterKnife.bind(this);

        // Initialize the adapter
        mArticlesFeedAdapter = new ArticlesFeedAdapter(HomeActivity.this);

        // Set the layout manager
        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        mArticleFeedRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);

        // Set adapter
        mArticleFeedRecyclerView.setAdapter(mArticlesFeedAdapter);

        // Article Data access layer
        mArticleDaoImplementor = new ArticleDaoImpl();

        mOnArticlesLoadedListener = new OnArticlesLoadedListener() {
            @Override
            public void onArticlesLoaded(List<Article> articles) {
                loading = false;
                swipeContainer.setRefreshing(false);

                if (page == 0) {
                    mArticlesFeedAdapter.clearArticles();
                }

                mArticlesFeedAdapter.addArticles(articles);
                mArticlesFeedAdapter.notifyDataSetChanged();
            }
        };

        try {
            mArticleDaoImplementor.fetchArticles(SearchCriteria.getSavedInstance(HomeActivity.this),HomeActivity.this, page, mOnArticlesLoadedListener, ArticleDao.CachingStrategy.CacheThenNetwork);
        } catch (NoNetworkConnectionException e) {
            Toast.makeText(HomeActivity.this, e.getReason()+' '+e.getRemedy(), Toast.LENGTH_LONG).show();
        }

        mArticleFeedRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {

                    visibleItemCount = mStaggeredGridLayoutManager.getChildCount();
                    totalItemCount = mStaggeredGridLayoutManager.getItemCount();
                    int[] firstVisibleItems = null;
                    firstVisibleItems = mStaggeredGridLayoutManager.findFirstVisibleItemPositions(firstVisibleItems);
                    if(firstVisibleItems != null && firstVisibleItems.length > 0) {
                        pastVisibleItems = firstVisibleItems[0];
                    }

                    if (!loading) {
                        if ((visibleItemCount + pastVisibleItems) > 0.95 * totalItemCount) {

                            loading = true;
                            page = page+1;

                            try {
                                mArticleDaoImplementor.fetchArticles(SearchCriteria.getSavedInstance(HomeActivity.this), HomeActivity.this, page, mOnArticlesLoadedListener, ArticleDao.CachingStrategy.CacheThenNetwork);
                            } catch (NoNetworkConnectionException e) {
                                Toast.makeText(HomeActivity.this, e.getReason()+' '+e.getRemedy(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            }
        });

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 0;
                try {
                    mArticleDaoImplementor.fetchArticles(SearchCriteria.getSavedInstance(HomeActivity.this), HomeActivity.this, page, mOnArticlesLoadedListener, ArticleDao.CachingStrategy.CacheThenNetwork);
                } catch (NoNetworkConnectionException e) {
                    swipeContainer.setRefreshing(false);
                    Toast.makeText(HomeActivity.this, e.getReason()+' '+e.getRemedy(), Toast.LENGTH_LONG).show();
                }
            }
        });

        setTitle(R.string.articles);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                showFilterDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);

        // Buttons
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final MenuItem filterItem = menu.findItem(R.id.action_filter);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        if (searchItem != null) {
            MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    filterItem.setVisible(true);
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    filterItem.setVisible(false);
                    return true;
                }
            });
            MenuItemCompat.setActionView(searchItem, searchView);
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                searchItem.collapseActionView();
                SearchCriteria.getSavedInstance(HomeActivity.this).setSearchQueryText(query);
                SearchCriteria.getSavedInstance(HomeActivity.this).save();

                try {
                    mArticleDaoImplementor.fetchArticles(SearchCriteria.getSavedInstance(HomeActivity.this), HomeActivity.this, page, mOnArticlesLoadedListener, ArticleDao.CachingStrategy.CacheThenNetwork);
                } catch (NoNetworkConnectionException e) {
                    Toast.makeText(HomeActivity.this, e.getReason()+' '+e.getRemedy(), Toast.LENGTH_LONG).show();
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void showFilterDialog() {
        FragmentManager fm = getSupportFragmentManager();
        FilterDialogFragment editNameDialog = FilterDialogFragment.newInstance("Filter");
        editNameDialog.show(fm, "filter_dialog_fragment");
    }

}
