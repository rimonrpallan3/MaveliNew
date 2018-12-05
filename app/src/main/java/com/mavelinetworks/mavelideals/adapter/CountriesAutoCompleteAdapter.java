package com.mavelinetworks.mavelideals.adapter;


import android.support.v4.app.FragmentActivity;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.mavelinetworks.mavelideals.classes.CountriesModel;

import java.util.ArrayList;
import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by Droideve on 8/14/2016.
 */

public class CountriesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {

    //private static final String PLACES_API_BASE = "http://bestplaces.droideve.com/1.0/webservice/getGooglePlaces";

    public ArrayList<String> resultList;
    public ArrayList<String> getResultList() {
        return resultList;
    }
    public void setResultList(ArrayList<String> resultList) {
        this.resultList = resultList;
    }

    private FragmentActivity mContext;
    private int parent=0;


    private List<String> codeCountries;

    public List<String> getCodeCountries() {
        return codeCountries;
    }

    public void setCountries(List<String> countries) {
        this.codeCountries = countries;
    }


    public CountriesAutoCompleteAdapter(FragmentActivity context, int textViewResourceId) {
        super(context, textViewResourceId);
        mContext = context;
        resultList = new ArrayList<>();
        codeCountries = new ArrayList<>();
    }


    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public String getItem(int index) {

        return resultList.get(index);
    }

    @Override
    public Filter getFilter() {


        Filter filter = new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    // Retrieve the autocomplete results.

                    resultList = getList(constraint.toString());
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }

                return filterResults;

            }

            @Override
            protected void publishResults(CharSequence constraint, final FilterResults results) {

                results.values = resultList;
                results.count = resultList.size();

                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                }else{
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }


  private ArrayList<String> getList(String string){

      ArrayList<String> list = new ArrayList<>();
      Realm realm = Realm.getDefaultInstance();

      RealmResults<CountriesModel> result = realm.where(CountriesModel.class)
              .contains("name",string, Case.INSENSITIVE)
              .or()
              .contains("name",string, Case.SENSITIVE)
              .findAllSorted("name", Sort.ASCENDING);


      codeCountries.clear();
      for(CountriesModel country : result){
          codeCountries.add(country.getDial_code());
          list.add(country.getName()+" ( "+country.getDial_code()+" )");
      }

      realm.close();

      resultList = list;
      return list;
  }


}