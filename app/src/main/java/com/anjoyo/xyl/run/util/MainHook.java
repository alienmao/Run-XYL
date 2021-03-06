package com.anjoyo.xyl.run.util;

import android.app.AndroidAppHelper;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.anjoyo.xyl.run.BuildConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class MainHook implements IXposedHookLoadPackage, IXposedHookZygoteInit {
    public static int m = 50, max = 99990;
    public static int weixinCount = 1, qqCount = 1, ledongCount = 1, yuedongCount = 1, pinganCount = 1, codoonCount = 1, zhifubaoCount = 1;
    public static boolean isWeixin, isQQ, isLedong, isYuedong, isPingan, isCodoon, isWeibo, isAlipay, isZfbOn;
    public static int zfbSteps = 0;
    static long addValue;
    static String userId;
    static boolean incrementValue = true;
    static XSharedPreferences mXSharedPreferences;
    static Context context = null;
    public static Object sObject;
    private static float count = 0;
    public static int c;
    public static int e;
    public static int f;
    public final Object b;
    public int d;
    public long g;
    public long h;
    private static boolean controlIsFromMockProvider;

    static {
        c = 200;
        e = 1;
        f = Integer.MAX_VALUE;
        mXSharedPreferences = new XSharedPreferences("com.anjoyo.xyl.run");
    }

    public MainHook() {
        this.d = 10;
        this.b = XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", null), "currentActivityThread", new Object[0]);
    }

    public static void initData() {
        mXSharedPreferences.reload();
        mXSharedPreferences.makeWorldReadable();
        m = Integer.valueOf(
                mXSharedPreferences.getString("magnification", "50"))
                .intValue();
        incrementValue = mXSharedPreferences.getBoolean("increment", false);
        addValue = Long.valueOf(mXSharedPreferences.getString("addvalue", "0")).intValue();
        userId = mXSharedPreferences.getString("userid", "");
        controlIsFromMockProvider = mXSharedPreferences.getBoolean("controlIsFromMockProvider", false);
        isWeixin = mXSharedPreferences.getBoolean("weixin", false);
        isQQ = mXSharedPreferences.getBoolean("qq", false);
        isLedong = mXSharedPreferences.getBoolean("ledong", false);
        isYuedong = mXSharedPreferences.getBoolean("yuedong", false);
        isPingan = mXSharedPreferences.getBoolean("pingan", false);
        isCodoon = mXSharedPreferences.getBoolean("codoon", false);
        isWeibo = mXSharedPreferences.getBoolean("weibo", false);
        isAlipay = mXSharedPreferences.getBoolean("alipay", false);

        isZfbOn = mXSharedPreferences.getBoolean("isZfbOn", false);
        zfbSteps = Long.valueOf(mXSharedPreferences.getString("zfbSteps", "0")).intValue();

        controlIsFromMockProvider = mXSharedPreferences.getBoolean("controlIsFromMockProvider", false);
        if (BuildConfig.DEBUG)
            Log.d("xyl", "xyl-run：magnification=" + m + "incrementValue=" + incrementValue + ";addValue=" + addValue + ";userId=" + userId + ";isZfbOn=" + isZfbOn + ";zfbSteps=" + zfbSteps + ";controlIsFromMockProvider=" + controlIsFromMockProvider);
    }

    public void handleYDAddNum(Class<?> openSignEL) {
        XposedBridge.hookAllMethods(openSignEL, "makeSig",
                new XC_MethodHook() {
                    @SuppressWarnings("unchecked")
                    @Override
                    protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param)
                            throws Throwable {
                        if (BuildConfig.DEBUG)
                            Log.d("xyl", "makeSig str==" + param.args[0].toString() + "++++str2=" + param.args[1].toString() + "++++str3=" + param.args[3].toString());
                        initData();
                        HashMap<String, String> hashMap = (HashMap<String, String>) param.args[2];
                        if (hashMap != null) {
                            if (BuildConfig.DEBUG)
                                Log.d("xyl", "makeSig hashMap=="
                                        + hashMap.toString());
                        }
                        if (MainHook.addValue < 1) {
                            return;
                        }
                        if (!TextUtils.equals(
                                "/sport/report_runner_info_step_batch",
                                param.args[1].toString())) {
                            return;
                        }
                        if (hashMap != null
                                && hashMap.containsKey("steps_array_json")) {
                            JSONArray jsonArray = new JSONArray(hashMap
                                    .get("steps_array_json"));
                            if (jsonArray == null
                                    || jsonArray.length() != 1) {
                                return;
                            }
                            if (!TextUtils.isEmpty(MainHook.userId)) {
                                hashMap.put("user_id", MainHook.userId);
                                hashMap.put("client_user_id", MainHook.userId);
                            }
                            JSONObject jsonObject = jsonArray
                                    .getJSONObject(0);
                            jsonObject.remove("step");
//                            jsonObject.remove("cost_time");
                            jsonObject.put("step", MainHook.addValue);
//                            jsonObject.put("cost_time", 864000);
                            hashMap.put("steps_array_json", "["
                                    + jsonObject.toString() + "]");
                            XposedBridge.log("newhashMap=="
                                    + hashMap.toString());
                            MainHook.addValue = 0;
                            String motifyContent = "0";
                            Intent intent = new Intent("com.anjoyo.xyl.run.SETTING_CHANGED");
                            intent.putExtra("content", motifyContent);
                            intent.putExtra("type", 1);
                            if (MainHook.context != null) {
                                MainHook.context.sendBroadcast(intent);
                            }
                        }
                    }

                });
    }

    public void handleYDGetSignkey(Class<?> ydConfigs) {
        XposedBridge.hookAllMethods(ydConfigs, "getSignkey",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        String signkey = param.getResult().toString();
                        Intent intent = new Intent("com.anjoyo.xyl.run.yd_info");
                        intent.putExtra("action", 0);
                        intent.putExtra("signkey", signkey);
                        if (MainHook.context != null) {
                            MainHook.context.sendBroadcast(intent);
                        }
                    }
                }
        );
    }

    public void handleYDGetXyy(Class<?> ydAccount) {
        XposedBridge.hookAllMethods(ydAccount, "xyy",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Integer uid = (Integer) param.thisObject.getClass().getDeclaredMethod("uid", new Class[]{}).invoke(param.thisObject);
                        String xyy = param.getResult().toString();
                        Intent intent = new Intent("com.anjoyo.xyl.run.yd_info");
                        intent.putExtra("action", 1);
                        intent.putExtra("uid", uid);
                        intent.putExtra("xyy", xyy);
                        if (MainHook.context != null) {
                            MainHook.context.sendBroadcast(intent);
                        } else {
                            Context context = (Context) XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", null), "currentActivityThread", new Object[0]), "getSystemContext", new Object[0]);
                            if (context != null) {
                                context.sendBroadcast(intent);
                            }
                        }
                    }
                }
        );
