package com.greenjacket.greenjacket;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

public class Container extends AppCompatActivity {
    public Boolean demo;
    public MainActivity main_activity;
    public JSONObject menu_data;

    // not in main activity
    public Context container_context;
    private ContainerExtras extras;
    public JSONObject container_data;
    public static Context instance;

    public String category_id;
    public String category_name;
    public String main_opt_id;
    public String main_opt_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        main_activity = MainActivity.instance;
        menu_data = main_activity.menu_data;
        demo = main_activity.demo;
        instance = this;
        container_context = this;
        extras = new ContainerExtras(container_context, this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Container");


        if (!demo) {
            Intent intent = getIntent();
            category_id = intent.getStringExtra("category_id");
            category_name = intent.getStringExtra("category_name");
            main_opt_id = intent.getStringExtra("main_opt_id");
            main_opt_name = intent.getStringExtra("main_opt_name");
            //Log.d("Container", "from intent " + intent.getStringExtra("main_opt_name"));

            try {
                container_data = menu_data.getJSONObject("categories").getJSONObject(category_id).getJSONObject("mains").getJSONObject(main_opt_id);
                System.out.println(container_data);
            }
            catch (JSONException e)
            {
                Log.e("Container", "Error getting data from main option: " + e.toString());
            }

            new CreateContainerButtons().execute();
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final Context container_context = this;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Snackbar.make(view, "Are you ready to check out?", Snackbar.LENGTH_LONG)
                //       .setAction("Action", null).show();
                Intent to_checkout = new Intent(container_context, Checkout.class);
                startActivity(to_checkout);

            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void ContainerBurgerButton(View view) {
        // Do something in response to button
        Intent to_size = new Intent(this, Size.class);

        switch(view.getId())
        {
            case -1: // burger
                to_size.putExtra("container", "burger");
                break;
        }

        to_size.putExtra("item_added", true);
        startActivity(to_size);
    }

    public void ContainerButton(View view)
    {
        extras.ContainerButton(view);
    }

    private class CreateContainerButtons extends AsyncTask<String, String, Boolean>
    {
        private final String LogTag = CreateContainerButtons.class.getSimpleName();
        protected Boolean doInBackground(String... url)
        {
            try {
                extras.CreateContainerButtonsDo(container_data.getJSONObject("containers"));
            }
            catch (JSONException e)
            {
                Log.e(LogTag, "Error getting mains: " + e.toString());
            }
            return true;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Boolean success)
        {
            super.onPostExecute(true);
        }
    }
}
