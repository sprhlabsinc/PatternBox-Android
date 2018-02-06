package com.premium.patternbox;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.premium.patternbox.app.AppConfig;
import com.premium.patternbox.app.FabricCategoryInfo;
import com.premium.patternbox.app.FabricInfo;
import com.premium.patternbox.app.NotionCategoryInfo;
import com.premium.patternbox.app.NotionInfo;
import com.premium.patternbox.app.PatternCategoryInfo;
import com.premium.patternbox.app.PatternInfo;
import com.premium.patternbox.app.ProjectInfo;
import com.premium.patternbox.fragment.MainFragment;
import com.premium.patternbox.fragment.PolicyFragment;
import com.premium.patternbox.fragment.TermsFragment;
import com.premium.patternbox.helper.NetworkManager;
import com.premium.patternbox.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private Toolbar toolbar;

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_MAIN = "main";
    private static final String TAG_HELP = "help";
    private static final String TAG_POLICY = "policy";
    private static final String TAG_TERM = "term";
    private static final String TAG_CONTACT = "contact";
    public static String CURRENT_TAG = TAG_MAIN;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;

    private SessionManager session;

    private KProgressHUD progressHUD;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        session = new SessionManager(getApplicationContext());
        progressHUD = new KProgressHUD(MainActivity.this);
        progressHUD.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        // load nav menu header data
        loadNavHeader();

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_MAIN;
            loadHomeFragment();
        }
        AppConfig.initData();
        loadInitDataFromServer();
        loadProjects();
    }

    @Override
    public void onStart() {
        super.onStart();
        AppConfig.selPatternID = 0;
        AppConfig.selFabricIDs = "";
        AppConfig.selNotionIDs = "";
    }

    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website, notifications action view (dot)
     */
    private void loadNavHeader() {
        // showing dot next to notifications label
        //navigationView.getMenu().getItem(3).setActionView(R.layout.menu_dot);
    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            // show or hide the fab button
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }
        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                // home
                MainFragment homeFragment = new MainFragment();
                return homeFragment;
            case 1:
                // photos
                TermsFragment termsFragment = new TermsFragment();
                return termsFragment;
            case 2:
                // movies fragment
                PolicyFragment policyFragment = new PolicyFragment();
                return policyFragment;
            case 3:
                // notifications fragment
                TermsFragment notificationsFragment = new TermsFragment();
                return notificationsFragment;

            case 4:
                // settings fragment
//                SettingsFragment settingsFragment = new SettingsFragment();
//                return settingsFragment;
            default:
                return new MainFragment();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
