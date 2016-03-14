package vis.com.au.adapter;

import java.util.ArrayList;

import vis.com.au.Utility.AddDocumentType;
import vis.com.au.wallte.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DocumentSpinnerAdapter extends BaseAdapter {

	ArrayList<AddDocumentType> documentItems ;
	
	public DocumentSpinnerAdapter(ArrayList<AddDocumentType> documentItems ){
		this.documentItems = documentItems;
	}
	
	@Override
	public int getCount() {
		return documentItems.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return documentItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		convertView=inflater.inflate(R.layout.document_type_name_adapter,parent,false);
		
		TextView documentTypeName=(TextView) convertView.findViewById(R.id.documentTypeName);
		AddDocumentType addDocType=(AddDocumentType) getItem(position);
		documentTypeName.setText(addDocType.getDocNames());
		return convertView;
	}

}
