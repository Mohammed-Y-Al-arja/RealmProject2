package com.example.tag.realofflineexample;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DisplayItem extends AppCompatActivity {

    MySharedPreference sharedPreference;
    Toolbar toolbar_top;
    TextView t,cnt,price;
    EditText num;
    Button pay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        num = (EditText) findViewById(R.id.num);
        pay = (Button) findViewById(R.id.pay);
        toolbar_top = (Toolbar) findViewById(R.id.toolbar_top);
        t= (TextView) toolbar_top.findViewById(R.id.t);
        cnt = (TextView) findViewById(R.id.cnt);
        price = (TextView) findViewById(R.id.price);
        t.setText("أدوات كهربائية");

        final String id = getIntent().getStringExtra("id");
        sharedPreference = new MySharedPreference(getBaseContext());
        sharedPreference.SetData(id,id);
        String url = MyApplication.base_url+"view.php?id="+id;
        final ImageView img = (ImageView) findViewById(R.id.img);
        final TextView title = (TextView) findViewById(R.id.title);
        final TextView desc = (TextView) findViewById(R.id.desc);
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("جاري التحميل ...");
        pDialog.show();

        final JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for(int i=0;i<response.length();i++){
                            try {
                                JSONObject object = response.getJSONObject(i);
                                ImageLoader imageLoader = MyApplication.getInstance().getImageLoader();
                                imageLoader.get(object.getString("img"), new ImageLoader.ImageListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        System.out.println("Image Load Error: " + error.getMessage());
                                    }
                                    @Override
                                    public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
                                        if (response.getBitmap() != null) {
                                            img.setImageBitmap(response.getBitmap());
                                        }
                                    }
                                });
                                title.setText(object.getString("title"));
                                desc.setText(object.getString("desc"));
                                price.setText(object.getString("price"));
                                cnt.setText(object.getString("cnt"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        pDialog.hide();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
                pDialog.hide();
            }
        });


        MyApplication.getInstance().addToRequestQueue(req, "items");

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(num.length()!=0 && Integer.parseInt(num.getText().toString())<=Integer.parseInt(cnt.getText().toString())) {

                    pDialog.setMessage("جاري شراء المنتج ...");
                    pDialog.show();
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, MyApplication.base_url + "pay.php",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    MyApplication.getInstance().addToRequestQueue(req, "items");
                                    pDialog.hide();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("id", id);
                            params.put("order", num.getText().toString());
                            return params;
                        }

                    };
                    MyApplication.getInstance().addToRequestQueue(stringRequest, "update");
                }
                else{
                    num.setError("الرجاء التأكد من الكمية");
                }
            }
        });

    }
}
