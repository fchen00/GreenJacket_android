package com.greenjacket.greenjacket;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Sammy Sam on 4/4/2016.
 */
public class OptionsExtras {
    private Options options_activity;
    private Context options_context;

    OptionsExtras(Context new_context, Options new_activity)
    {
        options_activity = new_activity;
        options_context = new_context;
    }

    public void OptionsButton (View view)
    {
        String type = view.getTag(R.string.button_type_tag).toString();
        int checkbox_id = ((Integer) view.getTag(R.string.button_check_id_tag));
        String view_id = view.getTag(R.string.button_id_tag).toString();
        String view_name = view.getTag(R.string.button_name_tag).toString();

        if (type.compareTo("fixed") == 0)
        {
            View this_view = options_activity.findViewById(R.id.fab);
            Snackbar.make(this_view, "This item is required", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
        else if (type.compareTo("optional") == 0)
        {
            CheckBox checkbox = (CheckBox) options_activity.findViewById(checkbox_id);
            checkbox.toggle();

            if (checkbox.isChecked()) {
                options_activity.chosen_option_ids.add(view_id);
                options_activity.chosen_option_names.add(view_name);
            }
            else{
                options_activity.chosen_option_ids.remove(view_id);
                options_activity.chosen_option_names.remove(view_name);
            }
        }
    }

    public void CreateOptionsButtonsDo(JSONObject options) throws JSONException
    {
        ArrayList<String> options_names = new ArrayList<String>();
        ArrayList <String> options_ids = new ArrayList<String>();
        ArrayList <String> options_types = new ArrayList<String>();
        ArrayList <String> options_prices = new ArrayList<String>();

        for(Iterator<String> options_iter = options.keys(); options_iter.hasNext();) {
            String key = options_iter.next();
            String new_name = options.getJSONObject(key).getString("name");
            String price = options.getJSONObject(key).getString("price");
            String type = options.getJSONObject(key).getString("type");
            options_ids.add(key);
            options_names.add(new_name);
            options_types.add(type);
            options_prices.add(price);
        }

        View.OnClickListener options_listener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                OptionsButton(v);
            }
        };

        GridLayout grid_layout = (GridLayout) options_activity.findViewById(R.id.options_content_options);

        OptionsExtras.CreateOptionsButtons(options_ids, options_names, options_listener,
                "options_", grid_layout, options_context,
                options_activity, options_types, options_prices);
    }

