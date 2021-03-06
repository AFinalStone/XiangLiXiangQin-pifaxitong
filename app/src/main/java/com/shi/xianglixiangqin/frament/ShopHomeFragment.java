package com.shi.xianglixiangqin.frament;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afinalstone.androidstudy.view.roolpager.RollViewPager;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.shi.xianglixiangqin.R;
import com.shi.xianglixiangqin.activity.GoodsDetailGeneralActivity;
import com.shi.xianglixiangqin.activity.GoodsDetailSportActivity;
import com.shi.xianglixiangqin.activity.SearchGoodsActivity;
import com.shi.xianglixiangqin.activity.ShopActivity;
import com.shi.xianglixiangqin.activity.ShopRecommendActivity;
import com.shi.xianglixiangqin.activity.SportSaleBuyGroupActivity;
import com.shi.xianglixiangqin.activity.SportSaleBuyTimeLimitedActivity;
import com.shi.xianglixiangqin.adapter.MyBaseAdapter;
import com.shi.xianglixiangqin.app.MyApplication;
import com.shi.xianglixiangqin.interfaceImpl.OnConnectServerStateListener;
import com.shi.xianglixiangqin.json.JSONResultBaseModel;
import com.shi.xianglixiangqin.model.GoodsGeneralModel;
import com.shi.xianglixiangqin.model.GoodsGroupModel;
import com.shi.xianglixiangqin.model.GoodsSportModel;
import com.shi.xianglixiangqin.model.ShopInfoModel;
import com.shi.xianglixiangqin.task.ConnectCustomServiceAsyncTask;
import com.shi.xianglixiangqin.task.ConnectGoodsServiceAsyncTask;
import com.shi.xianglixiangqin.task.ConnectServiceUtil;
import com.shi.xianglixiangqin.util.ImagerLoaderUtil;
import com.shi.xianglixiangqin.util.InformationCodeUtil;
import com.shi.xianglixiangqin.util.LogUtil;
import com.shi.xianglixiangqin.util.StringUtil;
import com.shi.xianglixiangqin.util.TimeUtil;
import com.shi.xianglixiangqin.util.ToastUtil;
import com.shi.xianglixiangqin.view.ScrollListView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShopHomeFragment extends MyBaseFragment<ShopActivity> implements OnConnectServerStateListener<Integer>, SwipeRefreshLayout.OnRefreshListener {

    /**
     * swipeRefreshLayout
     **/
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    /**
     * 店铺Log图片
     **/
    @BindView(R.id.iv_shopLogo)
    ImageView iv_shopLogo;
    /**
     * 全部商品数量
     **/
    @BindView(R.id.tv_shopNumGoods)
    TextView tv_shopNumGoods;
    /**
     * 搜索内容EditText
     **/
    @BindView(R.id.et_searchContext)
    EditText et_searchContext;

    /**
     * 通知
     **/
    @BindView(R.id.tv_informMsg)
    TextView tv_informMsg;

    private List<TrumpetNoticeModel> listData_InforMsg;
    /**
     * 疯狂秒杀外围的控件
     **/
    @BindView(R.id.relativeLayout_buyCrazy)
    RelativeLayout relativeLayout_buyCrazy;
    /**
     * 疯狂秒杀GridView
     **/
    @BindView(R.id.gridView_buyCrazy)
    GridView gridView_buyCrazy;
    /**
     * 疯狂秒杀数据
     **/
    private List<GoodsSportModel> listData_buyCrazy;
    /**
     * 疯狂秒杀适配器
     **/
    private AdapterBuyCrazy adapterBuyCrazy;

    /**
     * 团购中心标题外围的控件
     **/
    @BindView(R.id.relativeLayout_goodsByGroupBuy)
    RelativeLayout relativeLayout_goodsByGroupBuy;
    /**
     * 精品团购ListView
     **/
    @BindView(R.id.listView_goodsByGroupBuy)
    ScrollListView listView_goodsByGroupBuy;
    /**
     * 精品团购数据
     **/
    private List<GoodsSportModel> listData_goodsByGroupBuy;
    /**
     * 精品团购适配器
     **/
    private AdapterGoodsByGroupBuy adapterGoodsByGroupBuy;

    /**
     * 限时抢购标题外围的控件
     **/
    @BindView(R.id.relativeLayout_salesByTimeLimited)
    RelativeLayout relativeLayout_salesByTimeLimited;
    /**
     * 限时抢购GridView
     **/
    @BindView(R.id.gridView_saleByTimeLimited)
    GridView gridView_saleByTimeLimited;
    /**
     * 限时抢购数据
     **/
    private List<GoodsSportModel> listData_saleByTimeLimited;
    /**
     * 限时抢购适配器
     **/
    private AdapterSalesByTimeLimited adapterSaleByTimeLimited;

    /**
     * 查看更多新品推荐外围的RelativeLayouts
     **/
    @BindView(R.id.relativeLayout_moreNewGoodsData)
    RelativeLayout relativeLayout_moreNewGoodsData;

    /**
     * 轮播图
     **/
    @BindView(R.id.rollViewpager)
    RollViewPager rollViewpager;
    List<String> listData_rollView;
    /**
     * 资源圈推荐店铺
     **/
    @BindView(R.id.iv_shopRecommend)
    ImageView iv_shopRecommend;
    /**
     * 特价商品ListView
     **/
    @BindView(R.id.listview_specialsPricesGoods)
    ScrollListView listview_specialsPricesGoods;
    private List<GoodsGeneralModel> listDataSpecialsPricesGoods;
    private AdapterSpecialsPricesGoods adapterSpecialsPricesGoods;
    /**
     * 新品推荐GridView
     **/
    @BindView(R.id.gridView_newProductsRecommendation)
    GridView gridView_newProductsRecommendation;
    private List<GoodsGeneralModel> listDataNewProductsRecommendation;
    private AdapterNewGoodsPush adapterNewProductsRecommendation;
    /**
     * 普通商品ListView
     **/
    @BindView(R.id.listview_generalGoods)
    ScrollListView listview_generalGoods;
    private List<GoodsGroupModel> listDataGeneralGoods;
    private AdapterGeneralGoods adapterGeneralGoods;

    ShopInfoModel currentShopInfoModel;

    private View rootView;


    /**
     * 当前服务器时间
     **/
    private long time_current;
    /**为了让通知全部显示**/
    private int position_currentInformMsg;
    /**是否包含活动商品**/
    private boolean IfHaveSportGoods = false;
    private int MESSAGE_01 = 1;
    //使用Handler的延时效果，每隔一秒刷新一下适配器，以此产生倒计时效果
    private Handler handler_timeCurrent = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            time_current = time_current + 1000;
            if(IfHaveSportGoods){
                adapterBuyCrazy.notifyDataSetChanged();
            }
//            if(time_current%10000 == 9000){
//                 if(listData_InforMsg.size() > 1){
//                    if(position_currentInformMsg < listData_InforMsg.size()){
//                        tv_informMsg.setText(listData_InforMsg.get(position_currentInformMsg).getTitle());
//                        position_currentInformMsg += 1;
//                    }else{
//                        position_currentInformMsg = 0;
//                    }
//                }
//            }
            handler_timeCurrent.sendEmptyMessageDelayed(MESSAGE_01, 1000);
        }
    };

    public ShopHomeFragment(){

    }

    @Override
    public View initView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = View.inflate(mActivity, R.layout.pager_shop_home, null);
            ButterKnife.bind(this, rootView);

            et_searchContext.setInputType(InputType.TYPE_NULL);
            iv_shopRecommend.setLayoutParams(new LinearLayout.LayoutParams(mActivity.displayDeviceWidth, mActivity.displayDeviceWidth * 4 / 15));

            //初始化整点秒杀
            listData_buyCrazy = new ArrayList<GoodsSportModel>();
            adapterBuyCrazy = new AdapterBuyCrazy(mActivity, listData_buyCrazy);
            gridView_buyCrazy.setAdapter(adapterBuyCrazy);
            gridView_buyCrazy.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    int PlatformActionID = listData_buyCrazy.get(position).getPlatformActionID();
                    Intent intent = new Intent(mActivity, GoodsDetailSportActivity.class);
                    intent.putExtra(InformationCodeUtil.IntentPlatformActionID, PlatformActionID);
                    intent.putExtra(InformationCodeUtil.IntentPlatformActionType,
                            InformationCodeUtil.PlatformActionType_MesKill);
                    mActivity.startActivity(intent);
                }
            });

            //初始化团购中心
            listData_goodsByGroupBuy = new ArrayList<GoodsSportModel>();
            adapterGoodsByGroupBuy = new AdapterGoodsByGroupBuy(mActivity, listData_goodsByGroupBuy);
            listView_goodsByGroupBuy.setAdapter(adapterGoodsByGroupBuy);
            listView_goodsByGroupBuy.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    int PlatformActionID = listData_goodsByGroupBuy.get(position).getPlatformActionID();
                    Intent intent = new Intent(mActivity, GoodsDetailSportActivity.class);
                    intent.putExtra(InformationCodeUtil.IntentPlatformActionID, PlatformActionID);
                    intent.putExtra(InformationCodeUtil.IntentPlatformActionType,
                            InformationCodeUtil.PlatformActionType_GroupCentre);
                    mActivity.startActivity(intent);
                }
            });

            //初始化限时抢购
            listData_saleByTimeLimited = new ArrayList<GoodsSportModel>();
            adapterSaleByTimeLimited = new AdapterSalesByTimeLimited(mActivity, listData_saleByTimeLimited);
            gridView_saleByTimeLimited.setAdapter(adapterSaleByTimeLimited);
            gridView_saleByTimeLimited.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    int PlatformActionID = listData_saleByTimeLimited.get(position).getPlatformActionID();
                    Intent intent = new Intent(mActivity, GoodsDetailSportActivity.class);
                    intent.putExtra(InformationCodeUtil.IntentPlatformActionID, PlatformActionID);
                    intent.putExtra(InformationCodeUtil.IntentPlatformActionType,
                            InformationCodeUtil.PlatformActionType_SaleByTimeLimited);
                    mActivity.startActivity(intent);
                }
            });
            //特价商品
            listDataSpecialsPricesGoods = new ArrayList<GoodsGeneralModel>();
            adapterSpecialsPricesGoods = new AdapterSpecialsPricesGoods(mActivity, listDataSpecialsPricesGoods);
            listview_specialsPricesGoods.setAdapter(adapterSpecialsPricesGoods);
            listview_specialsPricesGoods.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    Intent intent = new Intent(mActivity, GoodsDetailGeneralActivity.class);
                    intent.putExtra(InformationCodeUtil.IntentGoodsID, listDataSpecialsPricesGoods.get(position).getDjLsh());
                    mActivity.startActivity(intent);
                }

            });
            //新品推荐
            listDataNewProductsRecommendation = new ArrayList<GoodsGeneralModel>();
            adapterNewProductsRecommendation = new AdapterNewGoodsPush(mActivity, listDataNewProductsRecommendation);
            gridView_newProductsRecommendation.setAdapter(adapterNewProductsRecommendation);
            gridView_newProductsRecommendation.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Intent intent = new Intent(mActivity, GoodsDetailGeneralActivity.class);
                    intent.putExtra(InformationCodeUtil.IntentGoodsID, listDataNewProductsRecommendation.get(position).getDjLsh());
                    mActivity.startActivity(intent);
                }
            });
            //普通商品列表
            listDataGeneralGoods = new ArrayList<GoodsGroupModel>();
            adapterGeneralGoods = new AdapterGeneralGoods(mActivity, listDataGeneralGoods);
            listview_generalGoods.setAdapter(adapterGeneralGoods);
            swipeRefreshLayout.setColorSchemeColors(Color.RED
                    , Color.GREEN
                    , Color.BLUE
                    , Color.YELLOW
                    , Color.CYAN
                    , 0xFFFE5D14
                    , Color.MAGENTA);
            swipeRefreshLayout.setOnRefreshListener(this);
        }
        return rootView;
    }

    @Override
    public void initData() {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                onRefresh();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        handler_timeCurrent.removeCallbacksAndMessages(null);
    }

    @Override
    public void onRefresh() {
        getData(InformationCodeUtil.methodNameStoreHomeIndex);
    }

    private void getData(String methodName) {

        if (methodName == InformationCodeUtil.methodNameStoreHomeIndex) {
            SoapObject soapObject = new SoapObject(ConnectServiceUtil.NAMESPACE, methodName);
            soapObject.addProperty("shopID", mActivity.currentShopID);
            ConnectGoodsServiceAsyncTask connectGoodsServiceAsyncTask = new ConnectGoodsServiceAsyncTask(
                    mActivity, this, soapObject, methodName);
            connectGoodsServiceAsyncTask.initProgressDialog(false);
            connectGoodsServiceAsyncTask.execute();
        }

        if (methodName == InformationCodeUtil.methodNameDelegateShopAllGoods) {
            SoapObject soapObject = new SoapObject(ConnectServiceUtil.NAMESPACE, methodName);
            soapObject.addProperty("customID", MyApplication.getmCustomModel(mActivity).getDjLsh());
            soapObject.addProperty("openKey", MyApplication.getmCustomModel(mActivity).getOpenKey());
            soapObject.addProperty("shopID", mActivity.currentShopID);
            soapObject.addProperty("goodsIDS", "");
            ConnectCustomServiceAsyncTask connectCustomServiceAsyncTask = new ConnectCustomServiceAsyncTask(
                    mActivity, this, soapObject, methodName);
            connectCustomServiceAsyncTask.execute();
        }

    }

    @OnClick({R.id.btn_agencyAllGoods, R.id.et_searchContext, R.id.btn_search
            , R.id.tv_moreNewGoodsData, R.id.iv_openConverSation, R.id.iv_shopRecommend
            , R.id.linearLayout_moreSalesByTimeLimited,R.id.linearLayout_goodsByGroupBuy})
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
               case R.id.btn_agencyAllGoods:
                getData(InformationCodeUtil.methodNameDelegateShopAllGoods);
                break;
            case R.id.linearLayout_moreSalesByTimeLimited:
                intent = new Intent(mActivity,SportSaleBuyTimeLimitedActivity.class);
                intent.putExtra(InformationCodeUtil.IntentSportSaleByActivityCurrentShopID
                        ,currentShopInfoModel.getDjLsh());
                mActivity.startActivity(intent);
                break;
            case R.id.linearLayout_goodsByGroupBuy:
                intent = new Intent(mActivity, SportSaleBuyGroupActivity.class);
                intent.putExtra(InformationCodeUtil.IntentSportSaleByActivityCurrentShopID
                        ,currentShopInfoModel.getDjLsh());
                mActivity.startActivity(intent);
                break;
            case R.id.et_searchContext:
            case R.id.btn_search:
            case R.id.tv_moreNewGoodsData:
                intent = new Intent(mActivity, SearchGoodsActivity.class);
                intent.putExtra(InformationCodeUtil.IntentSearchGoodsCurrentShopID, currentShopInfoModel.getDjLsh());
                mActivity.startActivity(intent);
                break;
            case R.id.iv_openConverSation:
                RongIM.getInstance().startConversation(mActivity, Conversation.ConversationType.PRIVATE,
                        "" + currentShopInfoModel.getShopUserID(), currentShopInfoModel.getShopName());
                break;
            case R.id.iv_shopRecommend:
                intent = new Intent(mActivity, ShopRecommendActivity.class);
                intent.putExtra(InformationCodeUtil.IntentShopRecommendActivityCurrentShopID,currentShopInfoModel.getDjLsh());
                mActivity.startActivity(intent);
                break;
            default:
                break;
        }
    }


    @Override
    public void connectServiceSuccessful(String returnString,
                                         String methodName, Integer state, boolean whetherRefresh) {
        LogUtil.LogShitou("店铺首页",returnString);
        if (methodName == InformationCodeUtil.methodNameStoreHomeIndex) {
            try {
                Gson gson = new Gson();
                currentShopInfoModel = gson.fromJson(returnString, ShopInfoModel.class);
                mActivity.currentShopUserID = currentShopInfoModel.getShopUserID();

                refreshTitleLogo(currentShopInfoModel);
                refreshTrupmet(currentShopInfoModel.getNotices(), gson);
                refreshSportGoods(currentShopInfoModel.getActs(), gson);
                refreshShopGeneralGoods(currentShopInfoModel.getModules(), gson);
                refreshRollView(currentShopInfoModel.getAdvList(), gson);
            } catch (Exception e) {
                e.printStackTrace();
            }
            swipeRefreshLayout.setRefreshing(false);
            return;
        }

        if (methodName == InformationCodeUtil.methodNameDelegateShopAllGoods) {
            if (returnString.contains("代理成功")) {
                ToastUtil.show(mActivity, "成功代理本店所有商品");
            }
            return;
        }

    }

    @Override
    public void connectServiceFailed(String returnStrError, String methodName, Integer state, boolean whetherRefresh) {
        if (methodName == InformationCodeUtil.methodNameStoreHomeIndex) {
            swipeRefreshLayout.setRefreshing(false);
            ToastUtil.show(mActivity, "网络异常，主页数据请求失败");
            return;
        }

        if (methodName == InformationCodeUtil.methodNameDelegateShopAllGoods) {
            ToastUtil.show(mActivity, "网络异常，一键代理失败");
            return;
        }
    }

    @Override
    public void connectServiceCancelled(String returnString,
                                        String methodName, Integer state, boolean whetherRefresh) {
        if (methodName == InformationCodeUtil.methodNameDelegateShopAllGoods) {
            if (returnString.contains("代理成功")) {
                ToastUtil.show(mActivity, "成功代理本店所有商品");
            }
            return;
        }
    }

    private void refreshTitleLogo(ShopInfoModel shopInfoModel){

        tv_shopNumGoods.setText(shopInfoModel.getCountAll() + " 件");
        if(!StringUtil.isEmpty(shopInfoModel.getShopName())){
            mActivity.tv_title.setText(shopInfoModel.getShopName());
        }else{
            mActivity.tv_title.setText("首页");
        }
        if(!StringUtil.isEmpty(shopInfoModel.getLogo())){
            iv_shopLogo.setVisibility(View.VISIBLE);
            ImagerLoaderUtil.getInstance(mActivity).displayMyImage(shopInfoModel.getLogo(), iv_shopLogo);
        }else{
            iv_shopLogo.setVisibility(View.GONE);
        }
    }

    /**刷新喇叭通知信息**/
    private void refreshTrupmet(String strNotices, Gson gson){
        try {
            listData_InforMsg = gson.fromJson( strNotices, new TypeToken<List<TrumpetNoticeModel>>(){}.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(listData_InforMsg == null){
            listData_InforMsg = new ArrayList<TrumpetNoticeModel>();
            listData_InforMsg.add(new TrumpetNoticeModel("通知",1,"暂无通知"));
        }
        position_currentInformMsg = 0;
        tv_informMsg.setText(listData_InforMsg.get(position_currentInformMsg).getTitle());
    }

    /**刷新活动商品**/
    private void refreshSportGoods(String strActs, Gson gson) {
        LogUtil.LogShitou("活动商品",strActs);
        try {
            relativeLayout_buyCrazy.setVisibility(View.GONE);
            relativeLayout_goodsByGroupBuy.setVisibility(View.GONE);
            relativeLayout_salesByTimeLimited.setVisibility(View.GONE);
            handler_timeCurrent.removeCallbacksAndMessages(null);
            IfHaveSportGoods = false;
            listData_buyCrazy.clear();
            listData_goodsByGroupBuy.clear();
            listData_saleByTimeLimited.clear();
            //JSONObject负责解决获取JSON键值对
            JSONArray jsonArray = new JSONArray(strActs);
            //循环遍历活动商品数组内容
            for (int i = 0; i < jsonArray.length(); i++) {
                //使用泛型解析JSON数据，主要用到了TypeToken这个对象
                JSONResultBaseModel<GoodsSportModel> mJSONResultModel = gson.fromJson
                        (jsonArray.get(i).toString(), new TypeToken<JSONResultBaseModel<GoodsSportModel>>() {
                        }.getType());
                if ("整点秒杀".equals(mJSONResultModel.getTitle())) {
                    listData_buyCrazy.addAll(mJSONResultModel.getList());
                    relativeLayout_buyCrazy.setVisibility(View.VISIBLE);
                    setBuyCrazyGridView(listData_buyCrazy, gridView_buyCrazy);
                    IfHaveSportGoods = true;
                    time_current = TimeUtil.getTimeDate(listData_buyCrazy.get(0).getPresentTime()).getTime();
                }
                if ("精品团购".equals(mJSONResultModel.getTitle())) {
                    listData_goodsByGroupBuy.addAll(mJSONResultModel.getList());
                    relativeLayout_goodsByGroupBuy.setVisibility(View.VISIBLE);
                    time_current = TimeUtil.getTimeDate(listData_goodsByGroupBuy.get(0).getPresentTime()).getTime();
                }
                if ("限时抢购".equals(mJSONResultModel.getTitle())) {
                    listData_saleByTimeLimited.addAll(mJSONResultModel.getList());
                    relativeLayout_salesByTimeLimited.setVisibility(View.VISIBLE);
                    setSalesByTimeLimitedGridView(listData_saleByTimeLimited, gridView_saleByTimeLimited);
                    time_current = TimeUtil.getTimeDate(listData_saleByTimeLimited.get(0).getPresentTime()).getTime();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        adapterBuyCrazy.notifyDataSetChanged();
        adapterGoodsByGroupBuy.notifyDataSetChanged();
        adapterSaleByTimeLimited.notifyDataSetChanged();
        if(IfHaveSportGoods){
            handler_timeCurrent.sendEmptyMessageDelayed(MESSAGE_01,100);
        }
    }



    /**
     * 刷新普通商品
     **/
    private void refreshShopGeneralGoods(String strAllModuls, Gson gson) {

        try {
            relativeLayout_moreNewGoodsData.setVisibility(View.GONE);
            listDataSpecialsPricesGoods.clear();
            listDataNewProductsRecommendation.clear();
            listDataGeneralGoods.clear();

            List<String> listData_generalGoods =
                    gson.fromJson(strAllModuls,new TypeToken<List<String>>(){}.getType());
            for (int i=0; i<listData_generalGoods.size(); i++){

                JSONResultBaseModel<GoodsGeneralModel> jSONResultModel =
                        gson.fromJson(listData_generalGoods.get(i)
                                ,new TypeToken<JSONResultBaseModel<GoodsGeneralModel>>(){}.getType());

                if("特价商品".equals(jSONResultModel.getTitle())){
                    refreshSpecialsPricesGoods(jSONResultModel);
                    continue;
                }
                if("新品推荐".equals(jSONResultModel.getTitle())){
                    refreshNewProductsRecommendation(jSONResultModel);
                    continue;
                }
                if("全部商品".equals(jSONResultModel.getTitle())
                        || "台式机".equals(jSONResultModel.getTitle())
                        || "笔记本".equals(jSONResultModel.getTitle())){
                    refreshGeneralAllGoods(jSONResultModel);
                    continue;
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 刷新台式机，笔记本，全部商品内容
     **/
    private void refreshGeneralAllGoods(JSONResultBaseModel<GoodsGeneralModel> mJSONProductModel) {

        try {
            GoodsGroupModel productModelGroup = new GoodsGroupModel();
            productModelGroup.setClassID(mJSONProductModel.getID());
            productModelGroup.setProductModeType(mJSONProductModel.getTitle());
            List<GoodsGeneralModel> listGoods = mJSONProductModel.getList();
            if(listGoods != null && listGoods.size() != 0 ){
                productModelGroup.setListProductModels(mJSONProductModel.getList());
                listDataGeneralGoods.add(productModelGroup);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        adapterGeneralGoods.notifyDataSetChanged();
    }

    /**
     * 刷新新品推荐内容
     **/
    private void refreshNewProductsRecommendation(JSONResultBaseModel<GoodsGeneralModel> mJSONProductModel) {
        try {
            listDataNewProductsRecommendation.addAll(mJSONProductModel.getList());
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        if (listDataNewProductsRecommendation.size() != 0) {
            relativeLayout_moreNewGoodsData.setVisibility(View.VISIBLE);
        }
        setNewProductsGridView(listDataNewProductsRecommendation, mActivity, gridView_newProductsRecommendation);
        adapterNewProductsRecommendation.notifyDataSetChanged();
    }

    /**
     * 设置新品推荐中的GirdView的宽和高,使其能够横向滑动
     */
    public void setNewProductsGridView(List<GoodsGeneralModel> returnModel, Activity mContext, GridView gridView) {
        int size = returnModel.size();
        if(size == 0){
            return;
        }
        int itemWidth = mActivity.displayDeviceWidth * 19 / 40;
        int gridViewWidth = size * itemWidth;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                gridViewWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        gridView.setLayoutParams(params); // 设置GirdView布局参数,横向布局的关键
        gridView.setColumnWidth(itemWidth); // 设置列表项宽
        gridView.setHorizontalSpacing(1); // 设置列表项水平间距
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setNumColumns(size); // 设置列数量=列表集合数
    }

    /**
     * 刷新特价商品内容
     **/
    private void refreshSpecialsPricesGoods(JSONResultBaseModel<GoodsGeneralModel> mJSONProductModel) {
        try {
            listDataSpecialsPricesGoods.addAll(mJSONProductModel.getList());
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        adapterSpecialsPricesGoods.notifyDataSetChanged();
    }

    /**
     * 刷新轮播图内容
     **/
    private void refreshRollView(String rollData, Gson gson) {

        if (listData_rollView == null) {
            try {
                JSONArray jsonArray = new JSONObject(rollData).getJSONArray("List");
                listData_rollView = new ArrayList<String>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject mJSONAdvModel = jsonArray.getJSONObject(i);
                    String imageUrl = mJSONAdvModel.getString("ImgUrl");
                    if (imageUrl != null) {
                        listData_rollView.add(imageUrl);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (listData_rollView.size() == 0) {
                rollViewpager.setVisibility(View.GONE);
            } else {
                rollViewpager.setVisibility(View.VISIBLE);
                rollViewpager.setImageUris(listData_rollView);
                rollViewpager.startRoll();
            }
        }

    }


    public class AdapterGeneralGoods extends MyBaseAdapter<GoodsGroupModel> {

        public AdapterGeneralGoods(Context mContext, List listData) {
            super(mContext, listData);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.item_shop_generalgoods_listview, null);
                holder = new ViewHolder();
                holder.tv_type = (TextView) convertView.findViewById(R.id.tv_type);
                holder.tv_moreNewGoodsData = (TextView) convertView.findViewById(R.id.tv_moreNewGoodsData);

                holder.relativeLayoutView0.RelativeLayout = (RelativeLayout) convertView.findViewById(R.id.RelativeLayout1);
                holder.relativeLayoutView0.iv_specialPricesGoods = (ImageView) convertView.findViewById(R.id.iv_specialPricesGoods1);
                holder.relativeLayoutView0.tv_specialPricesGoodsName = (TextView) convertView.findViewById(R.id.tv_specialPricesGoodsName1);
                holder.relativeLayoutView0.tv_specialPricesGoodsPrices = (TextView) convertView.findViewById(R.id.tv_specialPricesGoodsPrices1);
                holder.relativeLayoutView0.tv_SaledCount = (TextView) convertView.findViewById(R.id.tv_seeDataDetail1);

                holder.relativeLayoutView1.RelativeLayout = (RelativeLayout) convertView.findViewById(R.id.RelativeLayout2);
                holder.relativeLayoutView1.iv_specialPricesGoods = (ImageView) convertView.findViewById(R.id.iv_specialPricesGoods2);
                holder.relativeLayoutView1.tv_specialPricesGoodsName = (TextView) convertView.findViewById(R.id.tv_specialPricesGoodsName2);
                holder.relativeLayoutView1.tv_specialPricesGoodsPrices = (TextView) convertView.findViewById(R.id.tv_specialPricesGoodsPrices2);
                holder.relativeLayoutView1.tv_SaledCount = (TextView) convertView.findViewById(R.id.tv_seeDataDetail2);

                holder.relativeLayoutView2.RelativeLayout = (RelativeLayout) convertView.findViewById(R.id.RelativeLayout3);
                holder.relativeLayoutView2.iv_specialPricesGoods = (ImageView) convertView.findViewById(R.id.iv_specialPricesGoods3);
                holder.relativeLayoutView2.tv_specialPricesGoodsName = (TextView) convertView.findViewById(R.id.tv_specialPricesGoodsName3);
                holder.relativeLayoutView2.tv_specialPricesGoodsPrices = (TextView) convertView.findViewById(R.id.tv_specialPricesGoodsPrices3);
                holder.relativeLayoutView2.tv_SaledCount = (TextView) convertView.findViewById(R.id.tv_seeDataDetail3);

                holder.relativeLayoutView3.RelativeLayout = (RelativeLayout) convertView.findViewById(R.id.RelativeLayout4);
                holder.relativeLayoutView3.iv_specialPricesGoods = (ImageView) convertView.findViewById(R.id.iv_specialPricesGoods4);
                holder.relativeLayoutView3.tv_specialPricesGoodsName = (TextView) convertView.findViewById(R.id.tv_specialPricesGoodsName4);
                holder.relativeLayoutView3.tv_specialPricesGoodsPrices = (TextView) convertView.findViewById(R.id.tv_specialPricesGoodsPrices4);
                holder.relativeLayoutView3.tv_SaledCount = (TextView) convertView.findViewById(R.id.tv_seeDataDetail4);

                holder.relativeLayoutView4.RelativeLayout = (RelativeLayout) convertView.findViewById(R.id.RelativeLayout5);
                holder.relativeLayoutView4.iv_specialPricesGoods = (ImageView) convertView.findViewById(R.id.iv_specialPricesGoods5);
                holder.relativeLayoutView4.tv_specialPricesGoodsName = (TextView) convertView.findViewById(R.id.tv_specialPricesGoodsName5);
                holder.relativeLayoutView4.tv_specialPricesGoodsPrices = (TextView) convertView.findViewById(R.id.tv_specialPricesGoodsPrices5);
                holder.relativeLayoutView4.tv_SaledCount = (TextView) convertView.findViewById(R.id.tv_seeDataDetail5);

                holder.relativeLayoutView5.RelativeLayout = (RelativeLayout) convertView.findViewById(R.id.RelativeLayout6);
                holder.relativeLayoutView5.iv_specialPricesGoods = (ImageView) convertView.findViewById(R.id.iv_specialPricesGoods6);
                holder.relativeLayoutView5.tv_specialPricesGoodsName = (TextView) convertView.findViewById(R.id.tv_specialPricesGoodsName6);
                holder.relativeLayoutView5.tv_specialPricesGoodsPrices = (TextView) convertView.findViewById(R.id.tv_specialPricesGoodsPrices6);
                holder.relativeLayoutView5.tv_SaledCount = (TextView) convertView.findViewById(R.id.tv_seeDataDetail5);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final GoodsGroupModel currentProductModelGroup = listData.get(position);

            holder.tv_type.setText(currentProductModelGroup.getProductModeType());
            holder.tv_moreNewGoodsData.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext,SearchGoodsActivity.class);
                    intent.putExtra(InformationCodeUtil.IntentSearchGoodsCurrentShopID, currentShopInfoModel.getDjLsh());
                    intent.putExtra(InformationCodeUtil.IntentSearchGoodsFilterClassID, currentProductModelGroup.getClassID());
                    mContext.startActivity(intent);
                }
            });

            if(currentProductModelGroup.getListProductModels() != null){

                List<GoodsGeneralModel> listModels = currentProductModelGroup.getListProductModels();
                for(int i=0; i<listModels.size() && i<6; i++){
                    final GoodsGeneralModel currentProductModel = listModels.get(i);
                    RelativeLayoutView currentRelativeLayoutView  = holder.getRelativeLayoutView(i);
                    if (currentProductModel != null ) {

                        currentRelativeLayoutView.RelativeLayout.setVisibility(View.VISIBLE);
                        ImagerLoaderUtil.getInstance(mContext).displayMyImage(
                                currentProductModel.getImgUrl(),
                                currentRelativeLayoutView.iv_specialPricesGoods);
                        currentRelativeLayoutView.tv_specialPricesGoodsName.setText(currentProductModel.getGoodsName());
                        currentRelativeLayoutView.tv_specialPricesGoodsPrices.setText("￥" + currentProductModel.getMinPrice());
                        currentRelativeLayoutView.tv_SaledCount.setText("已售" + currentProductModel.getSaledCount() + "件");
                        holder.getRelativeLayoutView(i).RelativeLayout.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(mContext, GoodsDetailGeneralActivity.class);
                                intent.putExtra(InformationCodeUtil.IntentGoodsID, currentProductModel.getDjLsh());
                                mContext.startActivity(intent);
                            }
                        });
                    } else {
                        holder.getRelativeLayoutView(i).RelativeLayout.setVisibility(View.GONE);
                    }
                }
            }

            return convertView;
        }

        private class ViewHolder {
            /**
             * 商品类型
             **/
            public TextView tv_type;
            /**
             * 更多
             **/
            public TextView tv_moreNewGoodsData;

            public RelativeLayoutView relativeLayoutView0 = new RelativeLayoutView();
            public RelativeLayoutView relativeLayoutView1 = new RelativeLayoutView();
            public RelativeLayoutView relativeLayoutView2 = new RelativeLayoutView();
            public RelativeLayoutView relativeLayoutView3 = new RelativeLayoutView();
            public RelativeLayoutView relativeLayoutView4 = new RelativeLayoutView();
            public RelativeLayoutView relativeLayoutView5 = new RelativeLayoutView();

            public RelativeLayoutView getRelativeLayoutView(int index) {
                RelativeLayoutView returnRelativeLayoutView = null;
                switch (index) {
                    case 0:
                        returnRelativeLayoutView = relativeLayoutView0;
                        break;
                    case 1:
                        returnRelativeLayoutView = relativeLayoutView1;
                        break;
                    case 2:
                        returnRelativeLayoutView = relativeLayoutView2;
                        break;
                    case 3:
                        returnRelativeLayoutView = relativeLayoutView3;
                        break;
                    case 4:
                        returnRelativeLayoutView = relativeLayoutView4;
                        break;
                    case 5:
                        returnRelativeLayoutView = relativeLayoutView5;
                        break;
                    default:
                        break;
                }
                return returnRelativeLayoutView;
            }
        }

        public class RelativeLayoutView {
            public RelativeLayout RelativeLayout;
            public ImageView iv_specialPricesGoods;
            public TextView tv_specialPricesGoodsName;
            public TextView tv_specialPricesGoodsPrices;
            public TextView tv_SaledCount;
        }
    }

    /*****
     * @author SHI
     *         2016-2-17 15:34:28
     */
    public class AdapterNewGoodsPush extends MyBaseAdapter<GoodsGeneralModel> {

        public AdapterNewGoodsPush(Context mContext, List<GoodsGeneralModel> listData) {
            super(mContext, listData);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.item_adapter_newgoods_push, null);
                holder = new ViewHolder();
                holder.iv_specialPricesGoods = (ImageView) convertView.findViewById(R.id.iv_specialPricesGoods);
                holder.tv_specialPricesGoodsName = (TextView) convertView.findViewById(R.id.tv_specialPricesGoodsName);
                holder.tv_specialPricesGoodsPrices = (TextView) convertView.findViewById(R.id.tv_specialPricesGoodsPrices);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            GoodsGeneralModel currentProductModel = listData.get(position);
            ImagerLoaderUtil.getInstance(mContext).displayMyImage(currentProductModel.getImgUrl(), holder.iv_specialPricesGoods);
            holder.tv_specialPricesGoodsName.setText(currentProductModel.getGoodsName());
            holder.tv_specialPricesGoodsPrices.setText("￥" + currentProductModel.getMinPrice());
            return convertView;
        }

        private class ViewHolder {
            /**
             * 特价商品图标
             **/
            ImageView iv_specialPricesGoods;
            /**
             * 特价商品名称
             **/
            TextView tv_specialPricesGoodsName;
            /**
             * 特价商品价格
             **/
            TextView tv_specialPricesGoodsPrices;
        }
    }

    /*****
     * @author SHI
     *         2016-2-17 15:34:42
     */
    public class AdapterSpecialsPricesGoods extends MyBaseAdapter<GoodsGeneralModel> {

        public AdapterSpecialsPricesGoods(Context mContext,
                                          List<GoodsGeneralModel> listData) {
            super(mContext, listData);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(mContext,
                        R.layout.item_shop_specialspricesgoods_listview, null);
                holder = new ViewHolder();
                holder.iv_specialPricesGoods = (ImageView) convertView.findViewById(R.id.iv_specialPricesGoods);
                holder.tv_specialPricesGoodsTitle = (TextView) convertView.findViewById(R.id.tv_specialPricesGoodsTitle);
                holder.tv_specialPricesGoodsName = (TextView) convertView.findViewById(R.id.tv_specialPricesGoodsName);
                holder.tv_specialPricesGoodsDesc = (TextView) convertView.findViewById(R.id.tv_specialPricesGoodsDesc);
                holder.tv_specialPricesGoodsPrices = (TextView) convertView.findViewById(R.id.tv_specialPricesGoodsPrices);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            GoodsGeneralModel currentProductModel = listData.get(position);
            ImagerLoaderUtil.getInstance(mContext).displayMyImage(currentProductModel.getImgUrl(), holder.iv_specialPricesGoods);
            holder.tv_specialPricesGoodsName.setText(currentProductModel.getGoodsName());
            holder.tv_specialPricesGoodsDesc.setText(currentProductModel.getGoodsDesc());
            holder.tv_specialPricesGoodsPrices.setText("￥" + currentProductModel.getMinPrice());
            return convertView;
        }

        private class ViewHolder {
            /**
             * 特价商品图标
             **/
            public ImageView iv_specialPricesGoods;
            /**
             * 特价商品标题
             **/
            public TextView tv_specialPricesGoodsTitle;
            /**
             * 特价商品名称
             **/
            public TextView tv_specialPricesGoodsName;
            /**
             * 特价商品描述
             **/
            public TextView tv_specialPricesGoodsDesc;
            /**
             * 特价商品价格
             **/
            public TextView tv_specialPricesGoodsPrices;
        }

    }

    /****
     * 整点秒杀适配器
     *
     * @author SHI 2016-3-2 16:12:21
     */
    public class AdapterBuyCrazy extends MyBaseAdapter<GoodsSportModel> {

        public AdapterBuyCrazy(Context mContext, List<GoodsSportModel> listData) {
            super(mContext, listData);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(mContext,
                        R.layout.item_adapter_sport_sale_by_crazy01, null);
                holder = new ViewHolder();
                holder.tv_hour = (TextView) convertView.findViewById(R.id.tv_hour);
                holder.tv_minute = (TextView) convertView.findViewById(R.id.tv_minute);
                holder.tv_second = (TextView) convertView.findViewById(R.id.tv_second);
                holder.linearLayout_timeToBeginBuyCray = (LinearLayout)
                        convertView.findViewById(R.id.linearLayout_timeToBeginBuyCray);
                holder.iv_productImage = (ImageView) convertView
                        .findViewById(R.id.iv_productImage);
                holder.tv_productName = (TextView) convertView
                        .findViewById(R.id.tv_productName);
                holder.tv_productPrice = (TextView) convertView
                        .findViewById(R.id.tv_productPrice);
                holder.tv_productOriginalPrice = (TextView) convertView
                        .findViewById(R.id.tv_productOriginalPrice);
                holder.btn_buyCrazy = (Button) convertView
                        .findViewById(R.id.btn_buyCrazy);
                holder.tv_crazyBuying = (TextView) convertView
                        .findViewById(R.id.tv_crazyBuying);
                holder.iv_IfBuyCrazyState = (ImageView) convertView
                        .findViewById(R.id.iv_IfBuyCrazyState);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final GoodsSportModel currentGoodsInfoActModel = listData.get(position);
            // 更新倒计时控件times_current
            updateTextView(holder,currentGoodsInfoActModel);

            if(currentGoodsInfoActModel.getImages() != null && currentGoodsInfoActModel.getImages().size()>0) {
                ImagerLoaderUtil.getInstance(mContext).displayMyImage(
                        currentGoodsInfoActModel.getImages().get(0), holder.iv_productImage);
            }

            holder.tv_productName.setText(currentGoodsInfoActModel.getGoodsName());
            holder.tv_productPrice.setText("￥"
                    + (int)currentGoodsInfoActModel.getPrice());
            holder.tv_productOriginalPrice.setText("￥"+(int)currentGoodsInfoActModel.getOriginalPrice());
            holder.tv_productOriginalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);


            return convertView;
        }

        /****
         * 刷新整点秒杀倒计时控件,这个时候活动截至时间还未到
         */
        public void updateTextView(ViewHolder holder, GoodsSportModel mGoodsInfoActModel) {
            long time_begin = TimeUtil.getTimeDate(mGoodsInfoActModel.getBeginTime()).getTime();
            long time_end = TimeUtil.getTimeDate(mGoodsInfoActModel.getEndTime()).getTime();
//			LogUtil.LogShitou("整点秒杀当前时间", ""+time_current);
//			LogUtil.LogShitou("整点秒杀结束时间", ""+time_end);
            //活动截至时间还未到
            if (time_end > time_current) {
                long time_remains = time_begin - time_current;
                if (time_remains <= 0) {
                    if (mGoodsInfoActModel.isIsRunning()) {
                        //活动正在进行，可以秒杀
                        holder.btn_buyCrazy.setEnabled(true);
                        holder.btn_buyCrazy.setText(R.string.clickMesKill);
                        holder.tv_crazyBuying.setVisibility(View.VISIBLE);
                        holder.linearLayout_timeToBeginBuyCray.setVisibility(View.INVISIBLE);
                        holder.iv_IfBuyCrazyState.setVisibility(View.INVISIBLE);
                    } else {
                        //活动已经结束,因为商品被抢光
                        holder.btn_buyCrazy.setEnabled(false);
                        holder.btn_buyCrazy.setText(R.string.haveFinishRob);
                        holder.tv_crazyBuying.setVisibility(View.INVISIBLE);
                        holder.linearLayout_timeToBeginBuyCray.setVisibility(View.VISIBLE);
                        holder.iv_IfBuyCrazyState.setVisibility(View.VISIBLE);
                        holder.iv_IfBuyCrazyState.setImageResource(R.drawable.icon_buy_crazy_clear);
                    }
                    holder.tv_hour.setText("00");
                    holder.tv_minute.setText("00");
                    holder.tv_second.setText("00");
                    return;
                }
                //活动即将开始
                holder.btn_buyCrazy.setEnabled(false);
                holder.btn_buyCrazy.setText(R.string.justToBegin);
                holder.tv_crazyBuying.setVisibility(View.INVISIBLE);
                holder.iv_IfBuyCrazyState.setVisibility(View.INVISIBLE);
                holder.linearLayout_timeToBeginBuyCray.setVisibility(View.VISIBLE);
                //秒钟
                long time_second = (time_remains / 1000) % 60;
                String str_second;
                if (time_second < 10) {
                    str_second = "0" + time_second;
                } else {
                    str_second = "" + time_second;
                }
                long time_temp = ((time_remains / 1000) - time_second) / 60;
                //分钟
                long time_minute = time_temp % 60;
                String str_minute;
                if (time_minute < 10) {
                    str_minute = "0" + time_minute;
                } else {
                    str_minute = "" + time_minute;
                }
                time_temp = (time_temp - time_minute) / 60;
                //小时
                long time_hour = time_temp;
                String str_hour;
                if (time_hour < 10) {
                    str_hour = "0" + time_hour;
                } else {
                    str_hour = "" + time_hour;
                }
                holder.tv_hour.setText(str_hour);
                holder.tv_minute.setText(str_minute);
                holder.tv_second.setText(str_second);
            } else {
                //活动已经结束,因为活动截至时间到了
                holder.btn_buyCrazy.setEnabled(false);
                holder.btn_buyCrazy.setText(R.string.haveFinish);
                holder.tv_crazyBuying.setVisibility(View.INVISIBLE);
                holder.linearLayout_timeToBeginBuyCray.setVisibility(View.VISIBLE);
                holder.iv_IfBuyCrazyState.setVisibility(View.VISIBLE);
                holder.iv_IfBuyCrazyState.setImageResource(R.drawable.icon_buy_crazy_finished);
            }


        }

        private class ViewHolder {
            /**
             * 剩余开抢小时
             **/
            TextView tv_hour;
            /**
             * 剩余开抢分钟
             **/
            TextView tv_minute;
            /**
             * 剩余开抢秒钟
             **/
            TextView tv_second;
            /**
             * 剩余时间外围布局控件
             **/
            LinearLayout linearLayout_timeToBeginBuyCray;
            /**
             * 商品图片
             **/
            ImageView iv_productImage;
            /**
             * 商品名称
             **/
            TextView tv_productName;
            /**
             * 商品价格
             **/
            TextView tv_productPrice;
            TextView tv_productOriginalPrice;
            /**
             * 秒杀按钮
             **/
            Button btn_buyCrazy;
            /**
             * 是否正在疯狂秒杀图标
             **/
            TextView tv_crazyBuying;
            /**
             * 商品是否已抢光或已结束
             **/
            ImageView iv_IfBuyCrazyState;
        }
    }

    /**
     * 设置整点秒杀GirdView的宽和高,使其能够横向滑动
     */
    public void setBuyCrazyGridView(List returnModel, GridView gridView) {
        int size = returnModel.size();
        if(size == 0){
            return;
        }
        int itemWidth =  mActivity.displayDeviceWidth / 2;
        int gridViewWidth =  size * itemWidth;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                gridViewWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        gridView.setLayoutParams(params); // 设置GirdView布局参数,横向布局的关键
        gridView.setColumnWidth(itemWidth); // 设置列表项宽
        gridView.setHorizontalSpacing(1); // 设置列表项水平间距
        gridView.setStretchMode(GridView.NO_STRETCH);// spacingWidthUniform
        gridView.setNumColumns(size); // 设置列数量=列表集合数
    }

    /****
     * 团购中心适配器
     *
     * @author SHI 2016-3-2 16:13:09
     */
    public class AdapterGoodsByGroupBuy extends MyBaseAdapter<GoodsSportModel> {

        public AdapterGoodsByGroupBuy(Context mContext,
                                      List<GoodsSportModel> listData) {
            super(mContext, listData);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(mContext,
                        R.layout.item_adapter_sport_sale_by_group01, null);
                holder = new ViewHolder();
                holder.iv_productImage = (ImageView) convertView
                        .findViewById(R.id.iv_productImage);
                holder.tv_productName = (TextView) convertView
                        .findViewById(R.id.tv_productName);
                holder.tv_productPrice_new = (TextView) convertView
                        .findViewById(R.id.tv_productPrice_new);
                holder.tv_productPrice_old = (TextView) convertView
                        .findViewById(R.id.tv_productPrice_old);
                holder.tv_NumOfjoinGroup = (TextView) convertView
                        .findViewById(R.id.tv_numOfJoinGroup);
                holder.btn_justToJoinGroup = (Button) convertView
                        .findViewById(R.id.btn_justToJoinGroup);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final GoodsSportModel currentGoodsInfoActModel = listData.get(position);
            if(currentGoodsInfoActModel.getImages() != null && currentGoodsInfoActModel.getImages().size()>0) {
                ImagerLoaderUtil.getInstance(mContext).displayMyImage(
                        currentGoodsInfoActModel.getImages().get(0), holder.iv_productImage);
            }
            updateGroupCentreTextView(holder,currentGoodsInfoActModel);

            holder.tv_productName.setText(currentGoodsInfoActModel.getGoodsName());
            holder.tv_productPrice_new.setText("￥"
                    + (int)currentGoodsInfoActModel.getPrice());
            holder.tv_productPrice_old.setText("￥"
                    + (int)currentGoodsInfoActModel.getOriginalPrice());
            holder.tv_productPrice_old.getPaint().setFlags(
                    Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tv_NumOfjoinGroup.setText(currentGoodsInfoActModel
                    .getJoinNum() + "件已参团");
            return convertView;
        }

        /**
         * 刷新精品团购中心数据
         */
        private void updateGroupCentreTextView(ViewHolder holder, GoodsSportModel mGoodsInfoActModel) {
            long times_begin = TimeUtil.getTimeDate(mGoodsInfoActModel.getBeginTime()).getTime();
//			LogUtil.LogShitou("精品团购当前时间", ""+time_current);
//			LogUtil.LogShitou("精品团购结束时间", ""+time_end);

            if (times_begin - time_current > 0) {
                //活动还未开始
                holder.btn_justToJoinGroup.setEnabled(false);
                holder.btn_justToJoinGroup.setText(R.string.justToBegin);

            } else {

                long time_end = TimeUtil.getTimeDate(mGoodsInfoActModel.getEndTime()).getTime();
                if (time_end > time_current) {
                    //团购是否正在进行
                    if (mGoodsInfoActModel.isIsRunning()) {
                        holder.btn_justToJoinGroup.setEnabled(true);
                        holder.btn_justToJoinGroup.setText(R.string.justToJoinGroup);
                    } else {
                        //活动结束，因为服务器后台人为停止了
                        holder.btn_justToJoinGroup.setEnabled(false);
                        //团购是否成功
                        if (mGoodsInfoActModel.isIsGrouped()) {
                            holder.btn_justToJoinGroup.setText(R.string.joinGroupSuccess);
                        } else {
                            holder.btn_justToJoinGroup.setText(R.string.joinGroupFailed);
                        }
                    }
                } else {
                    //活动结束，因为活动截至时间到了
                    holder.btn_justToJoinGroup.setEnabled(false);
                    //团购是否成功
                    if (mGoodsInfoActModel.isIsGrouped()) {
                        holder.btn_justToJoinGroup.setText(R.string.joinGroupSuccess);
                    } else {
                        holder.btn_justToJoinGroup.setText(R.string.joinGroupFailed);
                    }
                }
            }

        }

        private class ViewHolder {
            /**
             * 商品图片
             **/
            ImageView iv_productImage;
            /**
             * 商品名称
             **/
            TextView tv_productName;
            /**
             * 商品价格
             **/
            TextView tv_productPrice_new;
            TextView tv_productPrice_old;
            /**
             * 参团数量
             **/
            TextView tv_NumOfjoinGroup;
            /**
             * 马上参团
             **/
            Button btn_justToJoinGroup;
        }
    }

    /****
     * 限时抢购适配器
     *
     * @author SHI 2016-3-2 16:13:35
     */
    public class AdapterSalesByTimeLimited extends MyBaseAdapter<GoodsSportModel> {

        public AdapterSalesByTimeLimited(Context mContext,
                                         List<GoodsSportModel> listData) {
            super(mContext, listData);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(mContext,
                        R.layout.item_adapter_sport_sale_by_timelimited01, null);
                holder = new ViewHolder();
                holder.iv_productImage = (ImageView) convertView
                        .findViewById(R.id.iv_productImage);
                holder.tv_productName = (TextView) convertView
                        .findViewById(R.id.tv_productName);
                holder.tv_productPrice_new = (TextView) convertView
                        .findViewById(R.id.tv_productPrice_new);
                holder.tv_productPrice_old = (TextView) convertView
                        .findViewById(R.id.tv_productPrice_old);
//				holder.tv_timeRemain = (TextView) convertView
//						.findViewById(R.id.tv_timeRemain);
                holder.btn_justToBuy = (Button) convertView
                        .findViewById(R.id.btn_justToRob);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final GoodsSportModel currentGoodsInfoActModel = listData.get(position);

            updateTextView(holder,currentGoodsInfoActModel);

            if(currentGoodsInfoActModel.getImages() != null && currentGoodsInfoActModel.getImages().size()>0) {
                ImagerLoaderUtil.getInstance(mContext).displayMyImage(
                        currentGoodsInfoActModel.getImages().get(0), holder.iv_productImage);
            }
            holder.tv_productName.setText(currentGoodsInfoActModel.getGoodsName());
            holder.tv_productPrice_new.setText("￥"
                    + (int)currentGoodsInfoActModel.getPrice());
            holder.tv_productPrice_old.setText("￥"
                    + (int)currentGoodsInfoActModel.getOriginalPrice());
            holder.tv_productPrice_old.getPaint().setFlags(
                    Paint.STRIKE_THRU_TEXT_FLAG);

            return convertView;
        }


        /****
         * 刷新限时抢购倒计时控件
         */
        public void updateTextView(ViewHolder holder, GoodsSportModel mGoodsInfoActModel) {

            long time_begin = TimeUtil.getTimeDate(mGoodsInfoActModel.getBeginTime()).getTime();
            long time_end = TimeUtil.getTimeDate(mGoodsInfoActModel.getEndTime()).getTime();
//			LogUtil.LogShitou("限时抢购当前时间", ""+time_current);
//			LogUtil.LogShitou("限时抢购结束时间", ""+time_end);
            if (time_begin - time_current > 0) {
                //活动还未开始
                holder.btn_justToBuy.setEnabled(false);
                holder.btn_justToBuy.setText(R.string.justToBegin);
//				holder.tv_timeRemain.setText("00:00:00");

            } else {
                //活动开始
                long times_remain = time_end - time_current;
                if (times_remain <= 0) {
                    //活动结束,因为限时抢购截至时间到了
                    holder.btn_justToBuy.setEnabled(false);
                    holder.btn_justToBuy.setText(R.string.haveFinish);
//					holder.tv_timeRemain.setText("00:00:00");
//					holder.tv_timeRemain.setText(mGoodsInfoActModel.getEndTime()+"结束");
                    return;
                }
                if (mGoodsInfoActModel.isIsRunning()) {
                    //活动正在进行，可以秒杀
                    holder.btn_justToBuy.setEnabled(true);
                    holder.btn_justToBuy.setText(R.string.justToRob);
                } else {
                    //活动已经结束,因为商品已被卖光
                    holder.btn_justToBuy.setEnabled(false);
                    holder.btn_justToBuy.setText(R.string.haveFinishRob);
                }

            }

        }

        private class ViewHolder {

            /**
             * 商品图片
             **/
            ImageView iv_productImage;
            /**
             * 商品名称
             **/
            TextView tv_productName;
            /**
             * 商品价格
             **/
            TextView tv_productPrice_new;
            TextView tv_productPrice_old;
            /** 限时抢购剩余时间 **/
//			TextView tv_timeRemain;
            /**
             * 马上开抢按钮
             **/
            Button btn_justToBuy;
        }
    }


    /**
     * 设置限时抢购GirdView的宽和高,使其能够横向滑动
     */
    public void setSalesByTimeLimitedGridView(List returnModel, GridView gridView) {
        int size = returnModel.size();
        if(size == 0){
            return;
        }
        int itemWidth = mActivity.displayDeviceWidth * 2 / 5;
        int gridViewWidth = (size * (itemWidth+1));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                gridViewWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        gridView.setLayoutParams(params); // 设置GirdView布局参数,横向布局的关键
        gridView.setColumnWidth(itemWidth); // 设置列表项宽
        gridView.setHorizontalSpacing(1); // 设置列表项水平间距
        gridView.setStretchMode(GridView.NO_STRETCH);// spacingWidthUniform
        gridView.setNumColumns(size); // 设置列数量=列表集合数
    }

    /**通知对象**/
    private class TrumpetNoticeModel{
        private String Content;
        private int ID;
        private String Title;

        public TrumpetNoticeModel(String content, int ID, String title) {
            Content = content;
            this.ID = ID;
            Title = title;
        }

        public String getContent() {
            return Content;
        }

        public void setContent(String content) {
            Content = content;
        }

        public int getID() {
            return ID;
        }

        public void setID(int ID) {
            this.ID = ID;
        }

        public String getTitle() {
            return Title;
        }

        public void setTitle(String title) {
            Title = title;
        }
    }
}
