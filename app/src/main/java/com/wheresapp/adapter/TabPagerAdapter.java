package com.wheresapp.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.wheresapp.fragment.CallListFragment;
import com.wheresapp.fragment.ContactsListFragment;

public class TabPagerAdapter extends FragmentPagerAdapter {

    public TabPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {
        Bundle bundle = new Bundle();
        ContactsListFragment contacts = new ContactsListFragment();
        switch (index) {
            case 2:
                bundle.putInt("TAB", 0);
                contacts.setArguments(bundle);
                return contacts;
            case 1:
                CallListFragment calls = new CallListFragment();
                bundle.putInt("TAB", 1);
                calls.setArguments(bundle);
                return calls;
            case 0:
                bundle.putInt("TAB", 2);
                contacts.setArguments(bundle);
                return contacts;
        }
        return contacts;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Tab" + position;
    }
}