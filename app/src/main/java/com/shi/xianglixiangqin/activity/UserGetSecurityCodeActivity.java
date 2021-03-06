package com.shi.xianglixiangqin.activity;


import org.ksoap2.serialization.SoapObject;

import com.shi.xianglixiangqin.util.ToastUtil;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import butterknife.ButterKnife;
import butterknife.BindView;
import com.shi.xianglixiangqin.R;
import com.shi.xianglixiangqin.interfaceImpl.OnConnectServerStateListener;
import com.shi.xianglixiangqin.json.JSONResultMsgModel;
import com.shi.xianglixiangqin.task.ConnectCustomServiceAsyncTask;
import com.shi.xianglixiangqin.task.ConnectServiceUtil;
import com.shi.xianglixiangqin.util.InformationCodeUtil;
import com.shi.xianglixiangqin.util.StringUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * @action 注册第一步
 * @author SHI
 * @date  2016年4月29日 14:42:04
 */
public class UserGetSecurityCodeActivity extends MyBaseActivity implements OnClickListener, OnConnectServerStateListener<Integer> {

	/**左侧返回控件**/
	@BindView(R.id.iv_titleLeft)
	ImageView iv_titleLeft;
	/**标题**/
	@BindView(R.id.tv_title)
	TextView tv_title;
	/**手机号**/
	@BindView(R.id.et_phoneNumber)
	 EditText et_phoneNumber;
	/**验证码**/
	@BindView(R.id.et_securityCode)
	 EditText et_securityCode;
	/**获取验证码**/
	@BindView(R.id.btn_getSecurityCode)
	 Button btn_getSecurityCode;
	/**下一步**/
	@BindView(R.id.btn_nextStep)
	 Button btn_nextStep;
	
	/**获取服务器验证码的手机号*/
	String strCheckPhoneNumber;
	/**服务器返回的待核实验证码**/
	String strCheckSecurityCode;
	
	private String strPhoneNumber;
	private String strSecurityCode;
	private Class intentActivityObject;
	
