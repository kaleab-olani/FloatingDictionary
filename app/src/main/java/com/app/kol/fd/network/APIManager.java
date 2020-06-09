package com.app.kol.fd.network;

import android.os.AsyncTask;

import javax.net.ssl.HttpsURLConnection;

public class APIManager extends AsyncTask {
    String baseURL = "https://dictionaryapi.com/account";
    String definationKey = "5939c7f7-1962-4205-b68e-0bcbff399ae6";
    String query = "";
    protected String attachKey(String url, String key){
        return url + "?key="+key;
    }
    protected String attachWord(String url, String word){
        return url + "/="+word;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
    @Override
    protected Object doInBackground(Object[] objects) {
        //query = attachKey(attachWord(baseURL, word),definationKey);
        return null;
    }


    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }
}
