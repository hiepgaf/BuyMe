package app.bennsandoval.com.woodmin.fragments;

import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.actions.SearchIntents;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import app.bennsandoval.com.woodmin.R;
import app.bennsandoval.com.woodmin.activities.MainActivity;
import app.bennsandoval.com.woodmin.activities.OrderNew;
import app.bennsandoval.com.woodmin.activities.OrderDetail;
import app.bennsandoval.com.woodmin.adapters.OrderAdapter;
import app.bennsandoval.com.woodmin.data.WoodminContract;
import app.bennsandoval.com.woodmin.sync.WoodminSyncAdapter;
import app.bennsandoval.com.woodmin.utilities.Utility;

public class OrdersFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SearchView.OnQueryTextListener {

    private final String LOG_TAG = OrdersFragment.class.getSimpleName();

    private static final String ARG_SECTION_NUMBER = "section_number";
    private OrderAdapter mAdapter;

    private SwipeRefreshLayout mSwipeLayout;
    private RecyclerView mRecyclerView;

    private static final int ORDER_LOADER = 100;
    private static final String[] ORDER_PROJECTION = {
            WoodminContract.OrdersEntry._ID,
            WoodminContract.OrdersEntry.COLUMN_ID,
            WoodminContract.OrdersEntry.COLUMN_JSON,
            WoodminContract.OrdersEntry.COLUMN_CREATED_AT
    };

    private SearchView mSearchView;
    private String mQuery;

    public static OrdersFragment newInstance(int sectionNumber) {
        OrdersFragment fragment = new OrdersFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public OrdersFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        onNewIntent(getActivity().getIntent());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_orders, container, false);

        View.OnClickListener onClickListener = new View.OnClickListener(){
            @Override
            public void onClick(final View view) {
                int position = mRecyclerView.getChildPosition(view);
                mAdapter.getCursor().moveToPosition(position);
                int idSelected = mAdapter.getCursor().getInt(mAdapter.getCursor().getColumnIndex(WoodminContract.OrdersEntry.COLUMN_ID));

                Intent intent = new Intent(getActivity(), OrderDetail.class);
                intent.putExtra("order", idSelected);
                startActivity(intent);
            }
        };

