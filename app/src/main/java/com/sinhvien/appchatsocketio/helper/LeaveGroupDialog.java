package com.sinhvien.appchatsocketio.helper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.sinhvien.appchatsocketio.model.Room;

public class LeaveGroupDialog extends DialogFragment {
    String groupName;
    // Use this instance of the interface to deliver action events
    public LeaveGroupDialogListener listener;

    public LeaveGroupDialog(String groupName) {
        this.groupName = groupName;
    }

    // Technique passing events back to the dialog'host
    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
   public interface LeaveGroupDialogListener {
        void onDialogPositiveClick(DialogFragment dialogFragment);
    }

    // Override the Fragment.onAttach() method to instantiate the LeaveGroupListener
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the LeaveGroupDialogListener so we can send events to the host
            listener = (LeaveGroupDialogListener) context;
        } catch (ClassCastException ex) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(getActivity().toString()
                    + " must implement LeaveGroupDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Confirm")
                .setMessage("Do you want to leave group " + groupName + " ?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Send the positive button event back to the host activity
                        listener.onDialogPositiveClick(LeaveGroupDialog.this);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        return builder.create();
    }
}
