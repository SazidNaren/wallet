package vis.com.au.adapter;

import vis.com.au.wallte.R;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ViewPagerAdater extends PagerAdapter{

	//declare variables
	Context context;
	int[] image;
	LayoutInflater inflater;
	
	public ViewPagerAdater(Context context,int[] image){
		
		this.context = context;
		this.image = image;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return image.length;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		// TODO Auto-generated method stub
		
		return view == ((LinearLayout) object);
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		// TODO Auto-generated method stub
		//declare variable
		ImageView imgFlag;
		
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View itemView = inflater.inflate(R.layout.viewpager_items, container,false);
		
		imgFlag = (ImageView) itemView.findViewById(R.id.imageID);
		imgFlag.setImageResource(image[position]);
		
		// Add viewpager_item.xml to ViewPager
		((ViewPager) container).addView(itemView);
		 
		return itemView;
	}
	
	@Override
	public void destroyItem(View container, int position, Object object) {
		// TODO Auto-generated method stub
		//remove viewpager_item.xml from ViewPager
		
		((ViewPager) container).removeView((LinearLayout) object);
	}

}
