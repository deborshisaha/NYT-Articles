package design.semicolon.fastnewyorker.listener;

import android.app.DatePickerDialog;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by dsaha on 2/12/16.
 */
public class DateFormOnClickListener implements View.OnClickListener{

    private TextView dateValueTextView;
    private Date placeholderDate;

    public DateFormOnClickListener(TextView tv) {
        this.dateValueTextView = tv;
    }

    public DateFormOnClickListener() {}

    @Override
    public void onClick(View v) {
        /*
        final Calendar c = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),

                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker datePickerView, final int year,
                                          final int monthOfYear, final int dayOfMonth) {

                        if (!datePickerView.isShown()) {
                            return;
                        }

                        Calendar alarmDateTime = new GregorianCalendar(year, monthOfYear, dayOfMonth, 0, 0, 0);
                        placeholderDate = alarmDateTime.getTime();
                        dateValueTextView.setText(getDueDateReadableFormat(placeholderDate));
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
        */
    }

    private String getDueDateReadableFormat (Date date) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM dd, yyyy");
        return dateFormatter.format(date);
    }
}
