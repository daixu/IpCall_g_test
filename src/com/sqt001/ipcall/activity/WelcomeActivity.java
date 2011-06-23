package com.sqt001.ipcall.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;

import com.sqt001.ipcall.R;
import com.sqt001.ipcall.application.AppPreference;
import com.sqt001.ipcall.application.BuildConfig;
import com.sqt001.ipcall.contact.Account;
import com.sqt001.ipcall.login.SendSms;
import com.sqt001.ipcall.provider.GetSmsTask;
import com.sqt001.ipcall.provider.QueryAccountTask;
import com.sqt001.ipcall.provider.QueryAccountTask.QueryAccountTaskListener;
import com.sqt001.ipcall.provider.RegisterTask;
import com.sqt001.ipcall.util.LogUtil;

/**
 * 欢迎界面
 */
public class WelcomeActivity extends Activity {
  private SendSms sendSms = new SendSms(this);

  private final static int WELCOME_VIEW = 0;
  private final static int LOGON_RUN = 1;
  public final static int EXIT = 2;

  private Handler mHandler = new Handler() {

    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
      case WELCOME_VIEW:
        break;

      case LOGON_RUN:
        checkAccount();
        break;
      }
    }
  };

  Timer mTimer = null;

  public void SendMessage(final int what, int duration) {
    if (mTimer == null) {
      mTimer = new Timer();
    }

    TimerTask task = new TimerTask() {
      @Override
      public void run() {
        Message message = new Message();
        message.what = what;
        mHandler.sendMessage(message);
      }
    };
    mTimer.schedule(task, duration);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.welcome_activity);
    SendMessage(LOGON_RUN, 2000);
  }

  private void checkAccount() {
    boolean isAccount = isAccountExist();
    LogUtil.w("isAccount: " + isAccount);
    if (isAccount) {
      startupJump(DialtactsActivity.class, true);
    } else {
      boolean x = Account.isActive();
      if (x) {
        // exist imsi
        getBalance();
      } else {
        // no imsi.
        startupJump(DialtactsActivity.class, true);
        // Account.activeAccountForResult(this, 1);
      }
    }
  }

  private boolean isAccountExist() {
    boolean isExists = false;
    boolean isRegistered = isAccountRegistered();
    if (isRegistered) {
      isExists = true;
    }
    return isExists;
  }

  /**
   * @return true if is registered, false else.
   */
  private boolean isAccountRegistered() {
    if (BuildConfig.isDebug()) {
      // return false;
      return AppPreference.getAccount().length() > 0 || AppPreference.getUserId().length() > 0;
    } else {
      return AppPreference.getAccount().length() > 0 || AppPreference.getUserId().length() > 0;
    }
  }

  private void startupJump(final Class<?> clazz, final boolean isLogined) {
    Intent intent = new Intent(WelcomeActivity.this, clazz);
    intent.putExtra("isLogined", isLogined);
    WelcomeActivity.this.startActivity(intent);
    WelcomeActivity.this.finish();
  }

  private void getBalance() {
    new QueryAccountTask(this).execute(new QueryAccountTaskListener() {
      @Override
      public void onQueryAccountFinish(String isnewuser, String reason) {
        if (isnewuser.equals("0")) {
          if (AppPreference.getControl() == 0) {
            startGetSmsTask();
            register();
          } else if (AppPreference.getControl() == 1) {
            RegisterAndManualBind();
          } else if (AppPreference.getControl() == 2) {
            register();
          }
        } else if (isnewuser.equals("1")) {
          if (AppPreference.getControl() == 0) {
            startGetSmsTask();
            login();
          } else if (AppPreference.getControl() == 1) {
            LoginAndManualBind();
          } else if (AppPreference.getControl() == 2) {
            login();
          }
        } else {
          if (!reason.equals(""))
            handleFailResult(reason);
        }
      }

      private void RegisterAndManualBind() {
        startRegisterAndManualBindTask();
      }

      private void startRegisterAndManualBindTask() {
        new RegisterTask(WelcomeActivity.this).execute(new RegisterTask.RegisterTaskListener() {

          @Override
          public void onRegisterFinish(boolean success, String reason) {
            if (success) {
              handleSuccessResult();
            } else {
              handleFailResult(reason);
            }
          }

          private void handleSuccessResult() {
            toResultActivity(true);
          }

          private void toResultActivity(boolean success) {
            Intent intent = new Intent(WelcomeActivity.this, ManualBindMainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putBoolean("showPwd", true);
            bundle.putBoolean("success", success);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
          }
        });
      }

      private void register() {
        startRegisterTask();
      }

      private void startRegisterTask() {
        new RegisterTask(WelcomeActivity.this).execute(new RegisterTask.RegisterTaskListener() {

          @Override
          public void onRegisterFinish(boolean success, String reason) {
            if (success) {
              handleSuccessResult();
            } else {
              handleFailResult(reason);
            }
          }

          private void handleSuccessResult() {
            toResultActivity(true);
          }

          private void toResultActivity(boolean success) {
            Intent intent = new Intent(WelcomeActivity.this, FinalPromptActivity.class);
            Bundle bundle = new Bundle();
            bundle.putBoolean("showPwd", true);
            bundle.putBoolean("success", success);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
          }
        });
      }

      private void LoginAndManualBind() {
        Intent intent = new Intent(WelcomeActivity.this, ManualBindMainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("showPwd", false);
        bundle.putBoolean("success", true);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
      }

      private void login() {
        Intent intent = new Intent(WelcomeActivity.this, FinalPromptActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("showPwd", false);
        bundle.putBoolean("success", true);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
      }
    });
  }

  private void startGetSmsTask() {
    new GetSmsTask(WelcomeActivity.this).execute(new GetSmsTask.GetSmsTaskListener() {
      @Override
      public void onGetSmsTaskFinish(boolean success, String reason) {
        if (success) {
          String address = AppPreference.getGatewayNumber();
          sendSms.sendSms(address);
        } else {
          if (!reason.equals(""))
            handleFailResult(reason);
        }
      }
    });
  }

  private void handleFailResult(final String reason) {
    Intent intent = new Intent(WelcomeActivity.this, AccountActiveActivity.class);
    startActivity(intent);
    finish();
  }
}
