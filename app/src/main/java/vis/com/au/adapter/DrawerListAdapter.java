package vis.com.au.adapter;

import java.util.List;
import vis.com.au.wallte.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DrawerListAdapter extends BaseAdapter{

	List<String> listItemsName;
	List<Integer> listItemsImage;
	String countDoc="";
	
	public DrawerListAdapter(List<String> listItemsName,List<Integer> listItemsImage,String countDoc){
		
		this.listItemsImage = listItemsImage;
		this.listItemsName = listItemsName;
		this.countDoc=countDoc;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listItemsName.size();
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
		LayoutInflater mInfater = LayoutInflater.from(parent.getContext());
		convertView = mInfater.inflate(R.layout.slider_list_content, null);
		TextView listTV = (TextView) convertView.findViewById(R.id.listItemsTV);
		TextView countDoctxt=(TextView)convertView.findViewById(R.id.countDoc);
		if(position==0)
			countDoctxt.setText(countDoc);
		else
		countDoctxt.setVisibility(View.GONE);
		ImageView iconListIV=(ImageView) convertView.findViewById(R.id.listItemIcon);
		iconListIV.setImageResource(listItemsImage.get(position));
		//Log.e("print position", iconList.size()+"icon item "+iconList.get(position).toString());
		listTV.setText(listItemsName.get(position));
		return convertView;
	}

}
