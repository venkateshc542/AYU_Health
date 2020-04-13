package com.health.ayu;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.health.pojo.PatientData;
import com.health.utils.CoreApplication;
import com.health.utils.CustomSharedPreferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * Created by venkatesh on 07-Apr-20.
 */

public class PatientListActivity extends AppCompatActivity {

    CoreApplication application = null;
    private RecyclerView patient_recyclerview;
    private LinearLayoutManager mLayoutManager;
    private CustomAdapter mAdapter;
    private ArrayList<PatientData> item_list = new ArrayList<>();
    LinearLayout close_btn_layout;
    Button close_btn;
    int selected_position;
    TextView no_data_txt;

    private ShimmerFrameLayout mShimmerViewContainer;
    SwipeRefreshLayout mSwipeRefreshLayout;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        application = (CoreApplication) getApplication();

        //setup toolbar
        TextView tv = new TextView(getApplicationContext());
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(lp);
        tv.setText("List of Appointments");
        tv.setTextSize(18);
        tv.setTextColor(Color.parseColor("#FFFFFF"));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(tv);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        //intialising UI
        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
        close_btn = findViewById(R.id.close_btn);
        patient_recyclerview = findViewById(R.id.patient_recyclerview);
        no_data_txt = findViewById(R.id.no_data_txt);
        close_btn_layout = findViewById(R.id.close_btn_layout);
        patient_recyclerview.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        patient_recyclerview.setLayoutManager(mLayoutManager);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.pullToRefresh);
        patient_recyclerview.addItemDecoration(new DividerItemDecoration(patient_recyclerview.getContext(), DividerItemDecoration.VERTICAL));

        //fetching all the patient data
        if (application.getPatientList() == null || application.getPatientList().size() == 0) {
            item_list.add(new PatientData("PAT111", "Rubi", "33", "Male", "HSR Layout,Bangalore", "09-04-20 10:00-11.00 AM", "Head ache,Vomitting", "Penidng", "Rubi@gmail.com", "9972421482", "Video"));
            item_list.add(new PatientData("PAT111", "Ruby", "22", "Female", "KR Puram,Bangalore", "09-04-20 7:00-7.30 PM", "Anxiety,Stomach pain", "Penidng", "Ruby@gmail.com", "9972421482", "Video"));
            item_list.add(new PatientData("PAT111", "Lily", "33", "Female", "Marthahalli,Bangalore", "09-04-20 8:00-8.30 PM", "Heart Problem,Liver Problem", "Penidng", "lily@gmail.com", "9972421482", "Home Visit"));
            item_list.add(new PatientData("PAT111", "Brandon", "18", "Male", "BTM layout,Bangalore", "09-04-20 12:00-12.30 PM", "High Temperature", "Penidng", "Brandon@gmail.com", "9972421482", "Phone Call"));
            item_list.add(new PatientData("PAT111", "Batakooran", "65", "Male", "Bellandur,Bangalore", "09-04-20 4:00-5.00 PM", "Coughing and Sneezing", "Penidng", "Batakooran@gmail.com", "9972421482", "Video"));
            item_list.add(new PatientData("PAT111", "Joentravo", "33", "Male", "Hebbal,Bangalore", "09-04-20 7:00-7.30 PM", "Confusion", "Penidng", "Joentravo@gmail.com", "9972421482", "Video"));
            item_list.add(new PatientData("PAT111", "Grace", "45", "Male", "Whitefiled,Bangalore", "09-04-20 9:00-9.30 PM", "Bleeding from the Nose", "Penidng", "Grace@gmail.com", "9972421482", "Video"));

            application.setPatientList(item_list);
        } else {
            item_list = application.getPatientList();
        }

        for (int i = 0; i < item_list.size(); i++) {
            if (item_list.get(i).getName() != null) {
                SimpleDateFormat dateTimeInGMT = new SimpleDateFormat("dd MMM yyyy hh:mm aa");
                dateTimeInGMT.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
                if (i == 0) {
                    Date ee = new Date();
                    item_list.get(i).setApmtDate(dateTimeInGMT.format(ee));
                } else if (i == 1) {
                    Date ee = new Date(System.currentTimeMillis() + 30 * 60 * 1000);
                    item_list.get(i).setApmtDate(dateTimeInGMT.format(ee));
                } else if (i == 2) {
                    Date ee = new Date(System.currentTimeMillis() + 60 * 60 * 1000);
                    item_list.get(i).setApmtDate(dateTimeInGMT.format(ee));
                } else if (i == 3) {
                    Date ee = new Date(System.currentTimeMillis() + 120 * 60 * 1000);
                    item_list.get(i).setApmtDate(dateTimeInGMT.format(ee));
                } else if (i == 4) {
                    Date ee = new Date(System.currentTimeMillis() + 180 * 60 * 1000);
                    item_list.get(i).setApmtDate(dateTimeInGMT.format(ee));
                } else if (i == 5) {
                    Date ee = new Date(System.currentTimeMillis() + 210 * 60 * 1000);
                    item_list.get(i).setApmtDate(dateTimeInGMT.format(ee));
                } else if (i == 6) {
                    Date ee = new Date(System.currentTimeMillis() + 270 * 60 * 1000);
                    item_list.get(i).setApmtDate(dateTimeInGMT.format(ee));
                } else if (i == 7) {
                    Date ee = new Date(System.currentTimeMillis() + 300 * 60 * 1000);
                    item_list.get(i).setApmtDate(dateTimeInGMT.format(ee));
                } else {
                    Date ee = new Date(System.currentTimeMillis() + 60 * 60 * 1000);
                    item_list.get(i).setApmtDate(dateTimeInGMT.format(ee));
                }
            }
        }

        if (item_list.size() > 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Stopping Shimmer Effect's animation after data is loaded to ListView
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);
                }
            }, 1000);
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                shuffle();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        mAdapter = new CustomAdapter(PatientListActivity.this, item_list);
        patient_recyclerview.setAdapter(mAdapter);
    }

    private void shuffle() {
        Collections.shuffle(item_list, new Random(System.currentTimeMillis()));
        mAdapter = new CustomAdapter(PatientListActivity.this, item_list);
        patient_recyclerview.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        mShimmerViewContainer.startShimmerAnimation();
    }

    @Override
    protected void onPause() {
        mShimmerViewContainer.stopShimmerAnimation();
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here
                Intent intent = new Intent(PatientListActivity.this, HomeActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(PatientListActivity.this, HomeActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        //finish();
    }


    private void errorDialogue(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PatientListActivity.this, R.style.MyDialogTheme);
        //Creating dialog box
        builder.setMessage(errorMessage)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true; // Consumed
                } else {
                    return false; // Not consumed
                }
            }
        });
        AlertDialog dialog = builder.create();
        //Setting the title manually
        builder.setCancelable(false);
        dialog.setTitle(Html.fromHtml("<font color='#E40046'>Error</font>"));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));

    }

    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
        public ArrayList<PatientData> item_list;
        public Context context;

        public CustomAdapter(PatientListActivity context, ArrayList<PatientData> item_list) {
            this.context = context;
            this.item_list = item_list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_row_home, parent, false);
            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            selected_position = position;
            holder.name_gender_age_text.setText(item_list.get(position).getName() + " " + item_list.get(position).getAge() + " " + item_list.get(position).getGender());
            holder.time_text.setText(item_list.get(position).getApmtDate());
            holder.issue_text.setText(item_list.get(position).getDescription());
            holder.location_text.setText(item_list.get(position).getLocation());

            holder.type_text.setText(item_list.get(position).getConsultType());
            if (item_list.get(position).getConsultType().equals("video")) {
                holder.type_img.setImageResource(R.drawable.round_camera);
            } else if (item_list.get(position).getConsultType().equals("Phone Call")) {
                holder.type_img.setImageResource(R.drawable.round_call);
            } else if (item_list.get(position).getConsultType().equals("Home Visit")) {
                holder.type_img.setImageResource(R.drawable.round_home);
            } else {
                holder.type_img.setImageResource(R.drawable.round_camera);
            }

            holder.item_cardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                   /* if (item_list.get(position).getStatus().equalsIgnoreCase("completed")) {
                        errorDialogue(item_list.get(position).getName() + " is already treated..! \nPlease check other patient.");
                    } else {*/
                    CustomSharedPreferences.saveStringData(PatientListActivity.this, item_list.get(position).getName(), CustomSharedPreferences.SP_KEY.PATIENT_NAME);
                    Intent intent = new Intent(PatientListActivity.this, PatientDetailsActivity.class);
                    intent.putExtra("name", item_list.get(position).getName());
                    intent.putExtra("gender", item_list.get(position).getGender());
                    intent.putExtra("age", item_list.get(position).getAge());
                    intent.putExtra("location", item_list.get(position).getLocation());
                    intent.putExtra("email", item_list.get(position).getEmail());
                    intent.putExtra("mobilenumber", item_list.get(position).getMobilenumber());
                    intent.putExtra("apmtdate", item_list.get(position).getApmtDate());
                    intent.putExtra("desc", item_list.get(position).getDescription());
                    startActivity(intent);
                    finish();
                    // }
                }
            });
        }

        @Override
        public int getItemCount() {
            return item_list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView issue_text,
                    name_gender_age_text,
                    type_text,
                    time_text,
                    location_text;

            public CardView item_cardview;
            public ImageView type_img;

            public ViewHolder(View v) {
                super(v);
                item_cardview = v.findViewById(R.id.item_cardview);
                type_img = v.findViewById(R.id.type_img);
                issue_text = v.findViewById(R.id.issue_text);
                name_gender_age_text = v.findViewById(R.id.name_gender_age_text);
                type_text = v.findViewById(R.id.type_text);
                time_text = v.findViewById(R.id.time_text);
                location_text = v.findViewById(R.id.location_text);
            }
        }
    }
}