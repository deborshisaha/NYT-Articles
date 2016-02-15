package design.semicolon.fastnewyorker.objects;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Set;

import design.semicolon.fastnewyorker.helper.Util;

/**
 * Created by dsaha on 2/13/16.
 */
public class SearchCriteria {

    private static SearchCriteria instance;
    private Context mContext;

    public static SearchCriteria getSavedInstance(Context context) {
        if (instance != null) {
            instance.mContext = context;
        } else {
            instance = new SearchCriteria();

            instance.mContext = context;

            SharedPreferences scp = instance.sharedSearchCriteriaPreferences();

            if (scp!=null) {
                String beginDateTemp = scp.getString("beginDate", null);
                String endDateTemp = scp.getString("endDate", null);
                String searchQueryTextTemp = scp.getString("searchQueryText", null);
                String sortOrderTemp = scp.getString("sortOrder", null);
                Set<String> fieldKeyValuesTemp = scp.getStringSet("fieldKeys", null);

                instance.fieldKeyValues = fieldKeyValuesTemp;

                try{
                    instance.beginDate = Util.transformToDate(beginDateTemp, new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH));
                    instance.endDate = Util.transformToDate(endDateTemp, new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                instance.sortOrder = sortOrderTemp;
                instance.searchQueryText = searchQueryTextTemp;
            }
        }

        return instance;
    }

    public SearchCriteria() {
        super();
    }

    public String getSearchQueryText() {
        return searchQueryText;
    }

    public String getBeginDate() {
        return Util.dateInAPIQueryFormat(beginDate);
    }

    public String getEndDate() {
        return Util.dateInAPIQueryFormat(endDate);
    }

    public String getSortOrder(){

        if (sortOrder == null || sortOrder.length() == 0){
            return "newest";
        }

        return sortOrder;
    }

    public String getFieldQuery(){

        if (fieldKeyValues == null){return null;}

        StringBuffer stringBuffer = new StringBuffer();

        for(String fieldValue:fieldKeyValues){
            stringBuffer.append("\"" + fieldValue + "\"");
            Log.d("SearchCriteria", "field value:" + fieldValue);
        }

        if (stringBuffer.toString().length() == 0) {
            return null;
        }

        String fieldKV = "news_desk:("+stringBuffer+")";
        Log.d("SearchCriteria", "fieldKV:" + fieldKV);
        return fieldKV;
    }

    public void save(){
        if (mContext == null){return;}
        SharedPreferences scp = sharedSearchCriteriaPreferences();
        SharedPreferences.Editor editor = scp.edit();

        editor.putString("searchQueryText", searchQueryText);
        editor.putString("beginDate", getBeginDate());
        editor.putString("endDate", getEndDate());
        editor.putString("sortOrder", sortOrder);
        editor.putStringSet("fieldKeys", fieldKeyValues);

        editor.commit();
    }

    public SearchCriteria(String searchQueryText, Date beginDate, Date endDate, Set<String> fieldKeyValues) {
        this.searchQueryText = searchQueryText;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.fieldKeyValues = fieldKeyValues;
    }

    public void setFieldKeyValues(Set<String> fieldKeyValues) {
        this.fieldKeyValues = fieldKeyValues;
    }

    public void setSearchQueryText(String searchQueryText) {
        this.searchQueryText = searchQueryText;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     *  Private methods
     */
    private String searchQueryText;
    private Date beginDate;
    private Date endDate;

    public void setSortOrder(Boolean newest) {
        if (newest) {
            this.sortOrder = "newest";
        } else {
            this.sortOrder = "oldest";
        }
    }

    private String sortOrder;
    private Set<String> fieldKeyValues;

    private SharedPreferences sharedSearchCriteriaPreferences() {
        if (mContext == null){return null;}
        return mContext.getSharedPreferences("search_criteria", Context.MODE_PRIVATE);
    }
}
