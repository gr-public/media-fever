package com.mediafever.android;

import org.slf4j.Logger;
import android.app.Activity;
import android.support.v4.app.Fragment;
import com.google.android.gcm.GCMRegistrar;
import com.google.inject.AbstractModule;
import com.jdroid.android.AbstractApplication;
import com.jdroid.android.ActivityLauncher;
import com.jdroid.android.activity.BaseActivity;
import com.jdroid.android.context.DefaultApplicationContext;
import com.jdroid.android.context.SecurityContext;
import com.jdroid.android.exception.ExceptionHandler;
import com.jdroid.android.fragment.BaseFragment;
import com.jdroid.android.utils.NotificationUtils;
import com.jdroid.java.utils.LoggerUtils;
import com.mediafever.R;
import com.mediafever.android.exception.AndroidExceptionHandler;
import com.mediafever.android.service.DisableDeviceService;
import com.mediafever.android.service.EnableDeviceService;
import com.mediafever.android.ui.home.HomeActivity;
import com.mediafever.android.ui.login.LoginActivity;
import com.mediafever.context.ApplicationContext;
import com.mediafever.repository.FriendRequestsRepository;
import com.mediafever.repository.FriendsRepository;
import com.mediafever.repository.MediaSessionsRepository;

/**
 * 
 * @author Maxi Rosson
 */
public class AndroidApplication extends AbstractApplication {
	
	private final static Logger LOGGER = LoggerUtils.getLogger(AndroidApplication.class);
	
	public static AndroidApplication get() {
		return (AndroidApplication)AbstractApplication.INSTANCE;
	}
	
	/**
	 * @see com.jdroid.android.AbstractApplication#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		
		if (SecurityContext.get().isAuthenticated()) {
			if (GCMRegistrar.isRegistered(this)) {
				LOGGER.debug("GCM already registered on the device");
				if (!GCMRegistrar.isRegisteredOnServer(this)) {
					EnableDeviceService.runIntentInService(this);
				}
			} else {
				GCMRegistrar.register(this, getAndroidApplicationContext().getGoogleProjectId());
			}
		}
	}
	
	/**
	 * @see com.jdroid.android.AbstractApplication#createApplicationContext()
	 */
	@Override
	protected DefaultApplicationContext createApplicationContext() {
		return ApplicationContext.get();
	}
	
	/**
	 * @see com.jdroid.android.AbstractApplication#getExceptionHandlerClass()
	 */
	@Override
	public Class<? extends ExceptionHandler> getExceptionHandlerClass() {
		return AndroidExceptionHandler.class;
	}
	
	/**
	 * @see com.jdroid.android.AbstractApplication#createAndroidModule()
	 */
	@Override
	protected AbstractModule createAndroidModule() {
		return new AndroidModule();
	}
	
	public void logout() {
		
		DisableDeviceService.setUserToken(SecurityContext.get().getUser().getUserToken());
		GCMRegistrar.unregister(this);
		
		SecurityContext.get().detachUser();
		NotificationUtils.cancelAllNotifications();
		
		FriendsRepository friendsRepository = getInstance(FriendsRepository.class);
		friendsRepository.removeAll();
		friendsRepository.resetLastUpdateTimestamp();
		
		FriendRequestsRepository friendRequestsRepository = getInstance(FriendRequestsRepository.class);
		friendRequestsRepository.removeAll();
		friendRequestsRepository.resetLastUpdateTimestamp();
		
		MediaSessionsRepository mediaSessionsRepository = getInstance(MediaSessionsRepository.class);
		mediaSessionsRepository.removeAll();
		mediaSessionsRepository.resetLastUpdateTimestamp();
		
		ActivityLauncher.launchActivityClearTask(LoginActivity.class, true);
	}
	
	/**
	 * @see com.jdroid.android.AbstractApplication#getHomeActivityClass()
	 */
	@Override
	public Class<? extends Activity> getHomeActivityClass() {
		return HomeActivity.class;
	}
	
	/**
	 * @see com.jdroid.android.AbstractApplication#createBaseActivity(android.app.Activity)
	 */
	@Override
	public BaseActivity createBaseActivity(Activity activity) {
		return new AndroidBaseActivity(activity);
	}
	
	/**
	 * @see com.jdroid.android.AbstractApplication#createBaseFragment(android.support.v4.app.Fragment)
	 */
	@Override
	public BaseFragment createBaseFragment(Fragment fragment) {
		return new AndroidBaseFragment(fragment);
	}
	
	/**
	 * @see com.jdroid.android.AbstractApplication#isGcmEnabled()
	 */
	@Override
	public Boolean isGcmEnabled() {
		return true;
	}
	
	/**
	 * @see com.jdroid.android.AbstractApplication#getAppName()
	 */
	@Override
	public String getAppName() {
		return getString(getAndroidApplicationContext().isFreeApp() ? R.string.appNameFree : R.string.appName);
	}
}
