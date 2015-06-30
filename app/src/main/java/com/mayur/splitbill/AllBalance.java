package com.mayur.splitbill;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Mayur on 6/22/2015.
 */
public class AllBalance extends Fragment {

    ScrollView scrollView;
    LinearLayout linearLayout;
    Context context;
    SharedPreferences logininfo;
    Button[] names;
    String[] username;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_allbalance, container, false);

        context = getActivity();
        /*progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Loading");
        progressDialog.setCanceledOnTouchOutside(false);*/
        logininfo = context.getSharedPreferences("login", context.MODE_PRIVATE);

        scrollView = (ScrollView) rootView.findViewById(R.id.scrollView2);
        linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(linearLayout);

        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String url = Global.url + "fetchallbalance";
            new DownloadDataTask().execute(url);
        } else {
            Toast.makeText(context, "Not Connected!", Toast.LENGTH_SHORT).show();
        }
        return rootView;
    }

    private class DownloadDataTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressDialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                return Global.downloadUrl(urls[0], logininfo.getString("token", ""));
            } catch (IOException e) {
                return "Unable to access Webpage";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONArray users = new JSONArray(result);
                username = new String[users.length()];
                names = new Button[users.length()];
                for (int i=0; i<users.length(); i++) {
                    final JSONObject user = users.getJSONObject(i);
                    names[i] = new Button(context);
                    names[i]. setText(user.getString("name") + " : \u20AC" + user.getString("balance"));
                    username[i] = user.getString("username");
                    linearLayout.addView(names[i]);
                    names[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, BalanceActivity.class);
                            try {
                                intent.putExtra("username", user.getString("username"));
                                startActivity(intent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }
            } catch (JSONException e) {

            }

            //progressDialog.dismiss();
        }
    }
}