//        TextView title=(TextView)findViewById(getResources().getIdentifier("action_bar_title", "id", getPackageName()));
//        title.setText(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_main:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_MAIN;
                        break;
                    case R.id.nav_about_us:
                        startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                        drawer.closeDrawers();
                        break;
                    case R.id.nav_policy:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_POLICY;
                        break;
                    case R.id.nav_term:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_TERM;
                        break;
                    case R.id.nav_contact:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_CONTACT;

                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("message/rfc822");
                        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"Patternboxapp@gmail.com"});
                        i.putExtra(Intent.EXTRA_SUBJECT, "");
                        i.putExtra(Intent.EXTRA_TEXT   , "");
                        try {
                            startActivity(Intent.createChooser(i, "Send mail..."));
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                        }
                        drawer.closeDrawers();
                        break;
                    case R.id.nav_logout:
                        logout();
                        return true;
                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_MAIN;
                loadHomeFragment();
                return;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // show menu only when home fragment is selected
        if (navItemIndex == 0) {
            //getMenuInflater().inflate(R.menu.main, menu);
        }

        // when fragment is notifications, load the menu created for notifications
        if (navItemIndex == 3) {
            //getMenuInflater().inflate(R.menu.notifications, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // user is in notifications fragment
        // and selected 'Mark all as Read'
        if (id == R.id.action_mark_all_read) {
            Toast.makeText(getApplicationContext(), "All notifications marked as read!", Toast.LENGTH_LONG).show();
        }

        // user is in notifications fragment
        // and selected 'Clear All'
        if (id == R.id.action_clear_notifications) {
            Toast.makeText(getApplicationContext(), "Clear all notifications!", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

    //Logout
    private void logout(){
        session.setAPIKey("");
        session.setUserID(0);
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    private void loadInitDataFromServer(){

        progressHUD.show();
        Map<String, String> params = new HashMap<String, String>();
        params.put("purchase", "1");

        NetworkManager networkManager = new NetworkManager(getApplicationContext(), Request.Method.GET, AppConfig.API_TAG_INIT, params);
        networkManager.setNetworkListener(new NetworkManager.NetworkListener(){

            @Override
            public void onResult(JSONObject result) {
                progressHUD.dismiss();
                try {
                    JSONArray array = result.getJSONArray("patterns");
                    for(int n = 0; n < array.length(); n++)
                    {
                        JSONObject object = array.getJSONObject(n);
                        PatternInfo newItem = new PatternInfo();
                        newItem.id = object.getInt(AppConfig.KPatternId);
                        newItem.name = object.getString(AppConfig.KPatternName);
                        newItem.isPdf = object.getInt(AppConfig.KPatternIsPDF);
                        newItem.key = object.getString(AppConfig.KPatternKey);
                        newItem.info = object.getString(AppConfig.KPatternInfo);
                        newItem.frontImage = object.getString(AppConfig.KPatternFronPic);
                        newItem.backImage = object.getString(AppConfig.KPatternBackPic);
                        newItem.isBookmark = object.getInt(AppConfig.KPatternBookMark);
                        newItem.categories = object.getString(AppConfig.KPatternCategories);
                        newItem.urls = object.getString(AppConfig.KPatternUrls);

                        AppConfig.addPattern(newItem);
                    }
                    array = result.getJSONArray("fabrics");
                    for(int n = 0; n < array.length(); n++) {
                        JSONObject object = array.getJSONObject(n);
                        FabricInfo newItem = new FabricInfo();
                        newItem.id = object.getInt(AppConfig.KFabricId);
                        newItem.imageUrl = object.getString(AppConfig.KFabricImageName);
                        newItem.isBookmark = object.getInt(AppConfig.KFabricBookMark);
                        newItem.categories = object.getString(AppConfig.KFabricCategories);

                        newItem.name = object.getString(AppConfig.KFabricName);
                        newItem.type = object.getString(AppConfig.KFabricType);
                        newItem.color = object.getString(AppConfig.KFabricColor);
                        newItem.width = object.getString(AppConfig.KFabricWidth);
                        newItem.length = object.getString(AppConfig.KFabricLength);
                        newItem.weight = object.getString(AppConfig.KFabricWeight);
                        newItem.stretch = object.getString(AppConfig.KFabricStretch);
                        newItem.uses = object.getString(AppConfig.KFabricUses);
                        newItem.careInstructions = object.getString(AppConfig.KFabricCareInstructions);
                        newItem.notes = object.getString(AppConfig.KFabricNotes);

                        AppConfig.addFabric(newItem);
                    }
                    array = result.getJSONArray("notions");
                    for(int n = 0; n < array.length(); n++) {
                        JSONObject object = array.getJSONObject(n);
                        NotionInfo newItem = new NotionInfo();
                        newItem.id = object.getInt(AppConfig.KNotionId);
                        newItem.type = object.getString(AppConfig.KNotionType);
                        newItem.size = object.getString(AppConfig.KNotionSize);
                        newItem.color = object.getString(AppConfig.KNotionColor);
                        newItem.category = object.getString(AppConfig.KNotionCategoryId);

                        AppConfig.addNotion(newItem);
                    }

                    array = result.getJSONArray("pattern_categories");
                    for (int n = 0; n < array.length(); n ++) {
                        JSONObject object = array.getJSONObject(n);
                        PatternCategoryInfo newItem = new PatternCategoryInfo(object.getString("id"), object.getString("category"), -1, false);
                        AppConfig.addPatternCategory(newItem);
                    }
                    array = result.getJSONArray("fabric_categories");
                    for (int n = 0; n < array.length(); n ++) {
                        JSONObject object = array.getJSONObject(n);
                        FabricCategoryInfo newItem = new FabricCategoryInfo(object.getString("id"), object.getString("category"), false);
                        AppConfig.addFabricCategory(newItem);
                    }
                    array = result.getJSONArray("notion_categories");
                    for (int n = 0; n < array.length(); n ++) {
                        JSONObject object = array.getJSONObject(n);
                        NotionCategoryInfo newItem = new NotionCategoryInfo(object.getString("id"), object.getString("category"), false);
                        AppConfig.addNotionCategory(newItem);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(String error) {
                progressHUD.dismiss();
            }
        });
    }

    private void loadProjects(){

        progressHUD.show();
        Map<String, String> params = new HashMap<String, String>();

        NetworkManager networkManager = new NetworkManager(getApplicationContext(), Request.Method.GET, AppConfig.API_TAG_PROJECTS, params);
        networkManager.setNetworkListener(new NetworkManager.NetworkListener(){

            @Override
            public void onResult(JSONObject result) {
                progressHUD.dismiss();
                try {
                    JSONArray array = result.getJSONArray("result");
                    for(int n = 0; n < array.length(); n++)
                    {
                        JSONObject object = array.getJSONObject(n);
                        ProjectInfo newItem = new ProjectInfo();
                        newItem.id = object.getInt(AppConfig.KProjectId);
                        newItem.name = object.getString(AppConfig.KProjectName);
                        newItem.client = object.getString(AppConfig.KProjectClient);
                        newItem.measurements = object.getString(AppConfig.KProjectMeasurement);
                        newItem.image = object.getString(AppConfig.KProjectImageName);
                        newItem.pattern = object.getInt(AppConfig.KProjectPattern);
                        newItem.notes = object.getString(AppConfig.KProjectNotes);
                        newItem.fabrics = object.getString(AppConfig.KProjectFabrics);
                        newItem.notions = object.getString(AppConfig.KProjectNotions);

                        AppConfig.addProject(newItem);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(String error) {
                progressHUD.dismiss();
            }
        });
    }
}