	private int timeRemain;
	private int MESSAGE_01 = 1;
	private Handler handler = new Handler(new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {

			if (timeRemain > 0) {
				timeRemain--;
				btn_getSecurityCode.setText(timeRemain + " 秒后重新获取");
				handler.sendEmptyMessageDelayed(MESSAGE_01, 1000);
			}else{
				btn_getSecurityCode.setEnabled(true);
				btn_getSecurityCode.setText(R.string.getSecurityCode);
			}
			return false;
		}
	});
	@Override
	public void initView() {
		setContentView(R.layout.activity_user_get_security_code);
		ButterKnife.bind(this);
		iv_titleLeft.setOnClickListener(this);
		btn_getSecurityCode.setOnClickListener(this);
		btn_nextStep.setOnClickListener(this);
		intentActivityObject = (Class) getIntent()
				.getSerializableExtra(InformationCodeUtil.IntentGetSecurityCodeIntentObject);
	}

	@Override
	public void initData() {
		et_phoneNumber.postDelayed(new Runnable() {
			@Override
			public void run() {
				InputMethodManager inputManager =
						(InputMethodManager)et_phoneNumber.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(et_phoneNumber, 0);
			}
		}, 500);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_titleLeft:
			finish();
			break;
		case R.id.btn_getSecurityCode:
			getData(InformationCodeUtil.methodNameGetSMSCode);
			break;
		case R.id.btn_nextStep:
//			checkUserMsg();
			if(checkUserMsg())
			{
				Intent intent = new Intent(mContext,intentActivityObject);
				intent.putExtra(InformationCodeUtil.IntentPhoneNum, strPhoneNumber);
				startActivityForResult(intent,0);
			}
			break;

		default:
			break;
		}		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if(resultCode == RESULT_OK){
			Intent intent = getIntent();
			intent.putExtra(InformationCodeUtil.IntentPhoneNum,strPhoneNumber);
			setResult(RESULT_OK, intent);
			finish();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private boolean checkUserMsg(){
		strPhoneNumber = et_phoneNumber.getText().toString().trim();
		strSecurityCode = et_securityCode.getText().toString().trim();	


		if(!StringUtil.matchRegex(strPhoneNumber, InformationCodeUtil.regExpPhotoNumber)){
			et_phoneNumber.setError("请输入正确的手机号码");
			et_phoneNumber.requestFocus();
			return false;
		}
		
		//检验验证码是否合法
		if(StringUtil.isEmpty(strSecurityCode)){
			et_securityCode.setError("验证码不能为空");
			et_securityCode.requestFocus();
			return false;
		}
		
		if(!strSecurityCode.equals(strCheckSecurityCode) || StringUtil.isEmpty(strCheckSecurityCode)){
			et_securityCode.setError("输入验证码不正确,请重新输入或获取");
			et_securityCode.requestFocus();
			return false;
		}
		
		if(!strPhoneNumber.equals(strCheckPhoneNumber)){
			et_phoneNumber.setError("手机号更改，请重新获取验证码");
			et_phoneNumber.requestFocus();
			return false;
		}
		
		return true;
	}
	
	public void getData(String methodName){
		
			
		strPhoneNumber = et_phoneNumber.getText().toString().trim();

		if(!StringUtil.matchRegex(strPhoneNumber, InformationCodeUtil.regExpPhotoNumber)){
			et_phoneNumber.setError("请输入正确的手机号码");
			et_phoneNumber.requestFocus();
			return ;
		}
		
		SoapObject requestSoapObject = new SoapObject(ConnectServiceUtil.NAMESPACE, methodName);
		requestSoapObject.addProperty("phoneNum", strPhoneNumber);
		ConnectCustomServiceAsyncTask connectCustomServiceAsyncTask = new ConnectCustomServiceAsyncTask
		(mContext, this, requestSoapObject , methodName);
		connectCustomServiceAsyncTask.execute();
		return;
	}

	@Override
	public void connectServiceSuccessful(String returnString,
			String methodName, Integer state, boolean whetherRefresh) {
//		Object  obj = (Object ) returnSoapObject.getProperty(methodName+"Result");
		JSONResultMsgModel mJSONBackResultModel = null;
		Gson gson = new Gson();
		try {
			mJSONBackResultModel = gson.fromJson(returnString, JSONResultMsgModel.class);
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		}
//		 GetSMSCodeResponse{GetSMSCodeResult={"DjLsh":-1,"Msg":"1820","Sign":1}; }
//	     GetSMSCodeResponse{GetSMSCodeResult={"DjLsh":-1,"Msg":"验证码超出同模板同号码天发送上限","Sign":0}; }
		if(mJSONBackResultModel != null && mJSONBackResultModel.getSign() == 1){
			strCheckPhoneNumber = strPhoneNumber;
			strCheckSecurityCode = mJSONBackResultModel.getMsg();
			ToastUtil.show(mContext,"获取验证码成功,请输入获取到的验证码");
			timeRemain = 60;
			btn_getSecurityCode.setEnabled(false);
			handler.sendEmptyMessage(MESSAGE_01);			
			return;
		}
		
		if(mJSONBackResultModel != null && mJSONBackResultModel.getSign() == 0){
			ToastUtil.show(mContext, mJSONBackResultModel.getMsg());
		}
		strCheckPhoneNumber = null;
		strCheckSecurityCode = null;
	}

	@Override
	public void connectServiceFailed(String returnStrError, String methodName, Integer state,
			boolean whetherRefresh) {
		ToastUtil.show(mContext, "网络异常,获取验证码失败");
	}

	@Override
	public void connectServiceCancelled(String returnString,
			String methodName, Integer state, boolean whetherRefresh) {
	}
	
	
	@Override
	protected void onDestroy() {
		handler.removeCallbacksAndMessages(null);
		super.onDestroy();
	}

}
