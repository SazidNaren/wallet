package vis.com.au.adapter;

import java.util.ArrayList;

import vis.com.au.Utility.SettingListCell;
import vis.com.au.wallte.R;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


public class SettingListAdapter extends BaseAdapter{

	ArrayList<SettingListCell> sectionList;
	
	public SettingListAdapter(ArrayList<SettingListCell> sectionList){
		this.sectionList = sectionList;
	}
	
	@Override
	public int getCount() {
		return sectionList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		convertView = inflater.inflate(R.layout.setting_item_cell,null);
		TextView itemTextView = (TextView) convertView.findViewById(R.id.itemTextView);
		LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.liearLayout);
		 if(sectionList.get(position).getSectionValue() !=null && sectionList.get(position).getSectionValue().equalsIgnoreCase("")){
			 itemTextView.setText(sectionList.get(position).getSectionName());
             linearLayout.setBackgroundColor(Color.GRAY);
            
       }
       else{
    	   itemTextView.setText(sectionList.get(position).getSectionValue());
             linearLayout.setBackgroundColor(Color.WHITE);
       }
      
       
		return convertView;
	}

}
