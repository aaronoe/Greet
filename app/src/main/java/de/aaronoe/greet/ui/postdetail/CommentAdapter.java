package de.aaronoe.greet.ui.postdetail;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.aaronoe.greet.R;
import de.aaronoe.greet.model.Comment;
import de.aaronoe.greet.utils.DateUtils;
import de.hdodenhof.circleimageview.CircleImageView;


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> comments;
    private Context mContext;

    public  void setComments(List<Comment> comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }

    public CommentAdapter(Context context) {
        mContext = context;
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        holder.bind(comments.get(position));
    }

    @Override
    public int getItemCount() {
        return comments == null ? 0 : comments.size();
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.post_author_iv)
        CircleImageView mAuthorIv;
        @BindView(R.id.author_name_tv)
        TextView mAuthorNameTv;
        @BindView(R.id.comment_date_tv)
        TextView mCommentDateTv;
        @BindView(R.id.comment_item_tv)
        TextView mCommentTextTv;

        CommentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(Comment comment) {
            mAuthorNameTv.setText(comment.getUser().getProfileName());
            mCommentDateTv.setText(DateUtils.getGroupItemString(mContext, comment.getTimestamp()));
            mCommentTextTv.setText(comment.getCommentText());

            Glide.with(mContext)
                    .load(comment.getUser().getPictureUrl())
                    .into(mAuthorIv);
        }

    }

}
