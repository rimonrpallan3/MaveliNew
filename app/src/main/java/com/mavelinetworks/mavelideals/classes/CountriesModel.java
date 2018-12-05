package com.mavelinetworks.mavelideals.classes;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Abderrahim El imame on 8/13/16.
 *
 * @Email : abderrahim.elimame@gmail.com
 * @Author : https://twitter.com/bencherif_el
 */

public class CountriesModel extends RealmObject {

    @PrimaryKey
    @Expose
    private String name;
    @Expose
    private String dial_code;
    @Expose
    private String code;

    public static ArrayList<String> countriesToJson(List<CountriesModel> list){
        ArrayList<String> listStrings = new ArrayList<>();
        for(CountriesModel country : list){

            listStrings.add(country.getName()+" ("+country.getCode()+")");

        }

        return listStrings;
    }

    public String getDial_code() {
        return dial_code;
    }

    public void setDial_code(String dial_code) {
        this.dial_code = dial_code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
