package com.wheresapp;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends FragmentActivity implements
        ActionBar.TabListener {

    private ViewPager viewPager;
    private ActionBar actionBar;
    private TabPagerAdapter tabPagerAdapter;

    // Declare Tab Variable
    ContactsListFragment fragmentTabs[] = new ContactsListFragment[3];
    String[] nameTabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Habrá que variar las opciones según reciente, favoritos o todos los contactos
        fragmentTabs[0] = new ContactsListFragment();
        fragmentTabs[1] = new ContactsListFragment();
        fragmentTabs[2] = new ContactsListFragment();
        nameTabs = getResources().getStringArray(R.array.tab_names);

        viewPager = (ViewPager) findViewById(R.id.pager);
        tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabPagerAdapter);
        actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        for (String tab_name : nameTabs) {
            actionBar.addTab(actionBar
                    .newTab()
                    .setText(tab_name)
                    .setTabListener(this));
        }

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) { }

            @Override
            public void onPageScrollStateChanged(int arg0) { }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {

    }
}
