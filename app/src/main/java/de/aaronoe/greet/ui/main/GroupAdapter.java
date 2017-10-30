package de.aaronoe.greet.ui.main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.aaronoe.greet.R;
import de.aaronoe.greet.model.Group;
import de.aaronoe.greet.model.Post;
import de.aaronoe.greet.utils.DateUtils;


public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private List<Group> mGroups;
    private GroupClickCallback mCallback;
    private Context mContext;

    public interface GroupClickCallback {
        void onGroupClick(Group group);
    }

    public GroupAdapter(Context context, GroupClickCallback clickCallback) {
        mCallback = clickCallback;
        mContext = context;
    }

    public void setGroups(List<Group> groups) {
        mGroups = groups;
        notifyDataSetChanged();
    }

    @Override
    public GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_item, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GroupViewHolder holder, int position) {
        Group group = mGroups.get(position);
        holder.bind(mContext, group);
    }

    @Override
    public int getItemCount() {
        return mGroups == null ? 0 : mGroups.size();
    }


    class GroupViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.group_name_tv)
        TextView groupNameTv;
        @BindView(R.id.last_post_date_tv)
        TextView lastPostDateTv;
        @BindView(R.id.last_post_preview_tv)
        TextView lastPostPreviewTv;

        GroupViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mCallback.onGroupClick(mGroups.get(getAdapterPosition()));
        }

        void bind(Context context, Group group) {
            groupNameTv.setText(group.getGroupName());
            Post latestPost = group.getLatestPost();
            if (latestPost != null) {
                lastPostDateTv.setText(DateUtils.getGroupItemString(context, latestPost.getTimestamp()));
                lastPostPreviewTv.setText(String.format("%s: %s", latestPost.getAuthor().getProfileName(), latestPost.getPostText()));
            }
        }
    }

}
