package com.youni.youni;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Kushan on 10-03-2016.
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    Context context;
    ArrayList<Product> filteredResults;



    public SearchAdapter(Context context, ArrayList<Product> filteredResults) {

        this.filteredResults = filteredResults;
        this.context = context;

    }

    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.search_card,parent,false);

        SearchViewHolder vh1 = new SearchViewHolder(v);

        return vh1;
    }

    @Override
    public void onBindViewHolder(SearchViewHolder holder, int position) {


          holder.tv1.setText(filteredResults.get(position).getResult());


    }

    @Override
    public int getItemCount() {
        return filteredResults.size();
    }

    class SearchViewHolder extends RecyclerView.ViewHolder{

                TextView tv1;


        public SearchViewHolder(View itemView) {
            super(itemView);

             tv1 = (TextView) itemView.findViewById(R.id.searchResult);


        }
    }
}
