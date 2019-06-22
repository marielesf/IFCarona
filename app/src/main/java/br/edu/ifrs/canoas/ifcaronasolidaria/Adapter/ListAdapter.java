package br.edu.ifrs.canoas.ifcaronasolidaria.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by mariele on 04/12/2017.
 */

public class ListAdapter extends BaseAdapter {
    private Context context;
    private List<String> list;

    public ListAdapter(Context context, List<String> list){
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public String getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if(view == null){
            holder = new ViewHolder();

            view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, viewGroup, false);

            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }

        holder.ivFotinho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return view;
    }

    static class ViewHolder{
        ImageView ivFotinho;
        TextView tvFotinho;
    }
}
