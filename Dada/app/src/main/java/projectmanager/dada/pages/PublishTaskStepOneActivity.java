package projectmanager.dada.pages;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import java.util.List;
import projectmanager.dada.CheckPermissionsActivity;
import projectmanager.dada.R;
import projectmanager.dada.adapter.LocationAdapter;
import projectmanager.dada.adapter.LocationSearchedAdapter;
import projectmanager.dada.model.Location;
import projectmanager.dada.model.Task;
import projectmanager.dada.util.DataManager;


/**
 * 发布任务步骤一
 */
public class PublishTaskStepOneActivity extends CheckPermissionsActivity implements LocationSource, AMapLocationListener, Inputtips.InputtipsListener, PoiSearch.OnPoiSearchListener {

    private Button stepOneFinishButton;
    private AMap mMap;
    private MapView mMapView = null;
    private UiSettings mUiSettings;
    //定位需要的声明
    private AMapLocationClient mLocationClient = null;//定位发起端
    private AMapLocationClientOption mLocationOption = null;//定位参数
    private OnLocationChangedListener mListener = null;//定位监听器
    private boolean isFirstLoc = true;//是否是第一次定位
    private Marker marker;//选择任务地点的标记
    private LatLng myLatLng = null;//当前位置经纬度，用于resume后恢复位置
    private AutoCompleteTextView search;
    private LocationSearchedAdapter adapter;
    private PoiSearch.Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_task_step_one);
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMap = mMapView.getMap();
        mUiSettings = mMap.getUiSettings();
        mMap.setLocationSource(this);
        mUiSettings.setMyLocationButtonEnabled(true); // 显示默认的定位按钮
        mMap.setMyLocationEnabled(true);// 可触发定位并显示定位层
        initLocation();

        mMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (marker == null) {
                    marker = mMap.addMarker(new MarkerOptions().position(latLng));
                } else {
                    marker.setPosition(latLng);
                }
            }
        });
        search = (AutoCompleteTextView) findViewById(R.id.search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String keyword = charSequence.toString().trim();
                query = new PoiSearch.Query(keyword, "", "");
                PoiSearch poiSearch = new PoiSearch(PublishTaskStepOneActivity.this, query);
                poiSearch.setOnPoiSearchListener(PublishTaskStepOneActivity.this);
                poiSearch.searchPOIAsyn();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        stepOneFinishButton = (Button) findViewById(R.id.first_step_finish_button);
        stepOneFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  将从地图上选到的地址信息，存放在DataManager中。留作后用。
                //然后跳转到第二步的页面中
                if (marker != null && marker.getPosition() != null) {
                    Location location = new Location();
                    location.setLatitude(marker.getPosition().latitude);
                    location.setLongitude(marker.getPosition().longitude);
                    Task task = new Task();
                    task.setLocation(location);
                    DataManager.getInstance().setNewTask(task);
                    Intent nextPage = new Intent(PublishTaskStepOneActivity.this, PublishTaskStepTwoActivity.class);
                    startActivity(nextPage);
                } else {
                    Toast.makeText(PublishTaskStepOneActivity.this, "请先选择地点", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /**
     * 初始化定位
     */
    private void initLocation() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(1000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        mLocationClient.onDestroy();
        if (DataManager.getInstance().getNewTask() != null) {
            DataManager.getInstance().getNewTask().setLocation(null);
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        mListener = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        if (myLatLng != null) {
            mMapView.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 17));
        }
        isFirstLoc = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                if (isFirstLoc) {
                    mListener.onLocationChanged(aMapLocation);
                    LatLng latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                    myLatLng = latLng;
                    isFirstLoc = false;
                }
            }
        }
    }


    @Override
    public void onGetInputtips(List<Tip> tipList, int rCode) {
        if (rCode == 1000) {
            adapter = new LocationSearchedAdapter(PublishTaskStepOneActivity.this, R.layout.tiplist, tipList);
            search.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Tip tip = adapter.getItem(i);
                    LatLng latLng = new LatLng(tip.getPoint().getLatitude(), tip.getPoint().getLongitude());
                    if (marker == null) {
                        marker = mMap.addMarker(new MarkerOptions().position(latLng));
                    } else {
                        marker.setPosition(latLng);
                    }
                    search.setText(tip.getName());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                }
            });
        }
    }

    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        if (rCode == 1000) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    List<PoiItem> poiItems = result.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    final LocationAdapter adapter2 = new LocationAdapter(PublishTaskStepOneActivity.this, R.layout.tiplist, poiItems);
                    search.setAdapter(adapter2);
                    adapter2.notifyDataSetChanged();
                    search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            PoiItem poi = adapter2.getItem(i);
                            LatLng latLng = new LatLng(poi.getLatLonPoint().getLatitude(), poi.getLatLonPoint().getLongitude());
                            if (marker == null) {
                                marker = mMap.addMarker(new MarkerOptions().position(latLng));
                            } else {
                                marker.setPosition(latLng);
                            }
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                        }
                    });
                }
            }
        }

    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) { }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
