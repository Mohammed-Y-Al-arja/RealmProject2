package com.example.tag.realofflineexample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmResults;

public class OfflineRealmActivity extends AppCompatActivity {

    CustomAdapter madapter;
    ListView lv;
    Realm db = Realm.getDefaultInstance();
    Toolbar toolbar_top;
    TextView t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_from_relm);

        toolbar_top = (Toolbar) findViewById(R.id.toolbar_top);
        t= (TextView) toolbar_top.findViewById(R.id.t);
        t.setText("أدوات كهربائية");
        lv = (ListView) findViewById(R.id.items);
        RealmResults<Item> realmResults = db.where(Item.class).findAll();
        System.out.println("Log size "+realmResults.size());

        madapter = new CustomAdapter(this,realmResults);
        lv.setAdapter(madapter);
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
}
