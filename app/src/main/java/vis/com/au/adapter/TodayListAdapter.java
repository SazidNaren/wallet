package vis.com.au.adapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import vis.com.au.Utility.TodayListView;
import vis.com.au.wallte.R;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class TodayListAdapter extends BaseAdapter {

    List<TodayListView> todayListView;
    Context con;
    Picasso.Builder builder;

    public TodayListAdapter(List<TodayListView> todayListView, Context con) {
        this.todayListView = todayListView;
        this.con = con;
        builder = new Picasso.Builder(con);

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return todayListView.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater Inflater = LayoutInflater.from(parent.getContext());
            convertView = Inflater.inflate(R.layout.todaylist_adapter, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        TodayListView todayLV = todayListView.get(position);
        viewHolder.fileNameTextView.setText(todayLV.fileName);

        if (todayLV.isFirst()) {
            viewHolder.tvHeader.setVisibility(View.VISIBLE);
            viewHolder.tvHeader.setText(todayLV.uploadedDate);

        } else {
            viewHolder.tvHeader.setVisibility(View.GONE);
        }


        if (todayLV.isFolder()) {
            //viewHolder.uploadFileImageView.setBackgroundResource(R.drawable.open_folder);
            viewHolder.upLoadedFileTime.setText("Contains " + todayLV.getCount() + " item");
            builder.build().load(R.drawable.open_folder).error(R.drawable.open_folder).placeholder(R.drawable.open_folder).into(viewHolder.uploadFileImageView);

        } else {
            viewHolder.upLoadedFileTime.setText(df.format(todayLV.upLoadedTime));
            String imgUrl = "http://workerswallet.com.au/walletapi/" + todayLV.getFilePath();
            builder.build().load(imgUrl).error(R.drawable.file_icon).placeholder(R.drawable.file_icon).into(viewHolder.uploadFileImageView);

        }


        return convertView;
    }


    private class ViewHolder {
        ImageView uploadFileImageView, nextFileDetailPage;
        TextView fileNameTextView, upLoadedFileTime, tvHeader;

        public ViewHolder(View view) {
            uploadFileImageView = (ImageView) view.findViewById(R.id.upLoadFileImageView);
            nextFileDetailPage = (ImageView) view.findViewById(R.id.nextFileDetailPage);
            fileNameTextView = (TextView) view.findViewById(R.id.fileName);
            upLoadedFileTime = (TextView) view.findViewById(R.id.uploadFileTime);
            tvHeader = (TextView) view.findViewById(R.id.tvHeader);
        }
    }
}
