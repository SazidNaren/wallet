package vis.com.au.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import vis.com.au.Utility.FolderItemPojo;
import vis.com.au.wallte.R;

/**
 * Created by Linchpin25 on 2/8/2016.
 */
public class FolderItemAdapter extends BaseAdapter {

    ArrayList<FolderItemPojo> folderItemPojoArrayList;
    Context con;
    ViewHolderItem holder;

    public FolderItemAdapter(Context con, ArrayList<FolderItemPojo> folderItemPojoArrayList) {
        this.con = con;
        this.folderItemPojoArrayList = folderItemPojoArrayList;
    }

    @Override
    public int getCount() {
        return folderItemPojoArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.todaylist_adapter, parent, false);
            holder = new ViewHolderItem(convertView);
            convertView.setTag(holder);


        } else {
            holder = (ViewHolderItem) convertView.getTag();
        }

        FolderItemPojo folderItem = folderItemPojoArrayList.get(position);
        holder.fileNameTextView.setText(folderItem.getFileTitle());
        holder.upLoadedFileTime.setText("Uploaded on " + folderItem.getCreatedDate());


        return convertView;
    }

    public static class ViewHolderItem {

        ImageView uploadFileImageView, nextFileDetailPage;
        TextView fileNameTextView, upLoadedFileTime, tvHeader;

        public ViewHolderItem(View view) {
            uploadFileImageView = (ImageView) view.findViewById(R.id.upLoadFileImageView);
            nextFileDetailPage = (ImageView) view.findViewById(R.id.nextFileDetailPage);
            fileNameTextView = (TextView) view.findViewById(R.id.fileName);
            upLoadedFileTime = (TextView) view.findViewById(R.id.uploadFileTime);
            tvHeader = (TextView) view.findViewById(R.id.tvHeader);
        }


    }


}
