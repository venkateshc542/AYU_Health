package com.health.ayu;

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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.health.pojo.PatientData;
import com.health.utils.CoreApplication;
import com.health.utils.CustomSharedPreferences;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import noman.weekcalendar.WeekCalendar;
import noman.weekcalendar.listener.OnDateClickListener;

/**
 * Created by venkatesh 06-Apr-20.
 */
public class HomeActivity extends AppCompatActivity {

    ListView mDrawerList;
    ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();
    DrawerListAdapter2 adapter;
    private Toolbar toolbar;

    private DrawerLayout drawerLayout;

    boolean doubleBackToExitPressedOnce = false;
    WeekCalendar weekCalendar;

    TextView current_time_text;

    int selected_position;
    CoreApplication application = null;

    private ArrayList<PatientData> item_list = new ArrayList<>();
    private ArrayList<PatientData> item_list2 = new ArrayList<>();
    ArrayList<String> compltList = new ArrayList<>();
    ArrayList<String> pendindList = new ArrayList<>();

    TextView name_text, gender_text, viewall_text;
    private RecyclerView patient_recyclerview;
    private PatientAdapter patientAdapter;
    private RecyclerView.LayoutManager pLayoutManager;

    CardView reminder_card;
    TextView total_apt_text,
            complt_apt_text,
            pending_apt_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        application = (CoreApplication) getApplicationContext();
        toolbar = findViewById(R.id.toolbar);

        drawerLayout = findViewById(R.id.drawer);
        current_time_text = findViewById(R.id.current_time_text);
        SimpleDateFormat dateTimeInGMT0 = new SimpleDateFormat("dd MMM yyyy hh:mm aa");
        dateTimeInGMT0.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
        Date ee0 = new Date(System.currentTimeMillis() + 35 * 60 * 1000);
        current_time_text.setText(dateTimeInGMT0.format(ee0));
        weekCalendar = findViewById(R.id.weekCalendar);

        total_apt_text = findViewById(R.id.total_apt_text);
        complt_apt_text = findViewById(R.id.complt_apt_text);
        pending_apt_text = findViewById(R.id.pending_apt_text);
        reminder_card = findViewById(R.id.reminder_card);

        setSupportActionBar(toolbar);

        //update complete and pending counts
        if (application.getPatientList() != null && application.getPatientList().size() > 0) {
            for (int i = 0; i < application.getPatientList().size(); i++) {
                if (application.getPatientList().get(i).getStatus().equalsIgnoreCase("Completed")) {
                    compltList.add("Completed");
                } else {
                    pendindList.add("Pending");
                }
            }
        }
        if (compltList != null && compltList.size() > 0) {
            complt_apt_text.setText("" + compltList.size());
        } else {
            complt_apt_text.setText("" + 0);
        }
        if (pendindList != null && pendindList.size() > 0) {
            pending_apt_text.setText("" + pendindList.size());
        } else {
            pending_apt_text.setText("" + 7);
        }
        //end update complete and pending counts


