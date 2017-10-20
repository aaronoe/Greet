package de.aaronoe.greet.ui.main;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.aaronoe.greet.R;
import de.aaronoe.greet.model.Group;


public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private List<Group> mGroups;

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
        holder.groupNameTv.setText(group.getGroupName());
    }

    @Override
    public int getItemCount() {
        return mGroups == null ? 0 : mGroups.size();
    }


    class GroupViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.group_name_tv)
        TextView groupNameTv;

        GroupViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
