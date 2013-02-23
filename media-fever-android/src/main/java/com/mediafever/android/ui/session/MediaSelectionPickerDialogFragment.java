package com.mediafever.android.ui.session;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import com.jdroid.android.dialog.AbstractDialogFragment;
import com.mediafever.R;
import com.mediafever.domain.session.MediaSession;
import com.mediafever.usecase.mediasession.SmartSelectionUseCase;

/**
 * 
 * @author Maxi Rosson
 */
public class MediaSelectionPickerDialogFragment extends AbstractDialogFragment {
	
	private static final String MEDIA_SESSION_EXTRA = "mediaSession";
	
	private SmartSelectionUseCase smartSelectionUseCase;
	private MediaSession mediaSession;
	
	public static void show(Fragment targetFragment, MediaSession mediaSession) {
		FragmentManager fm = targetFragment.getActivity().getSupportFragmentManager();
		MediaSelectionPickerDialogFragment dialogFragment = new MediaSelectionPickerDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(MEDIA_SESSION_EXTRA, mediaSession);
		dialogFragment.setArguments(bundle);
		dialogFragment.setTargetFragment(targetFragment, 1);
		dialogFragment.show(fm, MediaSelectionPickerDialogFragment.class.getSimpleName());
	}
	
	/**
	 * @see com.jdroid.android.dialog.AbstractDialogFragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mediaSession = getArgument(MEDIA_SESSION_EXTRA);
		smartSelectionUseCase = getInstance(SmartSelectionUseCase.class);
		smartSelectionUseCase.setMediaSession(mediaSession);
	}
	
	/**
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup,
	 *      android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.media_selection_picker_dialog_fragment, container, false);
	}
	
	/**
	 * @see com.jdroid.android.dialog.AbstractDialogFragment#onViewCreated(android.view.View, android.os.Bundle)
	 */
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		View manualSelection = findView(R.id.manualSelection);
		manualSelection.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Implement manual selection
			}
		});
		
		View smartSelection = findView(R.id.smartSelection);
		smartSelection.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				executeUseCase(smartSelectionUseCase);
			}
		});
		
		getDialog().setTitle(R.string.selection);
	}
	
	/**
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		onResumeUseCase(smartSelectionUseCase, this);
	}
	
	/**
	 * @see android.support.v4.app.Fragment#onPause()
	 */
	@Override
	public void onPause() {
		super.onPause();
		onPauseUseCase(smartSelectionUseCase, this);
	}
	
	/**
	 * @see com.jdroid.android.dialog.AbstractDialogFragment#onFinishUseCase()
	 */
	@Override
	public void onFinishUseCase() {
		executeOnUIThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Implement media selection add use case
				// getMediaSessionSetupUseCase().addSelection(smartSelectionUseCase.getWatchable());
				((MediaSelectionsFragment)getTargetFragment()).refresh();
				dismissLoading();
				dismiss();
			}
		});
	}
}
