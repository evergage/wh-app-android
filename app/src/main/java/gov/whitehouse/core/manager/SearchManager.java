package gov.whitehouse.core.manager;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;

import java.util.Map;
import gov.whitehouse.BuildConfig;
import gov.whitehouse.core.Constants;
import gov.whitehouse.util.GsonUtils;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

public
class SearchManager
{

    public static
    SearchManager get()
    {
        return new SearchManager();
    }

    private static
    RestAdapter getRestAdapter()
    {
        Gson gson = GsonUtils.createGsonBuilder()
                             .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                             .create();
        return new RestAdapter.Builder()
                .setEndpoint(Constants.SEARCH_HOST)
                .setConverter(new GsonConverter(gson))
                .build();
    }

    public
    Observable<Map<String, Object>> search(String query)
    {
        SearchService ss;
        RestAdapter ra = getRestAdapter();

        if (BuildConfig.DEBUG) {
            ra.setLogLevel(RestAdapter.LogLevel.FULL);
        }
        ss = ra.create(SearchService.class);

        return ss.search(query);
    }

    private static
    interface SearchService
    {
        // The SmartSearch request
        //https://{accountName}.evergage.com/api/dataset/{dataset}/recommendations/{recipeId}/smartSearch?userId={userIdOrAnonId}&anonymous={trueIfAnonId}
        @GET("/api/dataset/whitehouse/recommendations/EMbKG/smartSearch?userId=TestSmartSearchUser")
        Observable<Map<String, Object>> search(@Query("query") String query);
    }
}
