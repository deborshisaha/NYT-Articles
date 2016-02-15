package design.semicolon.fastnewyorker.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import design.semicolon.fastnewyorker.R;
import design.semicolon.fastnewyorker.listener.DateFormOnClickListener;
import design.semicolon.fastnewyorker.objects.SearchCriteria;

/**
 * Created by dsaha on 2/11/16.
 */

public class FilterDialogFragment extends DialogFragment {

    private TextView tvShowResultsFromLabel;
    private TextView tvShowResultsFromValue;

    private TextView tvShowResultsToLabel;
    private TextView tvShowResultsToValue;

    private TextView tvSortByLabel;
    private TextView tvSortByValue;

    private TextView tvTopicsInterestedLabel;
    private TextView tvTopicsInterestedValue;

    private boolean[] options = new boolean[] { false, false, false};
    private boolean[] clickedItems = options;

    Set<String> mSelectedItemsSet;

    public FilterDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static FilterDialogFragment newInstance(String title) {
        FilterDialogFragment frag = new FilterDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.filter_dialog_fragment, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setTitle();

        tvShowResultsFromLabel = (TextView) view.findViewById(R.id.from_label);
        tvShowResultsFromValue = (TextView) view.findViewById(R.id.from_value);

        tvShowResultsToLabel = (TextView) view.findViewById(R.id.to_label);
        tvShowResultsToValue = (TextView) view.findViewById(R.id.to_value);

        tvTopicsInterestedLabel  = (TextView) view.findViewById(R.id.topics_interested_label);
        tvTopicsInterestedValue  = (TextView) view.findViewById(R.id.topics_interested_value);

        tvSortByLabel  = (TextView) view.findViewById(R.id.sort_by_label);
        tvSortByValue  = (TextView) view.findViewById(R.id.sort_by_value);
        tvSortByValue.setText(SearchCriteria.getSavedInstance(getActivity()).getSortOrder());

        tvShowResultsFromValue.setText(getDueDateReadableFormat(SearchCriteria.getSavedInstance(getActivity()).getBeginDateInDate()));
        tvTopicsInterestedValue.setText(SearchCriteria.getSavedInstance(getActivity()).getFieldQueryInStringFormat());

        if (SearchCriteria.getSavedInstance(getActivity()).getEndDateInDate() == null){
            // No end date saved
            Calendar todaysCalendar = Calendar.getInstance();
            SearchCriteria.getSavedInstance(getActivity()).setEndDate(new java.sql.Date(todaysCalendar.getTime().getTime()));
            SearchCriteria.getSavedInstance(getActivity()).save();
            tvShowResultsToValue.setText(getDueDateReadableFormat(todaysCalendar.getTime()));
        } else {
            tvShowResultsToValue.setText(getDueDateReadableFormat( SearchCriteria.getSavedInstance(getActivity()).getEndDateInDate()));
        }

        DateFormOnClickListener onClickShowResultsBeginDateListener = new DateFormOnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),

                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker datePickerView, final int year,
                                                  final int monthOfYear, final int dayOfMonth) {

                                if (!datePickerView.isShown()) {
                                    return;
                                }

                                Calendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth, 0, 0, 0);
                                tvShowResultsFromValue.setText(getDueDateReadableFormat(calendar.getTime()));
                                SearchCriteria.getSavedInstance(getActivity()).setBeginDate(new java.sql.Date(calendar.getTime().getTime()));
                                SearchCriteria.getSavedInstance(getActivity()).save();
                            }
                        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.show();
            }
        };

        DateFormOnClickListener onClickShowResultsEndDateListener = new DateFormOnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),

                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker datePickerView, final int year,
                                                  final int monthOfYear, final int dayOfMonth) {

                                if (!datePickerView.isShown()) {
                                    return;
                                }

                                Calendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth, 0, 0, 0);
                                tvShowResultsToValue.setText(getDueDateReadableFormat(calendar.getTime()));
                                SearchCriteria.getSavedInstance(getActivity()).setEndDate(new java.sql.Date(calendar.getTime().getTime()));
                                SearchCriteria.getSavedInstance(getActivity()).save();
                            }
                        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.show();
            }
        };

        View.OnClickListener onClickTopicsInterestedListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // where we will store or remove selected items
                mSelectedItemsSet = SearchCriteria.getSavedInstance(getActivity()).getFieldKeyValues();

                String[] fields = getResources().getStringArray(R.array.news_desk_values);

                for(int i=0; i < fields.length; i++) {
                    if (mSelectedItemsSet.contains(fields[i])){
                        options[i] = true;
                    } else {
                        options[i] = false;
                    }
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                // set the dialog title
                builder.setTitle("Pick topics").setMultiChoiceItems(R.array.news_desk_values, options, new DialogInterface.OnMultiChoiceClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                        String[] fields = getResources().getStringArray(R.array.news_desk_values);

                        if (isChecked) {
                            mSelectedItemsSet.add(fields[which]);
                        } else if (mSelectedItemsSet.contains(fields[which])) {
                            mSelectedItemsSet.remove(fields[which]);
                        }
                    }

                }).setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (mSelectedItemsSet != null && mSelectedItemsSet.size() != 0){
                            SearchCriteria.getSavedInstance(getActivity()).setFieldKeyValues(mSelectedItemsSet);
                            SearchCriteria.getSavedInstance(getActivity()).save();
                            tvTopicsInterestedValue.setText(SearchCriteria.getSavedInstance(getActivity()).getFieldQueryInStringFormat());
                        }
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                }).show();
            }
        };

        tvSortByValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SearchCriteria.getSavedInstance(getActivity()).getSortOrder().equals("newest")) {
                    SearchCriteria.getSavedInstance(getActivity()).setSortOrder(false);
                    SearchCriteria.getSavedInstance(getActivity()).save();
                    tvSortByValue.setText(SearchCriteria.getSavedInstance(getActivity()).getSortOrder());
                } else if (SearchCriteria.getSavedInstance(getActivity()).getSortOrder().equals("oldest")){
                    SearchCriteria.getSavedInstance(getActivity()).setSortOrder(true);
                    SearchCriteria.getSavedInstance(getActivity()).save();
                    tvSortByValue.setText(SearchCriteria.getSavedInstance(getActivity()).getSortOrder());
                }
            }
        });

        tvShowResultsFromValue.setOnClickListener(onClickShowResultsBeginDateListener);
        tvShowResultsToValue.setOnClickListener(onClickShowResultsEndDateListener);

        tvShowResultsFromLabel.setOnClickListener(onClickShowResultsBeginDateListener);
        tvShowResultsToLabel.setOnClickListener(onClickShowResultsEndDateListener);

        tvTopicsInterestedLabel.setOnClickListener(onClickTopicsInterestedListener);
        tvTopicsInterestedValue.setOnClickListener(onClickTopicsInterestedListener);
    }

    private String getDueDateReadableFormat (Date date) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM dd, yyyy");
        return dateFormatter.format(date);
    }

    private void  setTitle(){
        String title = getArguments().getString("title", "Filter");
        getDialog().setTitle(title);
    }
}