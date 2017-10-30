package de.aaronoe.greet.ui.widgetpreview;


import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.aaronoe.greet.R;
import de.aaronoe.greet.model.Post;
import de.aaronoe.greet.utils.DateUtils;
import de.aaronoe.greet.utils.DbUtils;
import de.hdodenhof.circleimageview.CircleImageView;


public class PostCursorAdapter extends RecyclerView.Adapter<PostCursorAdapter.PostViewHolder> {

    private Cursor postCursor;
    private Context mContext;
    private PostCursorAdapter.PostClickCallback mCallback;

    interface PostClickCallback {
        void onPostClick(Post post, CardView view, ImageView iv);
    }

    public PostCursorAdapter(Context context, PostCursorAdapter.PostClickCallback clickCallback) {
        mContext = context;
        mCallback = clickCallback;
    }

    void setPostList(Cursor cursor) {
        postCursor = cursor;
        notifyDataSetChanged();
    }

    @Override
    public PostCursorAdapter.PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new PostCursorAdapter.PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PostCursorAdapter.PostViewHolder holder, int position) {
        Post post = DbUtils.getPostFromCursor(postCursor, position);
        holder.bind(mContext, post);
    }

    @Override
    public int getItemCount() {
        return postCursor == null ? 0 : postCursor.getCount();
    }

    class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.post_author_iv)
        CircleImageView mAuthorImageView;
        @BindView(R.id.author_name_tv)
        TextView mAuthorNameTv;
        @BindView(R.id.post_date_tv)
        TextView mDateTextView;
        @BindView(R.id.post_text)
        TextView mPostTextView;
        @BindView(R.id.post_image_iv)
        ImageView mPostImageView;
        @BindView(R.id.post_item_card)
        CardView mCardRoot;

        PostViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mCallback.onPostClick(DbUtils.getPostFromCursor(postCursor, getAdapterPosition()), mCardRoot, mPostImageView);
        }

        void bind(Context context, Post post) {

            Glide.with(context)
                    .load(post.getAuthor().getPictureUrl())
                    .into(mAuthorImageView);

            mAuthorNameTv.setText(post.getAuthor().getProfileName());
            mDateTextView.setText(DateUtils.getGroupItemString(mContext, post.getTimestamp()));
            mPostTextView.setText(post.getPostText());

            if (post.getPostImageUrl() == null) {
                mPostImageView.setVisibility(View.GONE);
            } else {
                mPostImageView.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(post.getPostImageUrl())
                        .into(mPostImageView);
            }
        }
    }

}
