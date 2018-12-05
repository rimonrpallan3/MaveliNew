package com.mavelinetworks.mavelideals.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mavelinetworks.mavelideals.R;
import com.mavelinetworks.mavelideals.adapter.StoreReviewsAdapter;
import com.mavelinetworks.mavelideals.appconfig.Constances;
import com.mavelinetworks.mavelideals.classes.Guest;
import com.mavelinetworks.mavelideals.classes.Review;
import com.mavelinetworks.mavelideals.classes.Store;
import com.mavelinetworks.mavelideals.classes.User;
import com.mavelinetworks.mavelideals.controllers.ReviewController;
import com.mavelinetworks.mavelideals.controllers.sessions.GuestController;
import com.mavelinetworks.mavelideals.controllers.sessions.SessionsController;
import com.mavelinetworks.mavelideals.controllers.stores.ReviewsController;
import com.mavelinetworks.mavelideals.controllers.stores.StoreController;
import com.mavelinetworks.mavelideals.network.ServiceHandler;
import com.mavelinetworks.mavelideals.network.VolleySingleton;
import com.mavelinetworks.mavelideals.network.api_request.SimpleRequest;
import com.mavelinetworks.mavelideals.parser.api_parser.ReviewParser;
import com.mavelinetworks.mavelideals.utils.Utils;
import com.norbsoft.typefacehelper.TypefaceHelper;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmList;
import io.techery.properratingbar.ProperRatingBar;

import static com.mavelinetworks.mavelideals.appconfig.AppConfig.APP_DEBUG;

/**
 * Created by Droideve on 11/12/2017.
 */

public class StoreReviewsFragment extends android.support.v4.app.Fragment {

    private int store_id;

