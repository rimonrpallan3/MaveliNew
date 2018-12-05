package com.mavelinetworks.mavelideals.utils;


import android.os.AsyncTask;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.mavelinetworks.mavelideals.unbescape.html.HtmlEscape;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Droideve on 1/3/2017.
 */

public class TextUtils {

    public static class decodeHtml extends AsyncTask<String,String,String> {

        private TextView view;

        public decodeHtml(View v){
            this.view = (TextView) v;
        }

        @Override
        protected void onPostExecute(final String text) {
            super.onPostExecute(text);
            view.setText(Html.fromHtml(text));
            //eventData.setDescription(text);
        }

        @Override
        protected String doInBackground(String... params) {

            return HtmlEscape.unescapeHtml(params[0]);
        }
    }


    public static List<String> parseLanguagesToList(String str){

        List<String> result = new ArrayList<>();
        try {
            JSONObject json = new JSONObject(str);
            for (int i = 0; i < json.length(); i++) {
                result.add(json.getString(String.valueOf(i)));
            }

            return result;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }






    public static String parseLanguagesListToHtml(List<String> list){
        StringBuilder sb = new StringBuilder();

        for(int i=0;i<list.size();i++){

            sb.append(list.get(i));

            if(list.size()-2>i)
                sb.append(", ");
            else if(list.size()-2==i){
                sb.append(" & ");
            }

        }

        return sb.toString();
    }

    /**
     * method to escape string
     *
     * @param string this is parameter for escapeJavaString  method
     * @return return value
     */
    public static String escapeJavaString(String string) {

        StringBuilder sb = new StringBuilder(string.length());

        for (int i = 0; i < string.length(); i++) {
            char ch = string.charAt(i);
            if (ch == '\\') {
                char nextChar = (i == string.length() - 1) ? '\\' : string.charAt(i + 1);
                // Octal escape?
                if (nextChar >= '0' && nextChar <= '7') {
                    String code = "" + nextChar;
                    i++;
                    if ((i < string.length() - 1) && string.charAt(i + 1) >= '0'
                            && string.charAt(i + 1) <= '7') {
                        code += string.charAt(i + 1);
                        i++;
                        if ((i < string.length() - 1) && string.charAt(i + 1) >= '0'
                                && string.charAt(i + 1) <= '7') {
                            code += string.charAt(i + 1);
                            i++;
                        }
                    }
                    sb.append((char) Integer.parseInt(code, 8));
                    continue;
                }
                switch (nextChar) {
                    case '\\':
                        ch = '\\';
                        break;
                    case 'b':
                        ch = '\b';
                        break;
                    case 'f':
                        ch = '\f';
                        break;
                    case 'n':
                        ch = '\n';
                        break;
                    case 'r':
                        ch = '\r';
                        break;
                    case 't':
                        ch = '\t';
                        break;
                    case '\"':
                        ch = '\"';
                        break;
                    case '\'':
                        ch = '\'';
                        break;
                    case 'u':
                        if (i >= string.length() - 5) {
                            ch = 'u';
                            break;
                        }
                        int code = Integer.parseInt(
                                "" + string.charAt(i + 2) + string.charAt(i + 3)
                                        + string.charAt(i + 4) + string.charAt(i + 5), 16);
                        sb.append(Character.toChars(code));
                        i += 5;
                        continue;
                }
                i++;
            }
            sb.append(ch);
        }
        return sb.toString();
    }
}