        mAdapter = new OrderAdapter(getActivity().getApplicationContext(), R.layout.fragment_order_list_item, null, onClickListener);
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.list_orders);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setAdapter(mAdapter);

        getActivity().getSupportLoaderManager().initLoader(ORDER_LOADER, null, this);

        mSwipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                WoodminSyncAdapter.syncImmediately(getActivity());
            }
        });

        mSwipeLayout.setColorSchemeResources(R.color.holo_blue_bright,
                R.color.holo_green_light,
                R.color.holo_orange_light,
                R.color.holo_red_light);

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView view, int scrollState) {
            }

            @Override
            public void onScrolled(RecyclerView view, int dx, int dy) {
                boolean enable = false;
                if (view != null && view.getChildCount() > 0) {
                    boolean topOfFirstItemVisible = view.getChildAt(0).getTop() == 0;
                    enable = topOfFirstItemVisible;
                }
                mSwipeLayout.setEnabled(enable);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        if(fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent orderIntent = new Intent(getActivity(), OrderNew.class);
                    startActivity(orderIntent);

                }
            });
        }

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //menu.clear();
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.order_fragment_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        if (mSearchView != null) {
            List<SearchableInfo> searchables = searchManager.getSearchablesInGlobalSearch();
            SearchableInfo info = searchManager.getSearchableInfo(getActivity().getComponentName());
            for (SearchableInfo inf : searchables) {
                if (inf.getSuggestAuthority() != null && inf.getSuggestAuthority().startsWith("applications")) {
                    info = inf;
                }
            }
            mSearchView.setSearchableInfo(info);
            mSearchView.setOnQueryTextListener(this);
            mSearchView.setQueryHint(getActivity().getString(R.string.order_title_search));

            if(mQuery != null && mQuery.length() > 0) {
                mSearchView.setQuery(mQuery, true);
                mSearchView.setIconifiedByDefault(false);
                mSearchView.performClick();
                mSearchView.requestFocus();
            } else {
                mSearchView.setIconifiedByDefault(true);
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(LOG_TAG, "onCreateLoader");

        String sortOrder = WoodminContract.OrdersEntry.COLUMN_ID + " DESC";
        CursorLoader cursorLoader;
        Uri ordersUri = WoodminContract.OrdersEntry.CONTENT_URI;
        switch (id) {
            case ORDER_LOADER:
                if(mQuery != null && mQuery.length()>0) {
                    if(mQuery.toLowerCase().equals("ship")) {

                        Date now = new Date();
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(now);
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE,0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                        Date startDay = calendar.getTime();

                        String query = WoodminContract.OrdersEntry.COLUMN_STATUS + " = ? AND " + WoodminContract.OrdersEntry.COLUMN_UPDATED_AT + " BETWEEN ? AND ?";
                        String[] parameters = new String[]{"completed", WoodminContract.getDbDateString(startDay), WoodminContract.getDbDateString(now)};
                        cursorLoader = new CursorLoader(
                                getActivity().getApplicationContext(),
                                ordersUri,
                                ORDER_PROJECTION,
                                query,
                                parameters,
                                sortOrder);

                        Toast.makeText(getContext(), getString(R.string.toast_search_ship, WoodminContract.getDbDateString(startDay)), Toast.LENGTH_LONG).show();

                    } else {
                        String query = WoodminContract.OrdersEntry.COLUMN_ORDER_NUMBER + " LIKE ? OR  " +
                                WoodminContract.OrdersEntry.COLUMN_CUSTOMER_FIRST_NAME + " LIKE ? OR  " +
                                WoodminContract.OrdersEntry.COLUMN_CUSTOMER_LAST_NAME + " LIKE ? OR  " +
                                WoodminContract.OrdersEntry.COLUMN_BILLING_FIRST_NAME + " LIKE ? OR  " +
                                WoodminContract.OrdersEntry.COLUMN_BILLING_FIRST_NAME + " LIKE ? OR  " +
                                WoodminContract.OrdersEntry.COLUMN_BILLING_FIRST_NAME + " LIKE ?" ;
                        String[] parameters = new String[]{
                                "%" + mQuery + "%",
                                "%" + mQuery + "%",
                                "%" + mQuery + "%",
                                "%" + mQuery + "%",
                                "%" + mQuery + "%" };
                        cursorLoader = new CursorLoader(
                                getActivity().getApplicationContext(),
                                ordersUri,
                                ORDER_PROJECTION,
                                query,
                                parameters,
                                sortOrder);
                    }
                } else {

                    Date today = new Date();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(today);
                    calendar.add(Calendar.MONTH, -1);
                    Date oneMonthsBack = calendar.getTime();

                    //String query = WoodminContract.OrdersEntry.COLUMN_ENABLE + " = ? AND " + WoodminContract.OrdersEntry.COLUMN_CREATED_AT + " BETWEEN ? AND ?";
                    //String[] parameters = new String[]{ String.valueOf("1"), startDate, endDate };
                    String query = WoodminContract.OrdersEntry.COLUMN_CREATED_AT + " BETWEEN ? AND ?";
                    String[] parameters = new String[]{WoodminContract.getDbDateString(oneMonthsBack), WoodminContract.getDbDateString(today)};
                    cursorLoader = new CursorLoader(
                            getActivity().getApplicationContext(),
                            ordersUri,
                            ORDER_PROJECTION,
                            query,
                            parameters,
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
        switch (cursorLoader.getId()) {
            case ORDER_LOADER:
                if(mSwipeLayout != null){
                    mSwipeLayout.setRefreshing(false);
                }
                Log.d(LOG_TAG, "Orders " + cursor.getCount());
                mAdapter.changeCursor(cursor);
                break;
            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        Log.d(LOG_TAG, "onLoaderReset");
        switch (cursorLoader.getId()) {
            case ORDER_LOADER:
                mAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mQuery = query;
        doSearch();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mQuery = newText;
        doSearch();
        return true;
    }

    protected void onNewIntent(Intent intent) {
        String action = intent.getAction();
        if (action != null && (action.equals(Intent.ACTION_SEARCH) || action.equals(SearchIntents.ACTION_SEARCH))) {
            mQuery = intent.getStringExtra(SearchManager.QUERY);
            mQuery = mQuery.replace(getString(R.string.order_voice_search)+" ","");
        }
    }

    private void doSearch() {
        getActivity().getSupportLoaderManager().restartLoader(ORDER_LOADER, null, this);
        getActivity().getSupportLoaderManager().getLoader(ORDER_LOADER).forceLoad();
    }

}
