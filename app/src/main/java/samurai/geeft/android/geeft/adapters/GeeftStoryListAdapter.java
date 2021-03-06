package samurai.geeft.android.geeft.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.models.Geeft;

/**
 * Created by ugookeadu on 09/02/16.
 */
public class GeeftStoryListAdapter extends
        RecyclerView.Adapter<GeeftStoryListAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private List<Geeft> mGeeftList =
            Collections.emptyList();

    //-------------------Macros
    private final int RESULT_OK = 1;
    private final int RESULT_FAILED = 0;
    private final int RESULT_SESSION_EXPIRED = -1;
    private int lastSize = 0;
    private Context mContext;

    private ProgressDialog mProgress;
    private long mLastClickTime = 0;

    //costructor
    public GeeftStoryListAdapter(Context context, List<Geeft> geeftList) {
        inflater = LayoutInflater.from(context);
        this.mGeeftList = geeftList;
        this.mContext = context;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView mGeeftImage;
        public TextView mGeeftTitle;
        public ViewHolder(View itemView) {
            super(itemView);
            mGeeftImage = (ImageView)itemView.findViewById(R.id.geeft_image);
            mGeeftTitle = (TextView)itemView.findViewById(R.id.geeft_title);
        }
    }
    @Override
    public GeeftStoryListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate the custom layout
        View mGeeftView = inflater.inflate(R.layout.geeft_story_list_item, parent, false);

        /** set the view's size, margins, paddings and layout parameters
         *
         */

        //Inflate a new view hierarchy from the specified xml resource.
        return new ViewHolder(mGeeftView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Geeft item = mGeeftList.get(position);
        Picasso.with(mContext).load(item.getGeeftImage())
                .fit().centerInside()
                .placeholder(R.drawable.ic_image_multiple)
                .into(holder.mGeeftImage);
        holder.mGeeftTitle.setText(item.getGeeftTitle());

    }

    @Override
    public int getItemCount() {
        return mGeeftList.size();
    }
}
