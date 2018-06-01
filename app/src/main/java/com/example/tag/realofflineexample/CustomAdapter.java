package com.example.tag.realofflineexample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.util.List;

public class CustomAdapter extends BaseAdapter {

    Context context;
    List<Item> items;
    MySharedPreference sharedPreference;

    public CustomAdapter(Context context, List<Item> items)
    {
        this.context=context;
        this.items =items;
        sharedPreference = new MySharedPreference(context);
    }

    @Override
    public boolean isEnabled(int position)
    {
        return true;
    }
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View lview = inflater.inflate(R.layout.row_item,parent,false);
        final ImageView img = (ImageView) lview.findViewById(R.id.img);
        final ImageView n = (ImageView) lview.findViewById(R.id.n);
        TextView title = (TextView) lview.findViewById(R.id.title);
        TextView id = (TextView) lview.findViewById(R.id.id);
        TextView views = (TextView) lview.findViewById(R.id.views);
        Item item = items.get(position);
        title.setText(item.getTitle());
        id.setText(item.getId());
        views.setText(item.getPrice());
        if(sharedPreference.getDataString(item.getId()) != null &&sharedPreference.getDataString(item.getId()).equals(item.getId()))
            n.setVisibility(View.GONE);
        ImageLoader imageLoader = MyApplication.getInstance().getImageLoader();
        imageLoader.get(item.getImg(), new ImageLoader.ImageListener() {

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
        return lview;
    }
}
