package com.mavelinetworks.mavelideals.controllers.sessions;

/**
 * Created by Droideve on 11/17/2016.
 */

public class LocationController {

//    public static void saveUserLocation(Activity activity){
//
//        RequestQueue queue = VolleySingleton.getInstance(activity).getRequestQueue();
//
//        queue = VolleySingleton.getInstance(activity).getRequestQueue();
//
//        User mUser = SessionsController.getSession().getUser();
//
//        GPStracker gps = new GPStracker(activity);
//        gps.canGetLocation();
//
//        if(mUser!=null && gps.canGetLocation() && gps.getLatitude()!=0) {
//
//            final int user_id = mUser.getId();
//            final double lat =  gps.getLatitude();
//            final double lng =  gps.getLongitude();
//
//            SimpleRequest request = new SimpleRequest(Request.Method.POST,
//                    Constances.API.API_SET_USER_LOCATION, new Response.Listener<String>() {
//                @Override
//                public void onResponse(final String response) {
//
//                    try {
//
//                        if(AppContext.DEBUG)
//                            Log.e("positionResponse",response);
//
//                        JSONObject js = new JSONObject(response);
//                        final Parser mParser = new Parser(js);
//                        int success = Integer.parseInt(mParser.getStringAttr(Tags.SUCCESS));
//
//                        if(success==1){
//
//                        }
//
//
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//
//                        //show loadToast with error
//                    }
//
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//
//                    if(AppContext.DEBUG)
//                        Log.e("ERROR", error.toString());
//
//
//
//                }
//            }) {
//
//                @Override
//                protected Map<String, String> getParams() {
//                    Map<String, String> params = new HashMap<String, String>();
//
//                    params.put("user_id", String.valueOf(user_id));
//                    params.put("lat", String.valueOf(lat));
//                    params.put("lng", String.valueOf(lng));
//
//                    return Security.cryptedData(params);
//                }
//
//            };
//
//
//            request.setRetryPolicy(new DefaultRetryPolicy(SimpleRequest.TIME_OUT,
//                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//
//            queue.add(request);
//
//        }
//
//
//    }

}