    // creates buttons. uses image_prefex+name to get image
    public static void CreateOptionsButtons (ArrayList<String> ids, ArrayList<String> names,
                                      View.OnClickListener listener, String image_prefix,
                                      final GridLayout gridLayout, Context context, Activity activity,
                                             final ArrayList<String> types, ArrayList<String> prices)
    {
        String LogTag = "createButton";

        gridLayout.removeAllViews();
        int total = names.size();
        int column = 3;
        int row = total / column;
        gridLayout.setColumnCount(column);
        gridLayout.setRowCount((row + 1) * 2);
        for (int i = 0, c = 0, r = 0; i < total; i++, c++) {
            if (c == column) {
                c = 0;
                r++;
            }

            final ImageButton new_button = new ImageButton(context);

            try{
                new_button.setImageResource(R.mipmap.class.getField(image_prefix + CleanForImage(names.get(i))).getInt("id"));
            }
            catch (Exception e)
            {
                Log.w(LogTag, "Failed to get image with name: " + image_prefix + CleanForImage(names.get(i)));
                new_button.setImageResource(R.mipmap.sandwich);
            }
            new_button.setBackgroundColor(ContextCompat.getColor(context, R.color.colorButton));
            new_button.setTag(R.string.button_id_tag, ids.get(i));
            new_button.setTag(R.string.button_name_tag, names.get(i));
            new_button.setTag(R.string.button_type_tag, types.get(i));
            new_button.setScaleType(ImageView.ScaleType.FIT_CENTER);
            new_button.setOnClickListener(listener);
            new_button.setPadding(0, 0, 0, 0);

            GridLayout.LayoutParams b_param = new GridLayout.LayoutParams();
            b_param.height = (int) context.getResources().getDimension(R.dimen.button_height);
            b_param.width = (int) context.getResources().getDimension(R.dimen.button_width);
            b_param.rightMargin = 20;
            b_param.leftMargin = 20;
            b_param.topMargin = 60;
            b_param.setGravity(Gravity.CENTER);

            try {
                if (total < 3)
                    b_param.columnSpec = GridLayout.spec(c);
                else
                    b_param.columnSpec = GridLayout.spec(c, 1f);
            }
            catch (NoSuchMethodError e)
            {
                Log.w(LogTag, "Not using weight");
                b_param.columnSpec = GridLayout.spec(c);
            }
            b_param.rowSpec = GridLayout.spec(r*2);
            new_button.setLayoutParams(b_param);

            //final TextView new_text = new TextView(context);
            final CheckBox new_check = new CheckBox(context);

            String text_string = names.get(i);
            if (text_string.length() > 20)
            {
                text_string = text_string.substring(0, 20);
                Log.w(LogTag, "Text "+names.get(i)+" too long for button, truncating");
            }
            text_string += "\n+$" + prices.get(i);

            new_check.setText(text_string + "\n");
            new_check.setTextColor(Color.parseColor("#ffffff"));
            new_check.setGravity(Gravity.CENTER_HORIZONTAL);
            new_check.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            //ColorStateList check_colors = new ColorStateList(new int[][]{new int[0]}, new int[]{0xffffcc00});

            int[][] states = new int[][] {
                    new int[] { android.R.attr.state_enabled}, // enabled
                    //new int[] {-android.R.attr.state_enabled}, // disabled
                    //new int[] {-android.R.attr.state_checked}, // unchecked
                    //new int[] { android.R.attr.state_pressed}  // pressed
            };

            int[] colors;

            if(types.get(i).compareTo("optional") == 0) {
                colors = new int[]{
                        ContextCompat.getColor(context, R.color.colorPrimary),
                        //Color.RED,
                        //Color.GREEN,
                        //Color.BLUE
                };
            }
            else if (types.get(i).compareTo("fixed") == 0)
            {
                colors = new int[]{
                        ContextCompat.getColor(context, R.color.colorPrimaryDark),
                        //Color.RED,
                        //Color.GREEN,
                        //Color.BLUE
                };
            }
            else
            {
                colors = new int[]{
                        Color.WHITE,
                        //Color.RED,
                        //Color.GREEN,
                        //Color.BLUE
                };
                Log.e("Options Extras", "Illegal option type found: " + types.get(i));
            }

            ColorStateList check_colors = new ColorStateList(states, colors);

            try {
                new_check.setButtonTintList(check_colors);
            }
            catch (NoSuchMethodError e)
            {
                Log.w("Options Extras", "Changing checkbox color not supported in api < 21");
            }

            int checkbox_id = gridLayout.generateViewId();
            new_check.setId(checkbox_id);
            new_button.setTag(R.string.button_check_id_tag, checkbox_id);

            GridLayout.LayoutParams t_param = new GridLayout.LayoutParams();
            t_param.height = GridLayout.LayoutParams.WRAP_CONTENT;
            t_param.width = (int) context.getResources().getDimension(R.dimen.button_width);
            t_param.rightMargin = 5;
            t_param.topMargin = 5;

            try
            {
                if (total < 3)
                    t_param.columnSpec = GridLayout.spec(c);
                else
                    t_param.columnSpec = GridLayout.spec(c, 1f);
            }
            catch (NoSuchMethodError e)
            {
                t_param.columnSpec = GridLayout.spec(c);
            }
            t_param.rowSpec = GridLayout.spec(r * 2+1);
            new_check.setLayoutParams(t_param);

            // updating ui must be done on main thread
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (new_button.getTag(R.string.button_type_tag).toString().compareTo("fixed") == 0)
                        new_check.setChecked(true);

                    gridLayout.addView(new_button);
                    gridLayout.addView(new_check);
                }
            });

        }
    }


    public static String CleanForImage(String raw_string)
    {
        // create a new copy
        String out_str = new String(raw_string);
        out_str = out_str.toLowerCase();
        out_str = out_str.replace(" ", "_");
        out_str = out_str.replaceAll("[^a-z|_|0-9]+", "");

        return out_str;
    }
}
