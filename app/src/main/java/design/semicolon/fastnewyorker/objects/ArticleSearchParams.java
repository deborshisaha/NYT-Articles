package design.semicolon.fastnewyorker.objects;

import com.loopj.android.http.RequestParams;

/**
 * Created by dsaha on 2/13/16.
 */
public class ArticleSearchParams extends RequestParams {

    private class Keys {
        static final String QUERY_KEY = "q";
        static final String BEGIN_DATE_KEY = "begin_date";
        static final String END_DATE_KEY = "end_date";
        static final String SORT_KEY = "sort";
        static final String FIELD_QUERY_KEY = "fq";
    }

    public ArticleSearchParams(){super();}

    public ArticleSearchParams(SearchCriteria searchCriteria) {
        super();
        populateParams(searchCriteria);
    }

    private void populateParams(SearchCriteria searchCriteria){

        if (searchCriteria == null) {return;}

        if (searchCriteria.getSearchQueryText() != null){
            this.put(Keys.QUERY_KEY, searchCriteria.getSearchQueryText());
        }

        if (searchCriteria.getBeginDate() != null){
            this.put(Keys.BEGIN_DATE_KEY, searchCriteria.getBeginDate());
        }

        if (searchCriteria.getEndDate() != null){
            this.put(Keys.END_DATE_KEY, searchCriteria.getEndDate());
        }

        if (searchCriteria.getSortOrder() != null){
            this.put(Keys.SORT_KEY, searchCriteria.getSortOrder());
        }

        if (searchCriteria.getFieldQuery() != null){
            this.put(Keys.FIELD_QUERY_KEY, searchCriteria.getFieldQuery());
        }

    }
}
