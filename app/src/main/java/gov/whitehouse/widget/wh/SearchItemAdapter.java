package gov.whitehouse.widget.wh;

import android.view.ViewGroup;

import com.evergage.android.promote.Item;

import gov.whitehouse.widget.BaseAdapter;
import gov.whitehouse.widget.ViewBinder;

public
class SearchItemAdapter extends BaseAdapter<Item> {

    @Override
    public
    ViewBinder<Item, SearchItemView> onCreateViewBinder(ViewGroup viewGroup)
    {
        return new ViewBinder<>(new SearchItemView(viewGroup.getContext()));
    }
}
