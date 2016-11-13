package com.hackerthon.storyteller.ui;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.hackerthon.storyteller.R;
import com.hackerthon.storyteller.pref.Consts;
import com.nhn.android.naverlogin.OAuthLogin;

/**
 * Created by Hynk on 2016-11-13.
 */
public class PostActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }
}
