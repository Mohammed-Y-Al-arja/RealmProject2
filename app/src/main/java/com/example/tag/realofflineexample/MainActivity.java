package com.example.tag.realofflineexample;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity {

    ListView lv;
    List<Item> items;
    CustomAdapter madapter;
    MySharedPreference sharedPreference;
    String url = MyApplication.base_url+"items.php";
    Button save,open;
    TextView title;


    Toolbar toolbar_top;
    private static MainActivity instance;

    Realm db ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db=Realm.getDefaultInstance();
        instance=this;

        toolbar_top = (Toolbar) findViewById(R.id.toolbar_top);
        save = (Button) toolbar_top.findViewById(R.id.save);
        open = (Button) toolbar_top.findViewById(R.id.open);
        title= (TextView) toolbar_top.findViewById(R.id.title);
        title.setText("أدوات كهربائية");
        if(!MobilesService.serviceRunning){
            startService(new Intent(getBaseContext(),MobilesService.class));
        }

        final SwipeRefreshLayout SwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.SwipeRefresh);
        SwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                JsonArrayRequest req = new JsonArrayRequest(url,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                sharedPreference.SetData("count",response.length()+"");
                                items.removeAll(items);
                                for(int i=0;i<response.length();i++){
                                    try {
                                        JSONObject object = response.getJSONObject(i);
                                        Item item = new Item();
                                        item.setId(object.getString("id"));
                                        item.setTitle(object.getString("title"));
                                        item.setDesc(object.getString("desc"));
                                        item.setImg(object.getString("img"));
                                        item.setCnt(object.getString("cnt"));
                                        item.setPrice(object.getString("price"));
                                        item.setViews(object.getString("views"));
                                        items.add(item);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                madapter = new CustomAdapter(getBaseContext(),items);
                                lv.setAdapter(madapter);
                                SwipeRefresh.setRefreshing(false);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error);
                    }
                });
                MyApplication.getInstance().addToRequestQueue(req, "items");
            }
        });

        lv = (ListView) findViewById(R.id.items);
        registerForContextMenu(lv);
        items = new ArrayList<>();
        sharedPreference = new MySharedPreference(getBaseContext());
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("جاري التحميل ...");
        pDialog.show();

        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        sharedPreference.SetData("count",response.length()+"");
                        for(int i=0;i<response.length();i++){
                            try {
                                JSONObject object = response.getJSONObject(i);
                                Item item = new Item();
                                Item item1=new Item();
                                item.setId(object.getString("id"));
                                item.setTitle(object.getString("title"));
                                item.setDesc(object.getString("desc"));
                                item.setCnt(object.getString("cnt"));
                                item.setPrice(object.getString("price"));
                                item.setImg(object.getString("img"));
                                item.setViews(object.getString("views"));
                                items.add(item);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        madapter = new CustomAdapter(getBaseContext(),items);
                        lv.setAdapter(madapter);
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

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String itemStr =  new Gson().toJson(items);
//                db.beginTransaction();
//                db.createOrUpdateAllFromJson(Item.class,itemStr);
//                db.commitTransaction();
            }
        });
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(),OfflineRealmActivity.class));
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView id = (TextView) view.findViewById(R.id.id);
                Intent intent = new Intent(getBaseContext(),DisplayItem.class);
                intent.putExtra("id",id.getText().toString());
                startActivity(intent);

            }
        });

    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final Item item1 = (Item) lv.getItemAtPosition(info.position);
        if(item.getTitle()=="حذف"){
            StringRequest stringRequest = new StringRequest(Request.Method.POST, MyApplication.base_url+"delete.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            items.remove(info.position);
                            madapter.notifyDataSetChanged();
                            Toast.makeText(getBaseContext(),response,Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }){
                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("id",item1.getId());
                    return params;
                }

            };
            MyApplication.getInstance().addToRequestQueue(stringRequest, "delete");

        }

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        sharedPreference.SetData("count",response.length()+"");
                        items.removeAll(items);
                        for(int i=0;i<response.length();i++){
                            try {
                                JSONObject object = response.getJSONObject(i);
                                Item item = new Item();
                                item.setId(object.getString("id"));
                                item.setTitle(object.getString("title"));
                                item.setDesc(object.getString("desc"));
                                item.setImg(object.getString("img"));
                                item.setCnt(object.getString("cnt"));
                                item.setPrice(object.getString("price"));
                                item.setViews(object.getString("views"));
                                items.add(item);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        madapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        });
        MyApplication.getInstance().addToRequestQueue(req, "items");
    }
}
