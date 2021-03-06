package com.mediafever.android.ui.settings;

import java.util.List;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.jdroid.android.contextual.ContextualListFragment;
import com.jdroid.android.images.CustomImageView;
import com.jdroid.android.tabs.TabAction;
import com.mediafever.R;

/**
 * 
 * @author Maxi Rosson
 */
public class SettingsLargeContextualListFragment extends ContextualListFragment {
	
	private final static int MAX_AVATAR_HEIGHT = 240;
	private final static int MAX_AVATAR_WIDTH = 240;
	
	public SettingsLargeContextualListFragment() {
		super();
	}
	
	public SettingsLargeContextualListFragment(List<? extends TabAction> actions, TabAction defaultContextualItem) {
		super(actions, defaultContextualItem);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.settings_contextual_list_fragment, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		CustomImageView avatar = findView(R.id.photo);
		avatar.setImageContent(getUser().getImage(), R.drawable.profile_default, MAX_AVATAR_WIDTH, MAX_AVATAR_HEIGHT);
	}
}
