package com.mayur.splitbill;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class BalanceActivity extends ActionBarActivity {

    ExpandableListAdapter expandableListAdapter;
    ExpandableListView expandableListView;
    List<Bill> listHeader;
    TextView amount, date, description;
    Button billSplit, billImage;
    ProgressDialog progressDialog;
    SharedPreferences logininfo;
    Bill bill;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);

        context = this;
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setCanceledOnTouchOutside(false);
        logininfo = getSharedPreferences("login", MODE_PRIVATE);

        expandableListView = (ExpandableListView) this.findViewById(R.id.expandableListView2);
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String url = Global.url + "fetchbillsbyuser/" + username;
            new DownloadDataTask().execute(url);
        } else {
            Toast.makeText(getApplicationContext(), "Not Connected!", Toast.LENGTH_SHORT).show();
        }
    }

    private class DownloadDataTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
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
            listHeader = new ArrayList<Bill>();
            try {
                JSONArray jsonbills = new JSONArray(result);
                for (int i=0; i<jsonbills.length(); i++) {
                    JSONObject jsonbill = jsonbills.getJSONObject(i);
                    listHeader.add(new Bill(jsonbill.getString("trans_id"), jsonbill.getString("amount"), jsonbill.getString("description"), jsonbill.getString("date")));
                }
            } catch (JSONException e) {

            }
            expandableListAdapter = new ExpandableListAdapter(context, listHeader);
            expandableListView.setAdapter(expandableListAdapter);
            progressDialog.dismiss();
        }
    }

    class ExpandableListAdapter extends BaseExpandableListAdapter {

        private Context context;
        private List<Bill> listHeader;
        public ExpandableListAdapter(Context context, List<Bill> d_listHeader) {
            this.context = context;
            this.listHeader = d_listHeader;
        }

        @Override
        public int getGroupCount() {
            return this.listHeader.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return this.listHeader.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return this.listHeader.get(groupPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_group, null);
            }
            amount = (TextView) convertView.findViewById(R.id.textViewAmount);
            date = (TextView) convertView.findViewById(R.id.textViewDate);
            description = (TextView) convertView.findViewById(R.id.textViewDescription);
            Bill bill = (Bill) getGroup(groupPosition);
            amount.setText("\u20AC" + bill.getAmount());
            description.setText(bill.getDescription());
            date.setText(bill.getDate());
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_details, null);
            }
            billSplit = (Button) convertView.findViewById(R.id.buttonDetBillSplit);
            billImage = (Button) convertView.findViewById(R.id.buttonDetBillImage);
            final Bill bill = (Bill) getGroup(groupPosition);
            billSplit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogFragment splitDialog = new SplitDialog(bill.getId());
                    splitDialog.show(getSupportFragmentManager(), "Bill Split");
                }
            });
            billImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogFragment imageDialog = new ImageDialog(bill.getId());
                    imageDialog.show(getSupportFragmentManager(), "Bill Image");
                }
            });
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    private class ImageDialog extends DialogFragment {
        private String id;
        private ImageView imageView;
        private TextView errorText;
        public ImageDialog(String d_id) {
            this.id = d_id;
        }
        @Override
        public Dialog onCreateDialog (Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater layoutInflater = getActivity().getLayoutInflater();
            final View dialogView = layoutInflater.inflate(R.layout.dialog_image, null);
            builder.setView(dialogView);
            imageView = (ImageView) dialogView.findViewById(R.id.imageView);
            errorText = (TextView) dialogView.findViewById(R.id.textViewError);
            String url = Global.baseurl + "img/" + id + ".jpg";
            new GetImageTask().execute(url);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            return builder.create();
        }

        private class GetImageTask extends AsyncTask<String, Void, Bitmap> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                errorText.setText("Loading...");
            }

            @Override
            protected Bitmap doInBackground(String... urls) {
                Bitmap image = null;
                try {
                    URL url = new URL(urls[0]);
                    URLConnection urlConnection = url.openConnection();
                    urlConnection.setRequestProperty("Cache-Control", "no-cache");
                    urlConnection.setRequestProperty("Pragma", "no-cache");
                    InputStream is = new BufferedInputStream(urlConnection.getInputStream());
                    //InputStream is = new java.net.URL(urls[0]).openStream();
                    image = BitmapFactory.decodeStream(is);
                } catch (IOException e) {

                }
                return image;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                super.onPostExecute(result);
                if (result != null) {
                    errorText.setText("");
                    imageView.setImageBitmap(result);
                } else {
                    errorText.setText("Bill Image not Availabe");
                }
                //progressDialog.dismiss();
            }
        }
    }

    private class SplitDialog extends DialogFragment {
        private String id;
        public SplitDialog(String d_id) {
            this.id = d_id;
        }
        @Override
        public Dialog onCreateDialog (Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            String url = Global.url + "fetchsplit/" + id;
            try {
                String response = null;
                try {
                    response = new GetSplitTask().execute(url).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                JSONArray names = new JSONArray(response);
                StringBuilder stringBuilder = new StringBuilder();
                for (int i=0; i<names.length(); i++) {
                    JSONObject name = names.getJSONObject(i);
                    stringBuilder.append(name.getString("name"));
                    stringBuilder.append("\n");
                }
                String messege = stringBuilder.toString();
                builder.setMessage(messege);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
            } catch (JSONException e) {

            }

            return builder.create();
        }

        private class GetSplitTask extends AsyncTask<String, Void, String> {
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

                //progressDialog.dismiss();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_balance, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
