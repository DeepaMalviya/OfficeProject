package com.daffodil.officeproject.HomeModule;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.daffodil.officeproject.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    /*public void AddClientMethod(View view) {
        startActivity(new Intent(this, AddClientActivity.class));
    }*/
}
