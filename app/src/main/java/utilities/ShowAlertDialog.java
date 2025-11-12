package utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.innovus.vyoma.s_parking_agentApollo.R;


public class ShowAlertDialog {
    public  static void showAlertDialog(Activity context, String message)
    {
        final AlertDialog alertDialog = new AlertDialog.Builder(
                context).create();

        LayoutInflater inflater = context.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.customdialoglayout, null);
        alertDialog.setView(dialogView);
        TextView heading = (TextView) dialogView.findViewById(R.id.heading);
        TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
        Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);
        // Setting Dialog Title
        //alertDialog.setTitle(title);
        /*alertDialog.setTitle(R.string.validation_name);*/
        heading.setText(R.string.validation_name);

        // Setting Dialog Message
        /*alertDialog.setMessage(message);*/
        msg_txt.setText(message);
        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.ic_app_icon);

        // Setting OK Button
        /*alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed

            }
        });*/
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        //Animate alert dialog box
        FragmentTransaction ft = context.getFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.animator.fade_in,
                android.R.animator.fade_out);
        // Showing Alert Message
        alertDialog.show();
    }
    public  static void showAlertDialogFailure(Activity context, String message)
    {
        final AlertDialog alertDialog = new AlertDialog.Builder(
                context).create();

        LayoutInflater inflater = context.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.customdialog_failurelayout, null);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.setView(dialogView);
        TextView heading = (TextView) dialogView.findViewById(R.id.title);
        TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
        Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);
        // Setting Dialog Title
        //alertDialog.setTitle(title);
        /*alertDialog.setTitle(R.string.validation_name);*/
        // heading.setText(R.string.validation_name);

        // Setting Dialog Message
        /*alertDialog.setMessage(message);*/
        msg_txt.setText(message);
        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.ic_app_icon);

        // Setting OK Button
        /*alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed

            }
        });*/
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        //Animate alert dialog box
        FragmentTransaction ft = context.getFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.animator.fade_in,
                android.R.animator.fade_out);
        // Showing Alert Message

        if(!((Activity) context).isFinishing()){//if activity is finished or not
            alertDialog.show();
        }

    }



}
