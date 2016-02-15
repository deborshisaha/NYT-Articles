package design.semicolon.fastnewyorker.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by dsaha on 2/10/16.
 */
public class Util {

    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        boolean isNetworkAvailable = (activeNetworkInfo != null && activeNetworkInfo.isConnected());

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String dateInAPIQueryFormat (Date date) {

        if (date == null){
            return null;
        }

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
        return dateFormatter.format(date);
    }

    public static Date transformToDate(String date, DateFormat format) throws ParseException {
        return new Date(format.parse(date).getTime());
    }
}
