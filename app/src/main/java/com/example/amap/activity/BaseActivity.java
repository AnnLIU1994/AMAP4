package com.example.amap.activity;

import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.example.amap.CustomApplcation;
import com.example.amap.R;
import com.example.amap.bean.User;
import cn.bmob.im.MyBmobUserManager;
import com.example.amap.util.CollectionUtils;
import com.example.amap.view.HeaderLayout;
import com.example.amap.view.dialog.DialogTips;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;


/** 基类
 * @ClassName: BaseActivity
 * @Description: TODO
 * @author smile
 * @date 2014-6-13 下午5:05:38
 */
public class BaseActivity extends FragmentActivity {

	MyBmobUserManager userManager;
	BmobChatManager manager;

	CustomApplcation mApplication;
	protected HeaderLayout mHeaderLayout;

	protected int mScreenWidth;
	protected int mScreenHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		userManager = MyBmobUserManager.getInstance(this);
		manager = BmobChatManager.getInstance(this);
		mApplication = CustomApplcation.getInstance();
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		mScreenWidth = metric.widthPixels;
		mScreenHeight = metric.heightPixels;
	}

	Toast mToast;

	public void ShowToast(final String text) {
		if (!TextUtils.isEmpty(text)) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (mToast == null) {
						mToast = Toast.makeText(getApplicationContext(), text,
								Toast.LENGTH_LONG);
					} else {
						mToast.setText(text);
					}
					mToast.show();
				}
			});

		}
	}

	public void ShowToast(final int resId) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (mToast == null) {
					mToast = Toast.makeText(BaseActivity.this.getApplicationContext(), resId,
							Toast.LENGTH_LONG);
				} else {
					mToast.setText(resId);
				}
				mToast.show();
			}
		});
	}

	/** 打Log
	 * ShowLog
	 * @return void
	 * @throws
	 */
	public void ShowLog(String msg){
		Log.i("zjx",msg);
	}

	/**
	 * 只有title initTopBarLayoutByTitle
	 * @Title: initTopBarLayoutByTitle
	 * @throws
	 */
	public void initTopBarForOnlyTitle(String titleName) {
		mHeaderLayout = (HeaderLayout)findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderLayout.HeaderStyle.DEFAULT_TITLE);
		mHeaderLayout.setDefaultTitle(titleName);
	}

	/**
	 * 初始化标题栏-带左右按钮
	 * @return void
	 * @throws
	 */
	public void initTopBarForBoth(String titleName, int rightDrawableId,String text,
								  HeaderLayout.onRightImageButtonClickListener listener) {
		mHeaderLayout = (HeaderLayout)findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderLayout.HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
		mHeaderLayout.setTitleAndLeftImageButton(titleName,
				R.drawable.base_action_bar_back_bg_selector,
				new OnLeftButtonClickListener());
		mHeaderLayout.setTitleAndRightButton(titleName, rightDrawableId,text,
				listener);
	}

	public void initTopBarForBoth(String titleName, int rightDrawableId,
								  HeaderLayout.onRightImageButtonClickListener listener) {
		mHeaderLayout = (HeaderLayout)findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderLayout.HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
		mHeaderLayout.setTitleAndLeftImageButton(titleName,
				R.drawable.base_action_bar_back_bg_selector,
				new OnLeftButtonClickListener());
		mHeaderLayout.setTitleAndRightImageButton(titleName, rightDrawableId,
				listener);
	}

	/**
	 * 只有左边按钮和Title initTopBarLayout
	 *
	 * @throws
	 */
	public void initTopBarForLeft(String titleName) {
		mHeaderLayout = (HeaderLayout)findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderLayout.HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
		mHeaderLayout.setTitleAndLeftImageButton(titleName,
				R.drawable.base_action_bar_back_bg_selector,
				new OnLeftButtonClickListener());
	}

	/** 显示下线的对话框
	 * showOfflineDialog
	 * @return void
	 * @throws
	 */
	public void showOfflineDialog(final Context context) {
		DialogTips dialog = new DialogTips(this,"您的账号已在其他设备上登录!", "重新登录");
		// 设置成功事件
		dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int userId) {
				CustomApplcation.getInstance().logout();
				startActivity(new Intent(context, LoginActivity.class));
				finish();
				dialogInterface.dismiss();
			}
		});
		// 显示确认对话框
		dialog.show();
		dialog = null;
	}

	// 左边按钮的点击事件
	public class OnLeftButtonClickListener implements
			HeaderLayout.onLeftImageButtonClickListener {

		@Override
		public void onClick() {
			finish();
		}
	}

	public void startAnimActivity(Class<?> cla) {
		this.startActivity(new Intent(this, cla));
	}

	public void startAnimActivity(Intent intent) {
		this.startActivity(intent);
	}
	/** 用于登陆或者自动登陆情况下的用户资料及好友资料的检测更新
	 * @Title: updateUserInfos
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	public void updateUserInfos(){
		//更新地理位置信息
		updateUserLocation();
		//查询该用户的好友列表(这个好友列表是去除黑名单用户的),目前支持的查询好友个数为100，如需修改请在调用这个方法前设置BmobConfig.LIMIT_CONTACTS即可。
		//这里默认采取的是登陆成功之后即将好于列表存储到数据库中，并更新到当前内存中,
		userManager.queryCurrentContactList(new FindListener<BmobChatUser>() {

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				if(arg0==BmobConfig.CODE_COMMON_NONE){
					ShowLog(arg1);
				}else{
					ShowLog("查询好友列表失败："+arg1);
				}
			}

			@Override
			public void onSuccess(List<BmobChatUser> arg0) {
				// TODO Auto-generated method stub
				// 保存到application中方便比较
				CustomApplcation.getInstance().setContactList(CollectionUtils.list2map(arg0));
			}
		});
	}

	/** 更新用户的经纬度信息
	 * @Title: uploadLocation
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	public void updateUserLocation(){
		if(CustomApplcation.lastPoint!=null){
			String savex  = mApplication.getAmapx();
			String savey = mApplication.getAmapy();
			String savez  = mApplication.getAmapz();
			String newX = String.valueOf(CustomApplcation.lastPoint.getX());
			String newY = String.valueOf(CustomApplcation.lastPoint.getY());
			String newZ = String.valueOf(CustomApplcation.lastPoint.getZ());
//			ShowLog("saveLatitude ="+saveLatitude+",saveLongtitude = "+saveLongtitude);
//			ShowLog("newLat ="+newLat+",newLong = "+newLong);
			ShowLog("try to update location:"+ mApplication.getAmapx()+"&"+CustomApplcation.lastPoint);
			if(!savex.equals(newX)|| !savey.equals(newY)|| !savez.equals(newZ)){//只有位置有变化就更新当前位置，达到实时更新的目的
				User u = (User) userManager.getCurrentUser(User.class);
				final User user = new User();
				user.setaMapPoint(CustomApplcation.lastPoint);
				user.setObjectId(u.getObjectId());
				user.update(this,new UpdateListener() {
					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						CustomApplcation.getInstance().setAmapx(String.valueOf(user.getaMapPoint().getX()));
						CustomApplcation.getInstance().setAmapy(String.valueOf(user.getaMapPoint().getY()));
						CustomApplcation.getInstance().setAmapz(String.valueOf(user.getaMapPoint().getZ()));
//						ShowLog("经纬度更新成功");
					}
					@Override
					public void onFailure(int code, String msg) {
						// TODO Auto-generated method stub
//						ShowLog("经纬度更新 失败:"+msg);
					}
				});
			}else{
//				ShowLog("用户位置未发生过变化");
			}
		}
	}
}
