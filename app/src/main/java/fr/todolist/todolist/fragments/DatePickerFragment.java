package fr.todolist.todolist.fragments;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

import fr.todolist.todolist.interfaces.AddTodoItemInterface;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return (new DatePickerDialog(getActivity(), this, year, month, day));
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        ((AddTodoItemInterface) getActivity()).onDateSet(year, month, dayOfMonth);
    }
}
