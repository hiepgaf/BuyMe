package app.bennsandoval.com.woodmin.fragments;

import android.app.Activity;
import android.database.Cursor;

import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import app.bennsandoval.com.woodmin.R;
import app.bennsandoval.com.woodmin.adapters.DrawerAdapter;
import app.bennsandoval.com.woodmin.data.WoodminContract;
import app.bennsandoval.com.woodmin.interfaces.NavigationDrawerCallbacks;
import app.bennsandoval.com.woodmin.models.orders.DrawerOption;

public class NavigationDrawerFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = NavigationDrawerFragment.class.getSimpleName();

    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    private NavigationDrawerCallbacks mCallbacks;

    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    private DrawerOption[] mValues;
    private DrawerAdapter mAdapter;

    private static final int SHOP_LOADER = 0;
    private static final String[] SHOP_PROJECTION = {
            WoodminContract.ShopEntry.COLUMN_NAME,
            WoodminContract.ShopEntry.COLUMN_DESCRIPTION
    };
    private int SHOP_COLUMN_NAME = 0;
    private int SHOP_COLUMN_DESCRIPTION = 1;

    private static final String[] COUNT_PROJECTION = {
            "COUNT(*)"
    };
    private int COLUMN_COUNT = 0;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mDrawerListView = (ListView) inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        mValues = new DrawerOption[]{
                new DrawerOption(getString(R.string.title_section1),R.drawable.orders,0),
                new DrawerOption(getString(R.string.title_section2),R.drawable.products,0),
                new DrawerOption(getString(R.string.title_section3),R.drawable.customers,0),
                //new DrawerOption(getString(R.string.title_section4),R.drawable.logo,0),
                new DrawerOption(getString(R.string.title_section5),R.drawable.logout,-1)
        };
        mAdapter = new DrawerAdapter(getActionBar().getThemedContext(),mValues);
        mDrawerListView.setAdapter(mAdapter);

        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);

        View header = inflater.inflate(R.layout.fragment_navigation_drawer_header, container, false);
        mDrawerListView.addHeaderView(header);

        getActivity().getSupportLoaderManager().initLoader(SHOP_LOADER, null, this);

        return mDrawerListView;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout) {

        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle( getActivity(),
                mDrawerLayout, R.drawable.ic_drawer,
                R.string.navigation_drawer_open) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                Cursor cursor = getActivity().getContentResolver().query(WoodminContract.OrdersEntry.CONTENT_URI,
                        COUNT_PROJECTION,
                        null,
                        null,
                        null);
                if(cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {
                            int count = cursor.getInt(COLUMN_COUNT);
                            mValues[0].setCount(count);
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                }

                cursor = getActivity().getContentResolver().query(WoodminContract.OrdersEntry.CONTENT_URI,
                        COUNT_PROJECTION,
                        null,
                        null,
                        null);
                if(cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {
                            int count = cursor.getInt(COLUMN_COUNT);
                            mValues[0].setCount(count);
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                }

                cursor = getActivity().getContentResolver().query(WoodminContract.ProductEntry.CONTENT_URI,
                        COUNT_PROJECTION,
                        null,
                        null,
                        null);
                if(cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {
                            int count = cursor.getInt(COLUMN_COUNT);
                            mValues[1].setCount(count);
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                }

                cursor = getActivity().getContentResolver().query(WoodminContract.CustomerEntry.CONTENT_URI,
                        COUNT_PROJECTION,
                        null,
                        null,
                        null);
                if(cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {
                            int count = cursor.getInt(COLUMN_COUNT);
                            mValues[2].setCount(count);
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                }

                mAdapter.notifyDataSetChanged();

                if (!mUserLearnedDrawer) {
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().supportInvalidateOptionsMenu();
            }
        };

        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.drawer_fragment_menu, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(LOG_TAG, "onCreateLoader");

        CursorLoader cursorLoader;
        switch (id) {
            case SHOP_LOADER: {
                Uri shopUri = WoodminContract.ShopEntry.CONTENT_URI;
                String sortOrder = WoodminContract.ShopEntry._ID + " ASC";
                cursorLoader = new CursorLoader(
                        getActivity().getApplicationContext(),
                        shopUri,
                        SHOP_PROJECTION,
                        null,
                        null,
                        sortOrder);
                }
                break;
            default:
                cursorLoader = null;
                break;
        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.d(LOG_TAG, "onLoadFinished");
        int count = 0;
        switch (cursorLoader.getId()) {
            case SHOP_LOADER: {
                    if (cursor.moveToFirst()) {
                        do {
                            String name = cursor.getString(SHOP_COLUMN_NAME);
                            String description = cursor.getString(SHOP_COLUMN_DESCRIPTION);

                            if(name!=null){
                                TextView shopName = (TextView) mDrawerListView.findViewById(R.id.name);
                                shopName.setText(name);
                            }
                            if(description!=null){
                                TextView resume = (TextView) mDrawerListView.findViewById(R.id.resume);
                                resume.setText(description);
                            }
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                }
                break;
            default:
                break;
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        Log.d(LOG_TAG, "onLoaderReset");
        switch (cursorLoader.getId()) {
            case SHOP_LOADER: {
                    TextView shopName = (TextView) mDrawerListView.findViewById(R.id.name);
                    shopName.setText("");
                    TextView resume = (TextView) mDrawerListView.findViewById(R.id.resume);
                    resume.setText("");
                }
                break;
            default:
                break;
        }
        mAdapter.notifyDataSetChanged();
    }

}