    private LinearLayout emptyLayout;
    private LinearLayout loadingLayout;
    private LinearLayout containerLayout;
    private LinearLayout rateBtn;
    private  List<Review> listReviews;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_store_reviews, container, false);

        TypefaceHelper.typeface(rootView);

        emptyLayout = (LinearLayout) rootView.findViewById(R.id.emptyLayout);
        loadingLayout = (LinearLayout) rootView.findViewById(R.id.loadingLayout);
        containerLayout = (LinearLayout) rootView.findViewById(R.id.container);
        rateBtn = (LinearLayout) rootView.findViewById(R.id.rateBtn);
        try {
            store_id = getArguments().getInt("store_id",0);
            if(APP_DEBUG)
                Log.e("_4_store_id", String.valueOf(store_id));


        }catch (Exception e){
            return rootView;
        }

        List<Review> listReviews = ReviewsController.findReviewyStoreId(store_id);
        this.listReviews = listReviews;
        reloadReviews(rootView,listReviews);

        //do update from server
        getComment(rootView);


        return rootView;
    }




    private void reloadReviews(View rootView,List<Review> listReviews){


        if(listReviews.size()>0){

            containerLayout.setVisibility(View.VISIBLE);
            loadingLayout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.GONE);

            StoreReviewsAdapter
                    .newInstance(getActivity()).load(listReviews)
                    .inflate(R.layout.item_store_review)
                    .into((LinearLayout) rootView.findViewById(R.id.container));

        }else{

            containerLayout.setVisibility(View.GONE);
            loadingLayout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.GONE);

        }

        rateBtn.setVisibility(View.VISIBLE);
        rateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ReviewController.isRated(store_id))
                    Toast.makeText(getActivity(),getString(R.string.you_ve_already_reviewd),Toast.LENGTH_LONG).show();
                else
                    showRateDialog();

            }
        });

    }



    public void getComment(final View rootview) {

        RequestQueue queue = VolleySingleton.getInstance(getActivity()).getRequestQueue();

        if(listReviews.size()==0){
            containerLayout.setVisibility(View.GONE);
            loadingLayout.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
        }


        final String mac_adr = ServiceHandler.getMacAddr();

        SimpleRequest request = new SimpleRequest(Request.Method.POST,
                Constances.API.API_USER_GET_REVIEWS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{

                    if(APP_DEBUG)
                        Log.e("_Comments", response.toString());
                    System.out.println("API_USER_GET_REVIEWS "+response.toString());


                    JSONObject jsonObject = new JSONObject(response);
                    //Log.e("response",response);

                    final ReviewParser commentParser = new ReviewParser(jsonObject);
                    final RealmList<Review> list = commentParser.getComments();

                    final int count = commentParser.getIntArg("count");

                    if(list.size()>0){

                        ReviewsController.deleteAllReviews(store_id);

                        final Store store = StoreController.findStoreById(store_id);

                        if(store!=null){
                            Realm realm = Realm.getDefaultInstance();
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    store.setNbr_votes(String.valueOf(count));
                                    realm.copyToRealmOrUpdate(store);
                                }
                            });
                        }

                        ReviewsController.insertReviews(list);
                        reloadReviews(rootview,list);

                        containerLayout.setVisibility(View.VISIBLE);
                        loadingLayout.setVisibility(View.GONE);
                        emptyLayout.setVisibility(View.GONE);

                    }else {

                        containerLayout.setVisibility(View.GONE);
                        loadingLayout.setVisibility(View.GONE);
                        emptyLayout.setVisibility(View.VISIBLE);

                    }



                }catch (JSONException e){
                    //send a rapport to support
                    e.printStackTrace();

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                if(APP_DEBUG)
                    Log.e("ERROR", error.toString());


            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("store_id",store_id+"");
                params.put("limit", String.valueOf(7));
                params.put("page", String.valueOf(1));
                params.put("mac_adr", mac_adr);

                if(APP_DEBUG)
                    Log.e("listReviewsRequested",""+params.toString());

                return params;
            }

        };


        request.setRetryPolicy(new DefaultRetryPolicy(SimpleRequest.TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);



    }


    public void sendReview(final int rating, String pseudo, String review, int guest_id, final Dialog mDialog) {

        RequestQueue queue = VolleySingleton.getInstance(getActivity()).getRequestQueue();
        queue = VolleySingleton.getInstance(getActivity()).getRequestQueue();


        final LinearLayout progress = (LinearLayout) mDialog.findViewById(R.id.progressLayout);
        final LinearLayout mainLayout = (LinearLayout) mDialog.findViewById(R.id.mainLayout);

        mainLayout.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);

        if(pseudo.trim().trim().equals(""))
            pseudo = "Guest-"+guest_id;

        if(review.trim().trim().equals(""))
            review = " ";


        final Map<String, String> params = new HashMap<String, String>();

        params.put("store_id",store_id + "");
        params.put("rate", rating+"");
        params.put("review", review+"");
        params.put("pseudo", pseudo+"");
        params.put("guest_id", guest_id+"");
        try {
            params.put("token", Utils.getToken(getActivity()));
        }catch (Exception e){

        }
        params.put("mac_adr", ServiceHandler.getMacAddr() );
        params.put("limit", "7");

        SimpleRequest request = new SimpleRequest(Request.Method.POST,
                Constances.API.API_RATING_STORE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    try {
                        Toast.makeText(getActivity(), getString(R.string.thankYou), Toast.LENGTH_LONG).show();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    mainLayout.setVisibility(View.GONE);
                    progress.setVisibility(View.VISIBLE);

                    JSONObject jso = new JSONObject(response);
                    int success = jso.getInt("success");
                    if (success == 1) {

                        final Store store = StoreController.findStoreById(store_id);
                        if(store!=null){
                            Realm realm = Realm.getDefaultInstance();
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    store.setNbr_votes(String.valueOf((Integer.parseInt(store.getNbr_votes())+1)));
                                    realm.copyToRealmOrUpdate(store);
                                }
                            });
                        }

                    } else {

                        mainLayout.setVisibility(View.VISIBLE);
                        progress.setVisibility(View.GONE);

                    }

                    //add view
                    if(mDialog.isShowing())
                        mDialog.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (APP_DEBUG) Log.e("ERROR", error.toString());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {

                return params;
            }

        };


        request.setRetryPolicy(new DefaultRetryPolicy(SimpleRequest.TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);

    }


    private void showRateDialog(){

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_rate);

        final ProperRatingBar ratingbar = (ProperRatingBar) dialog.findViewById(R.id.lowerRatingBar);
        final MaterialEditText review = (MaterialEditText) dialog.findViewById(R.id.review);
        final MaterialEditText pseudo = (MaterialEditText) dialog.findViewById(R.id.pseudo);
        final TextView addReview = (TextView) dialog.findViewById(R.id.addReview);

        Utils.setFont(getContext(),review, Constances.Fonts.BOLD);
        Utils.setFont(getContext(),pseudo, Constances.Fonts.BOLD);
        Utils.setFont(getContext(),addReview, Constances.Fonts.BOLD);



        if(SessionsController.isLogged()) {
            User user = SessionsController.getSession().getUser();
            pseudo.setText(user.getUsername());
            pseudo.setEnabled(false);
        }

        final Guest guest = GuestController.getGuest();
        int gid = 0;
        if(guest!=null)
            gid = guest.getId();

        final int finalGid = gid;
        addReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ratingbar.getRating()>0){
                    if(true){
                        //send review

                        sendReview(
                                ratingbar.getRating(),
                                pseudo.getText().toString().trim(),
                                review.getText().toString().trim(),
                                finalGid,
                                dialog
                        );

                    }else {
                        Toast.makeText(getActivity(),getString(R.string.pleaseWriteReview),Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(getActivity(),getString(R.string.selectRating),Toast.LENGTH_LONG).show();
                }
            }
        });


        dialog.show();

    }

}
