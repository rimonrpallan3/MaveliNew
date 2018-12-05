package com.mavelinetworks.mavelideals.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.mavelinetworks.mavelideals.AppController;
import com.mavelinetworks.mavelideals.R;
import com.mavelinetworks.mavelideals.activities.MainActivity;
import com.mavelinetworks.mavelideals.apis.SlidingTabLayout;
import com.mavelinetworks.mavelideals.appconfig.AppConfig;
import com.mavelinetworks.mavelideals.appconfig.Constances;
import com.mavelinetworks.mavelideals.classes.Category;
import com.mavelinetworks.mavelideals.controllers.sessions.SessionsController;
import com.mavelinetworks.mavelideals.dtmessenger.MessengerHelper;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;

import java.util.ArrayList;
import java.util.List;


public class MainFragment extends Fragment {

    public final static String TAG = "mainfragment";
    private static final int REQUEST_CODE = 1;
    private static ViewPager pager;
    private ViewPagerAdapter adapter;
    private SlidingTabLayout tabs;
    private FloatingActionButton mFab;
    private FragmentActivity myContext;


    public static ViewPager getPager(){
        return pager;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter =  new ViewPagerAdapter(myContext.getSupportFragmentManager(),
                AppConfig.TabsConfig,AppConfig.TabsConfig.size());

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) rootView.findViewById(R.id.pager);
        pager.setAdapter(adapter);

        pager.setOffscreenPageLimit((Constances.initConfig.Numboftabs));

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) rootView.findViewById(R.id.tabs);
        tabs.setCustomTabView(R.layout.tabrow,R.id.tabrow,R.id.notify);
        tabs.setViewPager(pager);
        tabs.setHorizontalFadingEdgeEnabled(true);



        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width
        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });




        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);

         /*
        *   SUPPORT RTL
        */
        if(AppController.isRTL()){
            pager.setCurrentItem(AppConfig.TabsConfig.size()-1);
        }

        try {
            boolean chat = getActivity().getIntent().getExtras().getBoolean("chat",false);
            if(chat==true){
                System.out.println("*****************Rimon :No Tab - : "+Constances.initConfig.Numboftabs);

                for (int i=0;i<Constances.initConfig.Numboftabs;i++){
                    if(AppConfig.TabsConfig.get(i).getNumCat()==-1){
                        pager.setCurrentItem(i);
                    }
                }
            }

        }catch (Exception e){

        }



        if(Constances.initConfig.Numboftabs==1){
            tabs.setVisibility(View.GONE);
        }

        tabs.setDistributeEvenly(false);

        return rootView;
    }

    public void setCurrentFragment(int position){
        pager.setCurrentItem(position);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        myContext = (FragmentActivity) activity;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        updateBadge();
    }


    private void updateBadge(){

        if (MessengerHelper.NbrMessagesManager.getNbrTotalMessages() > 0) {

            ActionItemBadge.update(getActivity(), MainActivity.mainMenu.findItem(R.id.item_samplebadge),
                    CommunityMaterial.Icon.cmd_bell_ring_outline,
                    ActionItemBadge.BadgeStyles.RED,
                    MessengerHelper.NbrMessagesManager.getNbrTotalMessages());

        } else {

            ActionItemBadge.hide(MainActivity.mainMenu.findItem(R.id.item_samplebadge));

        }
    }



    public class ViewPagerAdapter extends FragmentStatePagerAdapter {

        List<Category> tabsItems; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
        int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created

        List<Integer> nbrNotifis;

        // Build a Constructor and assign the passed Values to appropriate values in the class
        public ViewPagerAdapter(FragmentManager fm, List<Category> tabs, int mNumbOfTabsumb) {
            super(fm);

            this.tabsItems = tabs;
            this.NumbOfTabs = mNumbOfTabsumb;
            nbrNotifis =new ArrayList<>();


            for (int i=0;i<NumbOfTabs;i++){
                nbrNotifis.add(0);
            }
        }

        //This method return the fragment for the every position in the View Pager
        @Override
        public android.support.v4.app.Fragment getItem(int position) {

            android.support.v4.app.Fragment fragment = null;

            /*((AppCompatActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(
                            Color.parseColor(Constances.initConfig.Colors[position]))
            );*/


            ListStoresFragment frag0= null;


            if(AppConfig.APP_DEBUG)
             Log.e("tab", String.valueOf(AppConfig.TabsConfig.get(position).getType()));

            if(AppConfig.TabsConfig.get(position).getType() == Constances.initConfig.Tabs.EVENTS)
            {
                fragment = new ListEventFragment();

            }else if(AppConfig.TabsConfig.get(position).getType() == Constances.initConfig.Tabs.CHAT)
            {
                if(SessionsController.isLogged()){
                    fragment = new InboxFragment();
                }else
                    fragment = new LoginFragment();

            } else if(AppConfig.TabsConfig.get(position).getType()==Constances.initConfig.Tabs.NEARBY_OFFERS){

                fragment = new ListOffersFragment();
                Bundle b = new Bundle();
                fragment.setArguments(b);

            } else if(AppConfig.TabsConfig.get(position).getType()>0){

                fragment = new ListStoresFragment();
                Bundle b = new Bundle();
                b.putInt("category",AppConfig.TabsConfig.get(position).getNumCat());
                fragment.setArguments(b);

            }else{

                fragment = new ListStoresFragment();
                Bundle b = new Bundle();
                b.putInt("category",AppConfig.TabsConfig.get(position).getNumCat());
                fragment.setArguments(b);
            }


            return fragment;

            //return fragment;
        }

        // This method return the titles for the Tabs in the Tab Strip

        @Override
        public CharSequence getPageTitle(int position) {
            return tabsItems.get(position).getNameCat();
        }


        public int getNotifs(int position) {
            return nbrNotifis.get(position);
        }

        public int getimage(int position){
            return Constances.initConfig.ListCats.get(position).getIcon();
        }


        // This method return the Number of tabs for the tabs Strip

        @Override
        public int getCount() {
            return NumbOfTabs;
        }


    }



}
