package com.interview.search.adapter;

/**
 * Created by Vinit sharma on 11-06-2016.
 */
import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.interview.search.R;
import com.interview.search.app.AppController;
import com.interview.search.model.ImageObject;

public class CustomRecycleListAdapter extends RecyclerView.Adapter<CustomRecycleListAdapter.ViewHolder> {
    private List<ImageObject> mDataset;
    private Context context;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private NetworkImageView thumbnail;
        private TextView title;

        public ViewHolder(View v) {
            super(v);
            thumbnail = (NetworkImageView) v.findViewById(R.id.thumbnail);
            title = (TextView) v.findViewById(R.id.title);
        }
    }


    public CustomRecycleListAdapter(List<ImageObject> myDataset, Context context) {
        this.mDataset = myDataset;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CustomRecycleListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                  int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, null);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        ImageObject item = mDataset.get(position);
        if (item != null) {
            holder.title.setText(item.getTitle());
            holder.thumbnail.setErrorImageResId(R.drawable.img_not_available);
            holder.thumbnail.setDefaultImageResId(R.drawable.wiki_default);

            if (item.getThumbnail() != null) {
                int height = item.getThumbnail().getHeight();
                int width = item.getThumbnail().getWidth();
                int size = (height >= width ? height : width);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                        size,
                        size);

                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                holder.thumbnail.setLayoutParams(layoutParams);

                    holder.thumbnail.setImageUrl(item.getThumbnail().getSource(), imageLoader);

            }
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}