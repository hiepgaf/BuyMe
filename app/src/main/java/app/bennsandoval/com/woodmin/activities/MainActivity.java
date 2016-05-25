package app.bennsandoval.com.woodmin.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;

import com.google.android.gms.actions.SearchIntents;

import java.util.List;

import app.bennsandoval.com.woodmin.R;
import app.bennsandoval.com.woodmin.data.WoodminContract;
import app.bennsandoval.com.woodmin.fragments.CustomersFragment;
import app.bennsandoval.com.woodmin.fragments.NavigationDrawerFragment;
import app.bennsandoval.com.woodmin.fragments.OrdersFragment;
import app.bennsandoval.com.woodmin.fragments.ProductsFragment;
import app.bennsandoval.com.woodmin.fragments.ResumeFragment;
import app.bennsandoval.com.woodmin.interfaces.NavigationDrawerCallbacks;
import app.bennsandoval.com.woodmin.sync.WoodminSyncAdapter;
import app.bennsandoval.com.woodmin.utilities.Utility;

public class MainActivity extends AppCompatActivity implements NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

        WoodminSyncAdapter.initializeSyncAdapter(getApplicationContext());
        //WoodminSyncAdapter.syncImmediately(getApplicationContext());

        //Search
        //Intent intentHeader= new Intent(getApplicationContext(), HeadInfoService.class);
        //intentHeader.putExtra("search","3339024328");
        //startService(intentHeader);

        processIntent(getIntent());
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> allFragments = fragmentManager.getFragments();
        switch (position) {
            case 0:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, ResumeFragment.newInstance(position),"section")
                        .commit();
                /*
                mTitle = getString(R.string.app_name);
                if (allFragments != null && allFragments.size() > 0) {
                    for (Fragment frag : allFragments) {
                        if (frag!= null && frag.getTag()!= null && frag.getTag().equals("section")){
                            fragmentManager.beginTransaction().remove(frag).commit();
                        }
                    }
                }
                */
                break;
            case 1:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, OrdersFragment.newInstance(position),"section")
                        .commit();
                break;
            case 2:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, ProductsFragment.newInstance(position),"section")
                        .commit();
                break;
            case 3:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, CustomersFragment.newInstance(position),"section")
                        .commit();
                break;
            case 4:
                mTitle = getString(R.string.title_section5);

                if (allFragments != null && allFragments.size() > 0) {
                    for (Fragment frag : allFragments) {
                        if (frag!= null && frag.getTag()!= null && frag.getTag().equals("section")){
                            fragmentManager.beginTransaction().remove(frag).commit();
                        }
                    }
                }

                //Clear database
                getApplicationContext().getContentResolver().delete(WoodminContract.CustomerEntry.CONTENT_URI, null, null);
                getApplicationContext().getContentResolver().delete(WoodminContract.ProductEntry.CONTENT_URI, null, null);
                getApplicationContext().getContentResolver().delete(WoodminContract.ShopEntry.CONTENT_URI, null, null);
                getApplicationContext().getContentResolver().delete(WoodminContract.OrdersEntry.CONTENT_URI, null, null);

                //Remove Sync
                WoodminSyncAdapter.disablePeriodSync(getApplicationContext());
                WoodminSyncAdapter.removeAccount(getApplicationContext());

                //Remove Preferences
                Utility.setPreferredServer(getApplicationContext(), null);
                Utility.setPreferredLastSync(getApplicationContext(), 0L);
                Utility.setPreferredUserSecret(getApplicationContext(), null, null);
                Utility.setPreferredShoppingCard(getApplicationContext(), null);

                Intent main = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(main);
                finish();

                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.main_activity_menu, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void processIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = intent.getAction();
        if (action != null && (action.equals(Intent.ACTION_SEARCH) || action.equals(SearchIntents.ACTION_SEARCH))) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if(query != null && query.length()>0){
                FragmentManager fragmentManager = getSupportFragmentManager();
                if(query.toLowerCase().contains(getString(R.string.product_voice_search).toLowerCase())){
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, ProductsFragment.newInstance(2))
                            .commit();
                } else if(query.toLowerCase().contains(getString(R.string.customer_voice_search).toLowerCase())){
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, CustomersFragment.newInstance(1))
                            .commit();
                } else {
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, OrdersFragment.newInstance(1))
                            .commit();
                }
            }
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 0:
                mTitle = getString(R.string.app_name);
                break;
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = getString(R.string.title_section5);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

}