        //first card click listener
        reminder_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, PatientDetailsActivity.class);
                intent.putExtra("name", "Brandon");
                CustomSharedPreferences.saveStringData(HomeActivity.this, "Brandon", CustomSharedPreferences.SP_KEY.PATIENT_NAME);
                intent.putExtra("gender", "Male");
                intent.putExtra("age", "27");
                intent.putExtra("location", "HSR Layout,Bangalore");
                intent.putExtra("email", "venkateshc542@gmail.com");
                intent.putExtra("mobilenumber", "9972421482");
                intent.putExtra("apmtdate", current_time_text.getText().toString());
                intent.putExtra("desc", "High Temperature");
                startActivity(intent);
            }
        });
        //end first card click listener

        //Navigation view data items
        mNavItems.add(new NavItem("About App", R.drawable.round_camera));
        mNavItems.add(new NavItem("History", R.drawable.round_camera));
        mNavItems.add(new NavItem("FAQ", R.drawable.round_camera));
        mNavItems.add(new NavItem("Help", R.drawable.round_camera));
        mNavItems.add(new NavItem("Logout", R.drawable.round_camera));

        mDrawerList = findViewById(R.id.navList);
        adapter = new DrawerListAdapter2(HomeActivity.this, mNavItems);
        mDrawerList.setAdapter(adapter);

        name_text = findViewById(R.id.name_text);
        gender_text = findViewById(R.id.gender_text);
        viewall_text = findViewById(R.id.viewall_text);
        patient_recyclerview = findViewById(R.id.patient_recyclerview);
        patient_recyclerview.setHasFixedSize(true);
        pLayoutManager = new LinearLayoutManager(this);
        patient_recyclerview.setLayoutManager(pLayoutManager);
        patient_recyclerview.addItemDecoration(new DividerItemDecoration(patient_recyclerview.getContext(), DividerItemDecoration.VERTICAL));


        //Initializing patient data
        item_list.add(new PatientData("PAT111", "Rubi", "33", "Male", "HSR Layout,Bangalore", "09-04-20 10:00-11.00 AM", "Head ache,Vomitting", "Penidng", "Rubi@gmail.com", "9972421482", "Video"));
        item_list.add(new PatientData("PAT111", "Ruby", "22", "Female", "KR Puram,Bangalore", "09-04-20 7:00-7.30 PM", "Anxiety,Stomach pain", "Penidng", "Ruby@gmail.com", "9972421482", "Home Visit"));
        item_list.add(new PatientData("PAT111", "Lily", "33", "Female", "Marthahalli,Bangalore", "09-04-20 8:00-8.30 PM", "Heart Problem,Liver Problem", "Penidng", "lily@gmail.com", "9972421482", "Home Visit"));
        item_list.add(new PatientData("PAT111", "Brandon", "18", "Male", "BTM layout,Bangalore", "09-04-20 12:00-12.30 PM", "High Temperature", "Penidng", "Brandon@gmail.com", "9972421482", "Phone Call"));
        item_list.add(new PatientData("PAT111", "Batakooran", "65", "Male", "Bellandur,Bangalore", "09-04-20 4:00-5.00 PM", "Coughing and Sneezing", "Penidng", "Batakooran@gmail.com", "9972421482", "Video"));
        item_list.add(new PatientData("PAT111", "Joentravo", "33", "Male", "Hebbal,Bangalore", "09-04-20 7:00-7.30 PM", "Confusion", "Penidng", "Joentravo@gmail.com", "9972421482", "Video"));
        item_list.add(new PatientData("PAT111", "Grace", "45", "Male", "Whitefiled,Bangalore", "09-04-20 9:00-9.30 PM", "Bleeding from the Nose", "Penidng", "Grace@gmail.com", "9972421482", "Video"));

        if (application.getPatientList() == null || application.getPatientList().size() == 0) {
            application.setPatientList(item_list);
        }


        //second card data items
        item_list2.add(new PatientData("PAT111", "Rubi", "33", "Male", "HSR Layout,Bangalore", "09-04-20 10:00-11.00 AM", "Head ache,Vomitting", "Penidng", "Rubi@gmail.com", "9972421482", "Video"));
        item_list2.add(new PatientData("PAT111", "Ruby", "22", "Female", "KR Puram,Bangalore", "09-04-20 7:00-7.30 PM", "Anxiety,Stomach pain", "Penidng", "Ruby@gmail.com", "9972421482", "Home Visit"));
        item_list2.add(new PatientData("PAT111", "Lily", "33", "Female", "Marthahalli,Bangalore", "09-04-20 8:00-8.30 PM", "Heart Problem,Liver Problem", "Penidng", "lily@gmail.com", "9972421482", "Home Visit"));

        for (int i = 0; i < item_list2.size(); i++) {
            if (item_list2.get(i).getName() != null) {
                SimpleDateFormat dateTimeInGMT = new SimpleDateFormat("dd MMM yyyy hh:mm aa");
                dateTimeInGMT.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
                if (i == 0) {
                    Date ee = new Date();
                    item_list2.get(i).setApmtDate(dateTimeInGMT.format(ee));
                } else if (i == 1) {
                    Date ee = new Date(System.currentTimeMillis() + 30 * 60 * 1000);
                    item_list2.get(i).setApmtDate(dateTimeInGMT.format(ee));
                } else {
                    Date ee = new Date(System.currentTimeMillis() + 120 * 60 * 1000);
                    item_list2.get(i).setApmtDate(dateTimeInGMT.format(ee));
                }
            }
        }
        patientAdapter = new PatientAdapter(HomeActivity.this, item_list2);
        patient_recyclerview.setAdapter(patientAdapter);

        viewall_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i1 = new Intent(HomeActivity.this, PatientListActivity.class);
                startActivity(i1);
                //finish();
            }
        });

        //week calender click listner
        weekCalendar.setOnDateClickListener(new OnDateClickListener() {
            @Override
            public void onDateClick(DateTime dateTime) {
                DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                Date date = null;
                try {
                    date = sdf.parse(dateTime.toLocalDateTime().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //String  ff=new SimpleDateFormat("dd MMM yyyy hh:mm aa").format(date).toString();
                long millis = date.getTime();
                item_list2 = new ArrayList<>();
                item_list2.add(new PatientData("PAT111", "Kene", "33", "Male", "HSR Layout,Bangalore", "09-04-20 10:00-11.00 AM", "Head ache,Vomitting", "Penidng", "Rubi@gmail.com", "9972421482", "Video"));
                item_list2.add(new PatientData("PAT111", "Pual", "22", "Female", "KR Puram,Bangalore", "09-04-20 7:00-7.30 PM", "Anxiety,Stomach pain", "Penidng", "Ruby@gmail.com", "9972421482", "Home Visit"));

                for (int i = 0; i < item_list2.size(); i++) {
                    if (item_list2.get(i).getName() != null) {
                        SimpleDateFormat dateTimeInGMT = new SimpleDateFormat("dd MMM yyyy hh:mm aa");
                        dateTimeInGMT.format(date);
                        dateTimeInGMT.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
                        if (i == 0) {
                            /*Date ee = new Date();
                            item_list.get(i).setApmtDate("" + dateTimeInGMT.format(date));*/
                            //manually adding minuts to the current time
                            Date ee = new Date(millis + 60 * 60 * 1000);
                            item_list2.get(i).setApmtDate(dateTimeInGMT.format(ee));
                        } else if (i == 1) {
                            Date ee = new Date(millis + 120 * 60 * 1000);
                            item_list2.get(i).setApmtDate(dateTimeInGMT.format(ee));
                        } else {
                            Date ee = new Date(System.currentTimeMillis() + 120 * 60 * 1000);
                            item_list2.get(i).setApmtDate(dateTimeInGMT.format(ee));
                        }
                    }
                }
                patientAdapter = new PatientAdapter(HomeActivity.this, item_list2);
                pLayoutManager = new LinearLayoutManager(HomeActivity.this);
                patient_recyclerview.setLayoutManager(pLayoutManager);
                patient_recyclerview.setAdapter(patientAdapter);
                patientAdapter.notifyDataSetChanged();
            }

        });
        //end week calender listners

        //action for navigation drawer items
        mDrawerList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 4:
                        exitAppDialogue("Do you want exit the Application?");
                        break;
                    default:
                        break;
                }

            }
        });
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            //super.onBackPressed();
            exitAppDialogue("Do you want exit the Application?");
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private void exitAppDialogue(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this, R.style.MyDialogTheme);
        //Creating dialog box
        builder.setMessage(errorMessage)
                .setCancelable(false)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        finish();
                        moveTaskToBack(true);
                        dialog.cancel();
                    }
                });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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
        dialog.setTitle(Html.fromHtml("<font color='#18A4A8'>Exit Application</font>"));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
        dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#dddddd"));
    }

    private class DrawerListAdapter2 extends BaseAdapter {
        Context mContext;
        ArrayList<NavItem> mNavItems;

        public DrawerListAdapter2(Context context, ArrayList<NavItem> navItems) {
            mContext = context;
            mNavItems = navItems;
        }

        @Override
        public int getCount() {
            return mNavItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mNavItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return getCount();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View customview = convertView;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                customview = inflater.inflate(R.layout.drawer_item, null);
                TextView titleView = customview.findViewById(R.id.drawerlist_title_text);
                ImageView imageView = customview.findViewById(R.id.drawerlist_imageView);
                titleView.setText(mNavItems.get(position).mTitle);
                imageView.setImageResource(mNavItems.get(position).mIcon);
            }
            return customview;
        }
    }


    public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.PatientViewHolder> {
        public ArrayList<PatientData> item_list2;
        public Context context;

        public PatientAdapter(HomeActivity context, ArrayList<PatientData> item_list) {
            this.context = context;
            this.item_list2 = item_list;
        }

        @Override
        public PatientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_row_home, parent, false);
            // set the view's size, margins, paddings and layout parameters
            PatientViewHolder vh = new PatientViewHolder(v);
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
        public void onBindViewHolder(final PatientViewHolder holder, final int position) {
            selected_position = position;
            holder.name_gender_age_text.setText(item_list2.get(position).getName() + " " + item_list2.get(position).getGender() + " " + item_list2.get(position).getAge());
            holder.time_text.setText(item_list2.get(position).getApmtDate());
            holder.issue_text.setText(item_list2.get(position).getDescription());
            holder.location_text.setText(item_list2.get(position).getLocation());
            holder.type_text.setText(item_list2.get(position).getConsultType());
            if (item_list2.get(position).getConsultType().equals("video")) {
                holder.type_img.setImageResource(R.drawable.round_camera);
            } else if (item_list2.get(position).getConsultType().equals("Phone Call")) {
                holder.type_img.setImageResource(R.drawable.round_call);
            } else if (item_list2.get(position).getConsultType().equals("Home Visit")) {
                holder.type_img.setImageResource(R.drawable.round_home);
            } else {
                holder.type_img.setImageResource(R.drawable.round_camera);
            }

          /*LinearLayout.LayoutParams parameter = (LinearLayout.LayoutParams) holder.item_status_text.getLayoutParams();
            parameter.setMargins(0, 0, 0, 10); // left, top, right, bottom
            holder.item_status_text.setLayoutParams(parameter);*/

            holder.item_cardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CustomSharedPreferences.saveStringData(HomeActivity.this, item_list2.get(position).getName(), CustomSharedPreferences.SP_KEY.PATIENT_NAME);

                    Intent intent = new Intent(HomeActivity.this, PatientDetailsActivity.class);
                    intent.putExtra("name", item_list2.get(position).getName());
                    intent.putExtra("gender", item_list.get(position).getGender());
                    intent.putExtra("age", item_list2.get(position).getAge());
                    intent.putExtra("location", item_list2.get(position).getLocation());
                    intent.putExtra("email", item_list2.get(position).getEmail());
                    intent.putExtra("mobilenumber", item_list2.get(position).getMobilenumber());
                    intent.putExtra("apmtdate", item_list2.get(position).getApmtDate());
                    intent.putExtra("desc", item_list2.get(position).getDescription());
                    startActivity(intent);
                    //finish();

                }
            });
        }


        @Override
        public int getItemCount() {
            return item_list2.size();
        }

        public class PatientViewHolder extends RecyclerView.ViewHolder {

            public TextView issue_text,
                    name_gender_age_text,
                    type_text,
                    time_text,
                    location_text;

            public CardView item_cardview;
            public ImageView type_img,
                    time_img;

            public PatientViewHolder(View v) {
                super(v);

                item_cardview = v.findViewById(R.id.item_cardview);
                type_img = v.findViewById(R.id.type_img);
                time_img = v.findViewById(R.id.time_img);
                issue_text = v.findViewById(R.id.issue_text);
                name_gender_age_text = v.findViewById(R.id.name_gender_age_text);
                type_text = v.findViewById(R.id.type_text);
                time_text = v.findViewById(R.id.time_text);
                location_text = v.findViewById(R.id.location_text);
            }
        }
    }

}