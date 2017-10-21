package de.aaronoe.greet.ui.groupdetail;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.aaronoe.greet.R;
import de.aaronoe.greet.model.Post;
import de.aaronoe.greet.utils.DateUtils;
import de.hdodenhof.circleimageview.CircleImageView;


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> postList;
    private Context mContext;
    private PostClickCallback mCallback;

    interface PostClickCallback {
        void onPostClick(Post post, CircleImageView imageView);
    }

    public PostAdapter(Context context, PostClickCallback clickCallback) {
        mContext = context;
        mCallback = clickCallback;
    }

    void setPostList(List<Post> posts) {
        postList = posts;
        notifyDataSetChanged();
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.bind(mContext, post);
    }

    @Override
    public int getItemCount() {
        return postList == null ? 0 : postList.size();
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

        PostViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mCallback.onPostClick(postList.get(getAdapterPosition()), mAuthorImageView);
        }

        void bind(Context context, Post post) {

            Glide.with(context)
                    .load(post.getAuthor().getPictureUrl())
                    .into(mAuthorImageView);

            mAuthorNameTv.setText(post.getAuthor().getProfileName());
            mDateTextView.setText(DateUtils.convertTimestampToPostDate(post.getTimestamp()));
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
