package vis.com.au.helper;

import java.util.Comparator;

import vis.com.au.Utility.TodayListView;

/**
 * Created by Linchpin25 on 2/2/2016.
 */
public class DocumentsComparator implements Comparator<TodayListView> {
    @Override
    public int compare(TodayListView lhs, TodayListView rhs) {
        if (lhs.getUpLoadedTime().before(rhs.getUpLoadedTime()))
            return 1;
        else if (lhs.getUpLoadedTime().after(rhs.getUpLoadedTime()))
            return -1;
        else
            return 0;
    }
}
