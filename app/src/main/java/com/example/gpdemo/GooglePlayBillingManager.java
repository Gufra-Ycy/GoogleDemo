package com.example.gpdemo;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GooglePlayBillingManager implements PurchasesUpdatedListener {

    private static String TAG = "GPBillingManager";
    private static GooglePlayBillingManager instance = null;
    private static Activity mContext = null;
    private BillingClient billingClient = null;

    private boolean isSet = false;

    private static Map<String, SkuDetails> mSkuDetailsMap = new HashMap<>();

    public static GooglePlayBillingManager getInstance(Activity context){
        mContext = context;
        if (instance == null){
            instance = new GooglePlayBillingManager();

        }
        return instance;
    }

    //连接到GooglePlay
    public boolean setUpGP(final Context context){
        Log.e(TAG,"连接到GooglePlay");
        Runnable queryToExecute = new Runnable() {
            @Override
            public void run() {
                billingClient = BillingClient.newBuilder(context).build();
                billingClient.startConnection(new BillingClientStateListener() {
                    @Override
                    public void onBillingSetupFinished(BillingResult billingResult) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            Log.d(TAG,"The BillingClient is ready");
                            // The BillingClient is ready. You can query purchases here.
                            isSet = true;
                        }else{
                            Log.e(TAG,"The BillingClient is not ready");
                        }
                    }

                    @Override
                    public void onBillingServiceDisconnected() {
                        // Try to restart the connection on the next request to
                        // Google Play by calling the startConnection() method.
                        Log.e(TAG,"onBillingServiceDisconnected");
                    }
                });
            }
        };
        queryToExecute.run();
        return isSet;
    }

    //查询商品详情
    public void queryGoods(final String productId){
        Log.e(TAG,"查询商品详情query purchases......");
        Runnable queryToExecute = new Runnable() {
            @Override
            public void run() {
                Purchase.PurchasesResult purchasesResult = billingClient.queryPurchases(BillingClient.SkuType.INAPP);
                if (purchasesResult.getPurchasesList() != null) {//有历史订单
                    Log.e(TAG, "PurchasesList is not mull");
                    for (Purchase purchases : purchasesResult.getPurchasesList()) {
                        if (purchases != null) {
                            Log.d(TAG, "历史：" + "OriginalJson：" + purchases.getOriginalJson() + "&&sku:" + purchases.getSku());
                            //有历史订单进行消耗
                            handlePurchase(purchases);
                            return;
                        }
                    }

                }
                List<String> skuList = new ArrayList<>();
                skuList.add(productId);
                SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
                billingClient.querySkuDetailsAsync(params.build(), new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                            for (SkuDetails skuDetails : skuDetailsList) {
                                mSkuDetailsMap.put(skuDetails.getSku(),skuDetails);
                                //进行支付
                                launchBilling(mContext,productId,"uid");
                            }

                        }
                    }
                });
            }
        };

    }

    //请求支付
    private void launchBilling(final Activity activity, final String productId, final String uid) {
        Runnable purchaseFlowRequest = new Runnable() {
            @Override
            public void run() {
                BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(mSkuDetailsMap.get(productId))
                        .setAccountId(uid)
                        .build();
                BillingResult result = billingClient.launchBillingFlow(activity, flowParams);
            }
        };
        purchaseFlowRequest.run();
    }

    //消耗
    void handlePurchase(Purchase purchase) {
        ConsumeParams consumeParams =
                ConsumeParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .setDeveloperPayload(purchase.getDeveloperPayload())
                        .build();
        billingClient.consumeAsync(consumeParams, new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "消耗成功");

                } else {
                    Log.e(TAG, "消耗失败&&msg：" + billingResult.getDebugMessage());

                }
            }
        });
            /*
            // Acknowledge the purchase if it hasn't already been acknowledged.
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, new AcknowledgePurchaseResponseListener() {
                    @Override
                    public void onAcknowledgePurchaseResponse(BillingResult billingResult) {

                    }
                });
            }*/
    }

    //支付回调
    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                && purchases != null) {
            for (Purchase purchase : purchases) {
                //支付成功，订单去服务端验证，验证成功之后进行消耗
                handlePurchase(purchase);
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
            //取消支付
        } else {
            // Handle any other error codes.
            //支付失败
        }
    }
}