//        XposedBridge.hookAllMethods(ydAccount, "uid",
//                new XC_MethodHook() {
//                    @Override
//                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                        super.afterHookedMethod(param);
//                        String uid = param.getResult().toString();
//                        Log.d("xxx", "getuid uid==>"
//                                + uid);
//                    }
//                }
//        );
//        XposedBridge.hookAllMethods(ydAccount, "getUserObject",
//                new XC_MethodHook() {
//                    @Override
//                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                        super.afterHookedMethod(param);
//                        Object userObject = param.getResult();
//                        Log.d("xxx", "userObject userObject==>" + userObject.toString());
//                        Class classObj = userObject.getClass();
//                        //获取id属性
//                        Field idF = classObj.getDeclaredField("user_id");
//                        //打破封装
//                        idF.setAccessible(true); //使用反射机制可以打破封装性，导致了java对象的属性不安全。
//                        Object uid = idF.get(userObject);
//                        Log.d("xxx", "uid uid0000000==>" + uid.toString());
//                        uid = classObj.getDeclaredMethod("getUserId", new Class[]{}).invoke(userObject);
//                        Log.d("xxx", "uid uid11111111111==>" + uid.toString());
//                    }
//                }
//        );
    }

    public void handleIsFromMockProvider(Class<?> locationEL) {
        XposedBridge.hookAllMethods(locationEL, "isFromMockProvider",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        param.setResult(false);
                        super.afterHookedMethod(param);
                    }
                }
        );
    }

    public void bindReceicver() {
        try {
//            context = (Context) XposedHelpers.callMethod(XposedHelpers
//                            .callStaticMethod(XposedHelpers.findClass(
//                                    "android.app.ActivityThread", null),
//                                    "currentActivityThread", new Object[0]),
//                    "getSystemContext", new Object[0]);
            context = (Context) XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", null), "currentActivityThread", new Object[0]), "getSystemContext", new Object[0]);
        } catch (Throwable e) {
            // TODO: handle exception
            e.printStackTrace();
            context = null;
        }
    }

    public void handleLoadPackage(final LoadPackageParam loadPackageParam) {
        if (context == null) {
            bindReceicver();
        }
        if (BuildConfig.DEBUG)
            Log.d("xyl", controlIsFromMockProvider + "++loadpackageName==" + loadPackageParam.packageName);
        if (TextUtils.equals("com.anjoyo.xyl.run", loadPackageParam.packageName)) {
            final Class<?> NotiPrefrenceChangeUtil = XposedHelpers.findClass(
                    "com.anjoyo.xyl.run.util.NotiPrefrenceChangeUtil",
                    loadPackageParam.classLoader);
            if (NotiPrefrenceChangeUtil != null) {
                XposedBridge.hookAllMethods(NotiPrefrenceChangeUtil, "refreshPrefrence", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        XposedBridge.log("xyl-run:收到小熊跑步配置改变消息");
                        initData();
                    }
                });
                XposedHelpers.findAndHookMethod(NotiPrefrenceChangeUtil, "isModuleActive", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        XposedBridge.log("xyl-run:active");
                        param.setResult(Boolean.valueOf(true));
                    }
                });
            }
        }
        if (TextUtils.equals("com.yuedong.sport", loadPackageParam.packageName)) {
            try {
                final Class<?> openSignEL = XposedHelpers.findClass(
                        "com.yuedong.common.utils.OpenSign",
                        loadPackageParam.classLoader);
                if (openSignEL != null)
                    handleYDAddNum(openSignEL);
                Class<?> ydConfigs = XposedHelpers.findClass(
                        "com.yuedong.sport.common.Configs",
                        loadPackageParam.classLoader);
                if (ydConfigs != null) {
                    handleYDGetSignkey(ydConfigs);
                }
                Class<?> Account = XposedHelpers.findClass(
                        "com.yuedong.sport.controller.account.Account",
                        loadPackageParam.classLoader);
                if (Account != null) {
                    handleYDGetXyy(Account);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
//        if (loadPackageParam.packageName.equals("com.eg.android.AlipayGphone")) {
//            hookZfbSteps(loadPackageParam);
//        }
        if (controlIsFromMockProvider) {
            //屏蔽android.location.Location.isFromMockProvider()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                Class<?> locationEL = XposedHelpers.findClass(
                        "android.location.Location",
                        loadPackageParam.classLoader);
                if (locationEL != null) {
                    handleIsFromMockProvider(locationEL);
                }
            }
        }
        Class<?> sensorEL = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            sensorEL = XposedHelpers.findClass(
                    "android.hardware.SystemSensorManager$SensorEventQueue",
                    loadPackageParam.classLoader);
        }
        if (sensorEL != null) {
            XposedBridge.hookAllMethods(sensorEL, "dispatchSensorEvent",
                    new RunMethodHook(context, this, loadPackageParam));
        }

    }

    public void hookZfbSteps(final LoadPackageParam loadPackageParam) {
        XC_MethodReplacement xc_methodReplacement = new XC_MethodReplacement() {
            protected Object replaceHookedMethod(MethodHookParam methodHookParam) {
                return null;
            }
        };
        XC_MethodReplacement xc_methodReplacement1 = new XC_MethodReplacement() {
            protected Object replaceHookedMethod(MethodHookParam methodHookParam) {
                return null;
            }
        };
        XC_MethodHook xc_methodHook4 = new XC_MethodHook() {
            Class a;
            Class b;
            Class c;
            Class d;
            Class e;
            Class f;

            protected void afterHookedMethod(MethodHookParam methodHookParam) {
                ClassLoader classLoader = ((Context) methodHookParam.args[0]).getClassLoader();
                try {
                    a = classLoader.loadClass("com.alipay.mobile.base.security.CheckInject$1");
                    b = classLoader.loadClass("com.alipay.mobile.nebulacore.ui.H5Activity");
                    c = classLoader.loadClass("com.alipay.mobile.quinox.LauncherActivity");
                    d = classLoader.loadClass("com.alipay.mobile.healthcommon.stepcounter.MainProcessSpUtils");
                    e = classLoader.loadClass("com.alipay.mobile.healthcommon.stepcounter.MultiProcessSpUtils");
                    f = classLoader.loadClass("com.alipay.mobile.healthcommon.stepcounter.APMainStepManager");
                } catch (Exception e) {
                    XposedBridge.log("xyl-run:Class Not found:" + e.getMessage());
                }
                if (a != null) {
                    XposedHelpers.findAndHookMethod(a, "run", new Object[]{new XC_MethodReplacement() {
                        protected Object replaceHookedMethod(MethodHookParam methodHookParam) {
                            return null;
                        }
                    }});
                }
                if (c != null) {
                    XposedHelpers.findAndHookMethod(c, "onCreate", new Object[]{Bundle.class, new XC_MethodHook() {
                        protected void afterHookedMethod(MethodHookParam methodHookParam) {

                        }
                    }});
                    XposedHelpers.findAndHookMethod(c, "onResume", new Object[]{new XC_MethodHook() {
                        protected void afterHookedMethod(MethodHookParam methodHookParam) {
                            initData();
                            if (isZfbOn) {
                                XposedHelpers.callStaticMethod(d, "a", new Object[]{AndroidAppHelper.currentApplication().getApplicationContext(), "startup", Boolean.valueOf(true)});
                            }
                        }
                    }});
                    if (e != null) {
                        XposedHelpers.findAndHookMethod(e, "a", new Object[]{Context.class, String.class, String.class, new XC_MethodHook() {
                            protected void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                                Context context = (Context) methodHookParam.args[0];
                                methodHookParam.setResult(Boolean.valueOf(true));
                                super.afterHookedMethod(methodHookParam);
                            }
                        }});
                    }
                    if (b != null) {
                        XposedHelpers.findAndHookMethod(b, "onResume", new Object[]{new XC_MethodHook() {
                            protected void afterHookedMethod(MethodHookParam methodHookParam) {
                                initData();
                                if (isZfbOn) {
                                    XposedHelpers.callStaticMethod(d, "a", new Object[]{AndroidAppHelper.currentApplication().getApplicationContext(), "baseStep", "{\"steps\":" + zfbSteps + ",\"time\":" + System.currentTimeMillis() + "}"});
                                }
                            }
                        }});
                    }
                    if (f != null) {
                        XposedHelpers.findAndHookMethod(f, "deviceSupport", new Object[]{new XC_MethodHook() {

                            protected void beforeHookedMethod(MethodHookParam methodHookParam) {
                                methodHookParam.setResult(Boolean.valueOf(true));
                            }
                        }});
                    }
                }
            }
        };
        XposedHelpers.findAndHookMethod(Application.class, "attach", new Object[]{Context.class, xc_methodHook4});
        XposedHelpers.findAndHookMethod("com.alipay.mobile.base.security.d", loadPackageParam.classLoader, "onClick", new Object[]{DialogInterface.class, Integer.TYPE, xc_methodReplacement});
        XposedHelpers.findAndHookMethod("com.alipay.mobile.base.security.c", loadPackageParam.classLoader, "onClick", new Object[]{DialogInterface.class, Integer.TYPE, xc_methodReplacement1});
        XposedHelpers.findAndHookMethod("com.alipay.mobile.logmonitor.util.sensor.PedometerMonitor", loadPackageParam.classLoader, "c", new Object[]{new XC_MethodHook() {
            protected void beforeHookedMethod(MethodHookParam methodHookParam) {
                methodHookParam.setResult(Boolean.valueOf(true));
                XposedBridge.log("xyl-run:hook sensor succeed.result:" + methodHookParam.getResult());
            }
        }});
        XposedHelpers.findAndHookMethod("com.alipay.mobile.logmonitor.util.sensor.PedometerMonitor", loadPackageParam.classLoader, "a", new Object[]{String.class, Boolean.TYPE, new XC_MethodHook() {
            protected void afterHookedMethod(MethodHookParam methodHookParam) {
            }
        }});
    }

    public void initZygote(StartupParam startupParam) {
        mXSharedPreferences = new XSharedPreferences("com.anjoyo.xyl.run");
        this.mXSharedPreferences.reload();
        this.mXSharedPreferences.makeWorldReadable();
    }
}
