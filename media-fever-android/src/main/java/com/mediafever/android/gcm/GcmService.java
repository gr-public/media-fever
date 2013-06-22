package com.mediafever.android.gcm;

import android.content.Context;
import android.content.Intent;
import com.jdroid.android.context.SecurityContext;
import com.jdroid.android.gcm.AbstractGcmService;
import com.jdroid.android.utils.ToastUtils;
import com.mediafever.R;

/**
 * 
 * @author Maxi Rosson
 */
public class GcmService extends AbstractGcmService {
	
	/**
	 * @see com.jdroid.android.gcm.AbstractGcmService#onMissingGoogleAccountError(android.content.Context)
	 */
	@Override
	public void onMissingGoogleAccountError(Context context) {
		ToastUtils.showToastOnUIThread(R.string.onMissingGoogleAccountError);
	}
	
	/**
	 * @see com.google.android.gcm.GCMBaseIntentService#onMessage(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onMessage(Context context, Intent intent) {
		if (SecurityContext.get().isAuthenticated()) {
			GcmMessage gcmMessage = GcmMessage.find(intent);
			if (gcmMessage != null) {
				gcmMessage.handle(intent);
			}
		}
	}
}
