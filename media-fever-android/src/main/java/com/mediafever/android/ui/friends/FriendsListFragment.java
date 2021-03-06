package com.mediafever.android.ui.friends;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.jdroid.android.AndroidUseCaseListener;
import com.jdroid.android.activity.ActivityIf;
import com.jdroid.android.activity.BaseActivity.UseCaseTrigger;
import com.jdroid.android.fragment.AbstractListFragment;
import com.jdroid.android.gcm.GcmMessage;
import com.jdroid.android.gcm.GcmMessageBroadcastReceiver;
import com.jdroid.android.utils.ToastUtils;
import com.mediafever.R;
import com.mediafever.android.gcm.MediaFeverGcmMessage;
import com.mediafever.android.ui.UserAdapter;
import com.mediafever.domain.UserImpl;
import com.mediafever.usecase.friends.FriendsUseCase;
import com.mediafever.usecase.friends.RemoveFriendUseCase;

/**
 * 
 * @author Maxi Rosson
 */
public class FriendsListFragment extends AbstractListFragment<UserImpl> {
	
	private RemoveFriendUseCase removeFriendUseCase;
	private FriendsUseCase friendsUseCase;
	private AndroidUseCaseListener removeFriendUseCaseListener;
	
	private BroadcastReceiver refreshBroadcastReceiver;
	
	/**
	 * @see com.jdroid.android.fragment.AbstractFragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		friendsUseCase = getInstance(FriendsUseCase.class);
		friendsUseCase.setUserId(getUser().getId());
		
		removeFriendUseCase = getInstance(RemoveFriendUseCase.class);
		removeFriendUseCaseListener = new AndroidUseCaseListener() {
			
			@Override
			public void onFinishUseCase() {
				executeOnUIThread(new Runnable() {
					
					@Override
					public void run() {
						ToastUtils.showInfoToast(R.string.friendRemoved);
						executeUseCase(friendsUseCase);
						dismissLoading();
					}
				});
			}
			
			@Override
			protected ActivityIf getActivityIf() {
				return (ActivityIf)getActivity();
			}
		};
		removeFriendUseCase.setUserId(getUser().getId());
	}
	
	/**
	 * @see android.app.ListFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup,
	 *      android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.list_fragment, container, false);
	}
	
	/**
	 * @see com.jdroid.android.fragment.AbstractListFragment#onViewCreated(android.view.View, android.os.Bundle)
	 */
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		registerForContextMenu(getListView());
	}
	
	/**
	 * @see com.jdroid.android.fragment.AbstractFragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		onResumeUseCase(friendsUseCase, this, UseCaseTrigger.ONCE);
		onResumeUseCase(removeFriendUseCase, removeFriendUseCaseListener);
		
		refreshBroadcastReceiver = new GcmMessageBroadcastReceiver() {
			
			@Override
			protected void onGcmMessage(GcmMessage gcmMessage, Intent intent) {
				executeUseCase(friendsUseCase);
			}
		};
		
		GcmMessageBroadcastReceiver.startListeningGcmBroadcasts(refreshBroadcastReceiver,
			MediaFeverGcmMessage.FRIEND_REQUEST_ACCEPTED, MediaFeverGcmMessage.FRIEND_REMOVED);
	}
	
	/**
	 * @see com.jdroid.android.fragment.AbstractFragment#onPause()
	 */
	@Override
	public void onPause() {
		super.onPause();
		onPauseUseCase(friendsUseCase, this);
		onPauseUseCase(removeFriendUseCase, removeFriendUseCaseListener);
		
		GcmMessageBroadcastReceiver.stopListeningGcmBroadcasts(refreshBroadcastReceiver);
	}
	
	/**
	 * @see android.support.v4.app.Fragment#onCreateContextMenu(android.view.ContextMenu, android.view.View,
	 *      android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.friends_menu, menu);
	}
	
	/**
	 * @see android.support.v4.app.Fragment#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		UserImpl friend = getMenuItem(item);
		switch (item.getItemId()) {
			case R.id.removeItem:
				removeFriendUseCase.setFriendId(friend.getId());
				executeUseCase(removeFriendUseCase);
				return true;
			default:
				return false;
		}
	}
	
	/**
	 * @see com.jdroid.android.fragment.AbstractListFragment#getNoResultsText()
	 */
	@Override
	protected int getNoResultsText() {
		return R.string.noResultsFriends;
	}
	
	/**
	 * @see com.jdroid.android.fragment.AbstractFragment#onFinishUseCase()
	 */
	@Override
	public void onFinishUseCase() {
		executeOnUIThread(new Runnable() {
			
			@Override
			public void run() {
				setListAdapter(new UserAdapter(getActivity(), friendsUseCase.getFriends()));
				dismissLoading();
			}
		});
	}
}
