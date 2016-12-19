package gov.whitehouse.app.wh;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.evergage.android.internal.util.JSONUtil;
import com.evergage.android.promote.Article;
import com.evergage.android.promote.Item;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gov.whitehouse.R;
import gov.whitehouse.app.BaseListFragment;
import gov.whitehouse.core.manager.SearchManager;
import gov.whitehouse.util.NetworkUtils;
import gov.whitehouse.widget.BaseAdapter;
import gov.whitehouse.widget.wh.SearchItemAdapter;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public
class SearchFragment extends BaseListFragment<Item>
{
    private
    OnSearchResultClickedListener mSearchResultClickedListener;

    Subscription mSearchSub;

    @Override
    public
    void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
    }

    @Override
    public
    BaseAdapter<Item> onCreateAdapter()
    {
        return new SearchItemAdapter();
    }

    @Override
    public
    void onStart()
    {
        super.onStart();
        getAdapter().setOnItemClickListener((itemView, position) -> {
            if (mSearchResultClickedListener != null) {
                mSearchResultClickedListener.onSearchResultClicked(getAdapter().getItem(position),
                                                                   position);
            }
        });
    }

    @Override
    public
    void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        view.setBackgroundResource(R.color.wh_secondary_air);
    }

    @Override
    public
    boolean isRootFragment()
    {
        return false;
    }

    public
    void setSearchResultClickedListener(OnSearchResultClickedListener listener)
    {
        mSearchResultClickedListener = listener;
    }

    public
    void submitQuery(String query)
    {
        showList(false);
        showProgress(true);
        if (mSearchSub != null) {
            mSearchSub.unsubscribe();
        }
        if (!NetworkUtils.checkNetworkAvailable(getActivity())) {
            showProgress(false);
            Toast.makeText(getActivity(), R.string.no_network, Toast.LENGTH_SHORT).show();
            return;
        }
        mSearchSub = AndroidObservable.bindFragment(this, SearchManager.get()
                .search(query)
                .first()
                .map(searchResults -> {
                    List<Item> list = new ArrayList<>();
                    // Build Items from SmartSearch results
                    JSONObject results = new JSONObject(searchResults);
                    JSONArray recommendedItems = JSONUtil.getJSONArray(results, "recommendedItems");
                    if (recommendedItems != null) {
                        for (int i = 0; i < recommendedItems.length(); i++) {
                            JSONObject itemJson = JSONUtil.arrayGetJSONObject(recommendedItems, i);
                            if (itemJson != null) {
                                String id = JSONUtil.getString(itemJson, "_id");
                                if (id == null) id = "article-" + i;
                                Article article = Article.fromJSONObject(itemJson, id);
                                list.add(article);
                            }
                        }
                    }
                    return list;
                })
                .subscribeOn(Schedulers.newThread()))
                .subscribe(new Observer<List<Item>>()
                {
                    @Override
                    public
                    void onCompleted()
                    {
                        showProgress(false);
                        showList(true);
                    }

                    @Override
                    public
                    void onError(Throwable e)
                    {
                        Toast.makeText(getActivity(), "An error occurred while trying to search", Toast.LENGTH_SHORT).show();
                        Timber.w(e, "Error searching for query '%s'", query);
                    }

                    @Override
                    public
                    void onNext(List<Item> searchResults)
                    {
                        getAdapter().clear();
                        if (searchResults != null) {
                            getAdapter().addAll(searchResults);
                        }
                    }
                });
        bindSubscription(mSearchSub);
    }

    public static
    interface OnSearchResultClickedListener
    {
        public
        void onSearchResultClicked(Item result, int position);
    }
}
