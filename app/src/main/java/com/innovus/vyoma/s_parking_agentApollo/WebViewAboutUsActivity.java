package com.innovus.vyoma.s_parking_agentApollo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import data_objects.SParkingAgentModel;
import dmax.dialog.SpotsDialog;
import utilities.constants.SessionManager;

public class WebViewAboutUsActivity extends AppCompatActivity {
    private WebView about_webview;
    private  String web_view_string="";
    private  String web_view_heading="";
    private SpotsDialog progressDialog;
    private static final String TAG = "Main";
    private SessionManager session;

    SParkingAgentModel dataModel=SParkingAgentModel.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_about_us);

        Bundle bundle = getIntent().getExtras();
        try {
            web_view_string = bundle.getString("web_view_string");
            web_view_heading = bundle.getString("heading_webview");
            Log.e("web_url",web_view_string);

        }catch (Exception e){
            e.printStackTrace();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(web_view_heading);

        session = new SessionManager(getApplicationContext());

        initView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initView(){

        about_webview=(WebView) findViewById(R.id.about_webview);
        WebSettings settings = about_webview.getSettings();
        settings.setJavaScriptEnabled(true);
        about_webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        about_webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        start_progress_dialog();

        about_webview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i(TAG, "Processing webview url click...");
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                Log.i(TAG, "Finished loading URL: " + url);
                stop_progress_dialog();
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.e(TAG, "Error: " + description);
                Toast.makeText(WebViewAboutUsActivity.this, "Oh no! " + description, Toast.LENGTH_SHORT).show();
                alertDialog.setTitle(getResources().getString(R.string.error));
                alertDialog.setMessage(description);
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                alertDialog.show();
            }
        });
        about_webview.loadUrl(web_view_string);

        dataModel.about_advanced_dash = 1;
    }


    void start_progress_dialog() {

        try{
            progressDialog = new SpotsDialog(WebViewAboutUsActivity.this, R.style.CustomWaitDialog);
            progressDialog.setCancelable(false);
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void stop_progress_dialog() {

        try{

            if(progressDialog!=null){
                progressDialog.dismiss();
                progressDialog=null;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        dataModel.about_advanced_dash = 0;

        if (session.isLoggedIn()) {
                startActivity(new Intent(WebViewAboutUsActivity.this, DashBoardActivity.class));
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                finish();

        } else {
                startActivity(new Intent(WebViewAboutUsActivity.this, LoginActivity.class));
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                finish();
        }
    }
}
