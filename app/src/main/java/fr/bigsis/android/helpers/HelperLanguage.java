package fr.bigsis.android.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Locale;

public class HelperLanguage {
    protected static Locale myLocale;


    // change the current language with lang
    public static void changeLang(String lang, String opt, Context context)
    {
        if (lang.equalsIgnoreCase(""))
            return;
        myLocale = new Locale(lang,opt);
        saveLocale(lang, opt, context);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

    // save the new language lang
    public static void saveLocale(String lang, String opt, Context context)
    {
        String langPref = "Language";
        String optPref = "Option";
        SharedPreferences prefs = context.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(langPref, lang);
        editor.putString(optPref, opt);
        editor.apply();
    }

    // load the locale language
    public static void loadLocale(Context context)
    {
        String langPref = "Language";
        String langOpt = "Option";
        SharedPreferences prefs = context.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        String language = prefs.getString(langPref, "");
        String option = prefs.getString(langOpt, "");
        Log.d("HelperLanguage ", "Language : " + language +" , Option : "+option);
        changeLang(language, option, context);
    }

    // get language : use in listLanguage to show the current language
    public static String getLanguage(Context context){
        String langPref = "Language";
        String langOpt = "Option";
        SharedPreferences prefs = context.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        String language = prefs.getString(langPref, "");
        String option = prefs.getString(langOpt, "");
        Log.d("HelperLanguage ", "Language : " + language +" , Option : "+option);
        return language.concat("_"+option);
    }


    public static String GetCountryCode(String ssid){
        Locale loc = new Locale("", ssid);

        return loc.getDisplayCountry().trim();
    }


}
