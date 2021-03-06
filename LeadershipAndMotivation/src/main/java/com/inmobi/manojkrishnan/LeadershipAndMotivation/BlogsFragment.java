package com.inmobi.manojkrishnan.LeadershipAndMotivation;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.inmobi.manojkrishnan.LeadershipAndMotivation.feeders.FeedDataBlogs;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.network.NetworkUtils;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.utils.BlogShowCaseActivity;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.utils.blogData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by manoj.krishnan on 5/24/16.
 */
public class BlogsFragment  extends android.support.v4.app.Fragment {
    private ViewGroup mContainer;
    private BaseAdapter mFeedAdapter;
    private GridView mGridView;
    private ArrayList<FeedDataBlogs.FeedItem> mFeedItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_section_blogs, container, false);
        mContainer = (ViewGroup) view.findViewById(R.id.container);
        mGridView = (GridView) view.findViewById(R.id.gridview);

        if (!NetworkUtils.isNetworkAvailable(BlogsFragment.this.getActivity())) {
            Toast.makeText(BlogsFragment.this.getActivity(), "Please connect to network and launch again",
                    Toast.LENGTH_LONG).show();
            return view;
        }
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!NetworkUtils.isNetworkAvailable(BlogsFragment.this.getActivity())) {
            Toast.makeText(BlogsFragment.this.getActivity(), "Please connect to network and launch again",
                    Toast.LENGTH_LONG).show();
            return ;
        }
        mFeedItems = FeedDataBlogs.generateFeedItems();
        mFeedAdapter = new FeedItemAdapter(getActivity(), mFeedItems);
        mGridView.setAdapter(mFeedAdapter);
        mGridView.setOnItemClickListener(mItemClickListener);

    }


    final public class MyTaskParams {
        int width;
        int height;
        String image;
        Context ctxt;

        MyTaskParams(int width, int height, String image, Context ctxt) {
            this.width = width;
            this.height = height;
            this.image = image;
            this.ctxt = ctxt;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public String getImage() {
            return image;
        }

        public Context getContext() {
            return ctxt;
        }

    }

    private class FeedItemAdapter extends ArrayAdapter<FeedDataBlogs.FeedItem> {
        private Context context;
        private ArrayList<FeedDataBlogs .FeedItem> users;
        private LayoutInflater layoutInflater;

        class ContentViewHolder {
            TextView title;
            TextView content;
            ImageView thumb_image;
            ImageView big_image;
            //ImageView bottom_img;
        }

        public FeedItemAdapter(Context context, ArrayList<FeedDataBlogs.FeedItem> users) {
            super(context, R.layout.listitem, R.id.title, users);
            this.context = context;
            this.users = users;
            this.layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public int getCount() {
            return users.size();
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            if (null == rowView) {
                rowView = layoutInflater.inflate(R.layout.content_blog, parent, false);
                ContentViewHolder viewHolder = new ContentViewHolder();
                viewHolder.big_image = (ImageView) rowView.findViewById(R.id.big_image);
                viewHolder.title = (TextView) rowView.findViewById(R.id.blogtitle);
                rowView.setTag(viewHolder);

                Log.d("test", "RowView is null=============");
            } else
                Log.d("test", "RowView is not null");


            FeedDataBlogs .FeedItem feed = users.get(position);
            ContentViewHolder holder = (ContentViewHolder) rowView.getTag();
            Picasso.with(BlogsFragment.this.getContext()).load(feed.getThumbnail()).resize(200,200).into(holder.big_image);
            holder.title.setText(feed.gettitle());

            return rowView;


        }


    }

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            Log.d("testBlog","Inside the Click listener");
            try {
                if (!NetworkUtils.isNetworkAvailable(BlogsFragment.this.getActivity())) {
                    Toast.makeText(BlogsFragment.this.getActivity(), "Please connect to network to Read the full blog!", Toast.LENGTH_SHORT).show();
                    return;
                }

                FeedDataBlogs.FeedItem inst = mFeedItems.get(position);
                blogData data = new blogData(inst.getContent(),inst.getBigImage());
                Intent intentBlog = new Intent(BlogsFragment.this.getActivity(), BlogShowCaseActivity.class);
                intentBlog.putExtra("BlogItem", data);
                startActivity(intentBlog);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };







    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        Log.d("LOG LOG", "" + reqWidth+" "+reqHeight+" "+width+" "+height+" "+inSampleSize);

        return inSampleSize;
    }




    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    private class LongOperation extends AsyncTask<MyTaskParams, Void, Bitmap> {
        ImageView imgView;

        public LongOperation(ImageView imgView) {
            this.imgView = imgView;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            imgView.setImageBitmap(result);
        }

        @Override
        protected Bitmap doInBackground(MyTaskParams... params) {

            Bitmap bitmap = decodeSampledBitmapFromResource(getResources(), params[0].getContext().getResources().getIdentifier(params[0].getImage(), "drawable", params[0].getContext().getPackageName()), 100, 100);
            return bitmap;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}
