package com.booking.feedon.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.booking.feedon.BaseClasses.BaseActivity;
import com.booking.feedon.Fragments.FeedListFragment;
import com.booking.feedon.Fragments.MyFeedFragment;
import com.booking.feedon.Fragments.WebViewFragment;
import com.booking.feedon.Listeners.OnFragmentInteractionListener;
import com.booking.feedon.Models.RSSItem;
import com.booking.feedon.R;

/**
 * Created by Ajeet Kumar Meena on 18-06-2016.
 */

public class MainActivity extends BaseActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        OnFragmentInteractionListener,
        FragmentManager.OnBackStackChangedListener {

    public static final String EXTRA_ATTACH_FRAGMENT_NO = "extra_attach_fragment_no";
    public static final int EXTRA_FEED_LIST_FRAGMENT = 2;
    public static final int EXTRA_WEB_VIEW_FRAGMENT = 3;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FragmentManager fragmentManager;
    private AppBarLayout appBarLayout;
    private boolean doubleBackToExitPressedOnce = false;
    private ImageView toolbarActionImageView;


    private void attachHomeFragment() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frame, new MyFeedFragment(), MyFeedFragment.TAG);
        //fragmentTransaction.addToBackStack(MyFeedFragment.TAG);
        fragmentTransaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        super.handleDrawerPress();
        super.setStatusBarTranslucent(true);
        fragmentManager = getSupportFragmentManager();
        initViews();
        setupNavigationDrawer();
        attachHomeFragment();
      /*  RSSParser rssParser = new RSSParser(this);
        rssParser.getRSSFeedFromRSSLink("http://feeds.bbci.co.uk/news/rss.xml", new RSSParser.OnRSSFetchComplete() {
            @Override
            public void onRSSFetchComplete(RSSFeed rssFeed) {

            }
        });*/
    }

    private void initViews() {
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        toolbarActionImageView = (ImageView) findViewById(R.id.toolbar_action);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.about: {
                MaterialDialog aboutDialog = new MaterialDialog.Builder(this)
                        .title("About")
                        .content("Add favourite feeds to database and read them within the app." +
                                "\nDeveloped by: Ajeet Kumar Meena").positiveText("Ok")
                        .build();
                aboutDialog.getWindow().getAttributes().windowAnimations = R.style.MyAnimation_Window;
                aboutDialog.show();
                return true;
            }
        }
        return true;
    }

    public ImageView getToolbarActionImageView() {
        return toolbarActionImageView;
    }

    public void setToolbarActionImageView(ImageView toolbarActionImageView) {
        this.toolbarActionImageView = toolbarActionImageView;
    }


    private void setupNavigationDrawer() {
        fragmentManager.addOnBackStackChangedListener(this);
        navigationView.setNavigationItemSelectedListener(this);
        setToolbar((Toolbar) findViewById(R.id.toolbar));
        setSupportActionBar(getToolbar());
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, getToolbar(), R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getContentView().getWindowToken(), 0);
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
    }


    @Override
    public void showDrawerToggle(boolean showDrawerToggle) {

    }

    @Override
    public void onBackStackChanged() {
        actionBarDrawerToggle.setDrawerIndicatorEnabled(fragmentManager.getBackStackEntryCount() == 0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(fragmentManager.getBackStackEntryCount() > 0);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void clearBackStack() {
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int id = intent.getIntExtra(EXTRA_ATTACH_FRAGMENT_NO, 0);
        switch (id) {
            case EXTRA_FEED_LIST_FRAGMENT: {
                attachDetailFragment(intent.getIntExtra(FeedListFragment.EXTRA_ID, -1));
                break;
            }
            case EXTRA_WEB_VIEW_FRAGMENT: {
                attachWebViewFragment((RSSItem) intent.getParcelableExtra(WebViewFragment.EXTRA_RSS_ITEM));
                break;
            }
        }
    }

    private void attachDetailFragment(int id) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, FeedListFragment.getInstance(id), FeedListFragment.TAG);
        fragmentTransaction.addToBackStack(FeedListFragment.TAG);
        fragmentTransaction.commit();
        appBarLayout.setExpanded(true, true);
    }

    private void attachWebViewFragment(RSSItem rssItem) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, WebViewFragment.getInstance(rssItem), FeedListFragment.TAG);
        fragmentTransaction.addToBackStack(WebViewFragment.TAG);
        fragmentTransaction.commit();
        appBarLayout.setExpanded(true, true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.isDrawerIndicatorEnabled() &&
                actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        } else if (item.getItemId() == android.R.id.home &&
                getSupportFragmentManager().popBackStackImmediate()) {
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

}
