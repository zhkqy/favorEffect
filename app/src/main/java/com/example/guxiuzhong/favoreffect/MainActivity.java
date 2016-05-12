package com.example.guxiuzhong.favoreffect;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.example.guxiuzhong.favoreffect.surfaceview.FavorLayoutSurfaceView;

public class MainActivity extends AppCompatActivity {

    private FavorLayout mFavorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
