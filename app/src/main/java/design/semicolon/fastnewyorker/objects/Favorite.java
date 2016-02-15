package design.semicolon.fastnewyorker.objects;

import android.content.Context;
import android.content.SharedPreferences;

import design.semicolon.fastnewyorker.model.Article;

/**
 * Created by dsaha on 2/14/16.
 */
public class Favorite {

    private static Favorite instance = new Favorite();
    private Context mContext;

    public static Favorite getInstance(Context context){
        if (instance != null) {
            instance.mContext = context;
        }

        return instance;
    }

    private Favorite(){}

    public boolean articleUserFavorite(Article article) {
        if (mContext == null) {return false;}
        SharedPreferences sharedpreferences = sharedPreferences();

        if (sharedpreferences ==null) {return false;}
        return sharedpreferences.getBoolean(sharedArticleFavoriteId(article), false);
    }

    public void removeFromFavorite(Article article){
        if (mContext == null){return;}
        SharedPreferences sharedpreferences = sharedPreferences();
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.remove(sharedArticleFavoriteId(article));
        editor.commit();
    }

    public void addToFavorite(Article article){
        if (mContext == null){return;}
        SharedPreferences sharedpreferences = sharedPreferences();
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(sharedArticleFavoriteId(article), true);
        editor.commit();
    }

    private SharedPreferences sharedPreferences() {
        if (mContext == null){return null;}
        return mContext.getSharedPreferences("favorites", Context.MODE_PRIVATE);
    }

    private String sharedArticleFavoriteId(Article article) {
        return "fav"+ article.getArticleId();
    }
}
