package com.mediafever.android.ui.session;

import java.util.Date;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.jdroid.android.date.DateButton;
import com.jdroid.android.date.DatePickerDialogFragment.OnDateSetListener;
import com.jdroid.android.date.TimeButton;
import com.jdroid.android.date.TimePickerDialogFragment.OnTimeSetListener;
import com.jdroid.android.fragment.AbstractFragment;
import com.jdroid.java.utils.DateUtils;
import com.mediafever.R;
import com.mediafever.domain.session.MediaSession;
import com.mediafever.domain.watchable.WatchableType;

/**
 * 
 * @author Maxi Rosson
 */
public class MediaSessionSetupFragment extends AbstractFragment implements OnDateSetListener, OnTimeSetListener {
	
	private MediaSession mediaSession;
	private DateButton dateEditText;
	private TimeButton timeEditText;
	
	/**
	 * @see android.app.ListFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup,
	 *      android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.media_session_setup_fragment, container, false);
	}
	
	/**
	 * @see com.jdroid.android.fragment.AbstractFragment#onViewCreated(android.view.View, android.os.Bundle)
	 */
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		mediaSession = ((MediaSessionActivity)getActivity()).getMediaSessionSetupUseCase().getMediaSession();
		
		initWatchableTypeCheckbox(R.id.movies, WatchableType.MOVIE);
		initWatchableTypeCheckbox(R.id.series, WatchableType.SERIES);
		
		Date now = DateUtils.now();
		
		// Date
		dateEditText = findView(R.id.date);
		CheckBox anyDate = findView(R.id.anyDate);
		anyDate.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					dateEditText.setVisibility(View.GONE);
					mediaSession.setDate(null);
				} else {
					dateEditText.setVisibility(View.VISIBLE);
					mediaSession.setDate(dateEditText.getDate());
				}
			}
		});
		if (mediaSession.getDate() != null) {
			dateEditText.init(this, mediaSession.getDate());
			dateEditText.setVisibility(View.VISIBLE);
			anyDate.setChecked(false);
		} else {
			dateEditText.init(this, now);
			dateEditText.setVisibility(View.GONE);
			anyDate.setChecked(true);
		}
		
		// Time
		timeEditText = findView(R.id.time);
		CheckBox anyTime = findView(R.id.anyTime);
		anyTime.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					timeEditText.setVisibility(View.GONE);
					mediaSession.setTime(null);
				} else {
					timeEditText.setVisibility(View.VISIBLE);
					mediaSession.setTime(timeEditText.getTime());
				}
			}
		});
		if (mediaSession.getTime() != null) {
			timeEditText.init(this, mediaSession.getTime());
			timeEditText.setVisibility(View.VISIBLE);
			anyTime.setChecked(false);
		} else {
			timeEditText.init(this, now);
			timeEditText.setVisibility(View.GONE);
			anyTime.setChecked(true);
		}
	}
	
	private void initWatchableTypeCheckbox(int checkBoxResId, final WatchableType watchableType) {
		CheckBox checkBox = findView(checkBoxResId);
		checkBox.setChecked(mediaSession.getWatchableTypes().contains(watchableType));
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					mediaSession.addWatchableType(watchableType);
				} else {
					mediaSession.removeWatchableType(watchableType);
				}
			}
		});
		checkBox.setEnabled(!mediaSession.isWatchableTypeRequired(watchableType));
	}
	
	/**
	 * @see com.jdroid.android.date.DatePickerDialogFragment.OnDateSetListener#onDateSet(java.util.Date, int)
	 */
	@Override
	public void onDateSet(Date date, int requestCode) {
		dateEditText.setDate(date);
		mediaSession.setDate(date);
	}
	
	/**
	 * @see com.jdroid.android.date.TimePickerDialogFragment.OnTimeSetListener#onTimeSet(java.util.Date, int)
	 */
	@Override
	public void onTimeSet(Date time, int requestCode) {
		timeEditText.setTime(time);
		mediaSession.setTime(time);
	}
}
