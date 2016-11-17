package com.zeng.bbfeeding;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by xianganzeng on 2016/10/27.
 */

public class HistoryPage extends Page{
    private RecyclerView mHistoryView;


    @Override
    protected void onCreatePage(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.page_history);
        mHistoryView = (RecyclerView)findViewById(R.id.history_recyclerview);

        mHistoryView.setLayoutManager(new LinearLayoutManager(getContext()));
        Data.getInstance().mHistoryUpdated = true; // Life cycle of Date instance.
    }

    @Override
    protected void onShowPage() {
        super.onShowPage();
        if (Data.getInstance().mHistoryUpdated){
            Data.getInstance().mHistoryUpdated = false;

            HistoryAdapter adapter = new HistoryAdapter();
            mHistoryView.setAdapter(adapter);

            // scroll to latest
            if (!adapter.mItemList.isEmpty()) {
                int position = adapter.mItemList.size() - 1;
                for (; position > 0; --position) {
                    if (!adapter.mItemList.get(position).date.equals(adapter.mItemList.get(position-1).date)){
                        break;
                    }
                }
                mHistoryView.getLayoutManager().scrollToPosition(position);
            }
        }
    }

    @Override
    protected void onHidePage(){
        super.onHidePage();
    }

    private class HistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public ArrayList<ItemData> mItemList = new ArrayList<ItemData>();
        public HistoryAdapter(){
            super();

            Calendar today = Calendar.getInstance();
            long now = today.getTimeInMillis();
            today.setTimeInMillis(now - now%1000);
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            long dayFilter = today.getTimeInMillis() - 20 * (24*60*60*1000);

            String rawStr = Data.getInstance().getFeedingHistory();
            String[] rawArray = rawStr.split(",");
            StringBuilder builder = new StringBuilder("");
            boolean fixed = false;
            for (String v : rawArray) {
                String[] rawItem = v.split("\\|");
                try{
                    long t = Long.parseLong(rawItem[0]);
                    if (t > dayFilter) {
                        mItemList.add(new ItemData(t, Integer.parseInt(rawItem[1]), Integer.parseInt(rawItem[2])));
                        builder.append(v);
                        builder.append(",");
                    } else { fixed = true;}
                }catch (Exception e){
                    fixed = true;
                }
            }

            if (fixed) {
                Data.getInstance().setFeedingHistory(builder.toString());
            }
        }

        private class ItemData {
            public ItemData(long time, int pattern, int param){
                Calendar t = Calendar.getInstance(); t.setTimeInMillis(time);
                this.date = String.format(getString(R.string.history_item_date), t.get(Calendar.YEAR), t.get(Calendar.MONTH) + 1, t.get(Calendar.DAY_OF_MONTH));
                this.time = String.format(getString(R.string.history_item_time), t.get(Calendar.HOUR_OF_DAY), t.get(Calendar.MINUTE));
                this.pattern = pattern;
                this.param = param;
            }
            public String date;
            public String time;
            public int pattern;
            public int param;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.history_item, parent, false);
            return new ItemHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ItemData itemData = mItemList.get(position);
            ItemHolder itemHolder = (ItemHolder)holder;

            boolean showDate = false;
            if (position == 0) {showDate = true;}
            else {showDate = !mItemList.get(position-1).date.equals(itemData.date);}
            if (showDate) {
                itemHolder.mDatePanel.setVisibility(View.VISIBLE);
                itemHolder.mDateTextView.setText(itemData.date);
            } else {
                itemHolder.mDatePanel.setVisibility(View.GONE);
            }

            itemHolder.mContentPanel.setBackgroundResource(position % 2 == 0
                    ? R.color.colorHistoryItemLight : R.color.colorHistoryItemDark);
            itemHolder.mTimeTextView.setText(itemData.time);
            if (itemData.pattern != Data.FEEDING_PATTERN_FORMULA) {
                itemHolder.mPatternTextView.setText(itemData.pattern == Data.FEEDING_PATTERN_LEFT
                        ? getString(R.string.history_item_breast_left) : getString(R.string.history_item_breast_right));
                itemHolder.mParamTextView.setText(String.format(getString(R.string.history_item_breast_interval),
                        itemData.param == 0 ? "-" : String.valueOf(itemData.param)));
            } else {
                itemHolder.mPatternTextView.setText(R.string.history_item_formula);
                itemHolder.mParamTextView.setText(String.format(getString(R.string.history_item_formula_intake),
                        itemData.param == 0 ? "-" : String.valueOf(itemData.param)));
            }
        }

        @Override
        public int getItemCount() {
            return mItemList.size();
        }
    }

    private class ItemHolder extends RecyclerView.ViewHolder{
        public ItemHolder(View itemView){
            super(itemView);

            mDatePanel = itemView.findViewById(R.id.date_panel);
            mDateTextView = (TextView)itemView.findViewById(R.id.date_textview);

            mContentPanel = itemView.findViewById(R.id.content_panel);
            mTimeTextView = (TextView)itemView.findViewById(R.id.time_textview);
            mPatternTextView = (TextView)itemView.findViewById(R.id.pattern_textview);
            mParamTextView = (TextView)itemView.findViewById(R.id.param_textview);
        }

        public View mDatePanel;
        public TextView mDateTextView;

        public View mContentPanel;
        public TextView mTimeTextView;
        public TextView mPatternTextView;
        public TextView mParamTextView;
    }

}
