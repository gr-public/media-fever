package com.mediafever.android.ui.friends;

import android.app.Activity;
import android.support.v4.app.Fragment;
import com.jdroid.android.tabs.TabAction;
import com.mediafever.R;

/**
 * 
 * @author Maxi Rosson
 */
public enum FriendsContextualItem implements TabAction {
	
	MY_FRIENDS(R.string.friends, R.drawable.friends_contextual_selector, FriendsListActivity.class) {
		
		@Override
		public Fragment createFragment(Object args) {
			return new FriendsGridFragment();
		}
	},
	REQUESTS(R.string.requests, R.drawable.friends_request_contextual_selector, FriendsRequestsActivity.class) {
		
		@Override
		public Fragment createFragment(Object args) {
			return new FriendsRequestsFragment();
		}
	},
	FACEBOOK(R.string.facebook, R.drawable.facebook_contextual_selector, FacebookFriendsActivity.class) {
		
		@Override
		public Fragment createFragment(Object args) {
			return new FacebookFriendsGridFragment();
		}
	},
	SEARCH_USERS(R.string.searchUsers, R.drawable.search_users_contextual_selector, SearchUsersActivity.class) {
		
		@Override
		public Fragment createFragment(Object args) {
			return new SearchUsersFragment();
		}
	};
	
	private Integer resourceId;
	private Integer iconId;
	private Class<? extends Activity> activityClass;
	
	private FriendsContextualItem(Integer resourceId, Integer iconId, Class<? extends Activity> activityClass) {
		this.resourceId = resourceId;
		this.iconId = iconId;
		this.activityClass = activityClass;
	}
	
	/**
	 * @see com.jdroid.android.tabs.TabAction#getNameResource()
	 */
	@Override
	public int getNameResource() {
		return resourceId;
	}
	
	/**
	 * @see com.jdroid.android.tabs.TabAction#getIconResource()
	 */
	@Override
	public int getIconResource() {
		return iconId;
	}
	
	/**
	 * @see com.jdroid.android.tabs.TabAction#getActivityClass()
	 */
	@Override
	public Class<? extends Activity> getActivityClass() {
		return activityClass;
	}
	
	/**
	 * @see com.jdroid.android.tabs.TabAction#getName()
	 */
	@Override
	public String getName() {
		return name();
	}
}
