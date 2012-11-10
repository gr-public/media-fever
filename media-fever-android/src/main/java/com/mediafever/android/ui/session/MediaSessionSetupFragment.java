package com.mediafever.android.ui.session;

import java.util.Date;
import roboguice.inject.InjectView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import com.jdroid.android.ActivityLauncher;
import com.jdroid.android.fragment.AbstractFragment;
import com.jdroid.android.fragment.DatePickerDialogFragment.OnDateSetListener;
import com.jdroid.android.fragment.TimePickerDialogFragment.OnTimeSetListener;
import com.jdroid.android.view.DateButton;
import com.jdroid.android.view.TimeButton;
import com.jdroid.java.utils.DateUtils;
import com.mediafever.R;
import com.mediafever.domain.watchable.WatchableType;
import com.mediafever.usecase.MediaSessionSetupUseCase;

/**
 * 
 * @author Maxi Rosson
 */
public class MediaSessionSetupFragment extends AbstractFragment implements OnDateSetListener, OnTimeSetListener {
	
	private MediaSessionSetupUseCase mediaSessionSetupUseCase;
	
	@InjectView(R.id.movies)
	private CheckBox movies;
	
	@InjectView(R.id.series)
	private CheckBox series;
	
	@InjectView(R.id.date)
	private DateButton dateEditText;
	
	@InjectView(R.id.time)
	private TimeButton timeEditText;
	
	@InjectView(R.id.onDate)
	private RadioButton onDate;
	
	@InjectView(R.id.atTime)
	private RadioButton atTime;
	
	/**
	 * @see com.jdroid.android.fragment.AbstractFragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}
	
	/**
	 * @see com.jdroid.android.fragment.AbstractFragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		if (mediaSessionSetupUseCase == null) {
			mediaSessionSetupUseCase = getInstance(MediaSessionSetupUseCase.class);
		}
	}
	
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
		
		movies.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					mediaSessionSetupUseCase.addWatchableType(WatchableType.MOVIE);
				} else {
					mediaSessionSetupUseCase.removeWatchableType(WatchableType.MOVIE);
				}
			}
		});
		
		series.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					mediaSessionSetupUseCase.addWatchableType(WatchableType.SERIES);
				} else {
					mediaSessionSetupUseCase.removeWatchableType(WatchableType.SERIES);
				}
			}
		});
		
		Date now = DateUtils.now();
		dateEditText.init(this, now);
		dateEditText.setDate(now);
		
		timeEditText.init(this, now);
		
		onDate.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					dateEditText.setVisibility(View.VISIBLE);
					mediaSessionSetupUseCase.setDate(dateEditText.getDate());
				} else {
					dateEditText.setVisibility(View.GONE);
					mediaSessionSetupUseCase.setDate(null);
				}
			}
		});
		
		atTime.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					timeEditText.setVisibility(View.VISIBLE);
					mediaSessionSetupUseCase.setTime(timeEditText.getTime());
				} else {
					timeEditText.setVisibility(View.GONE);
					mediaSessionSetupUseCase.setTime(null);
				}
			}
		});
	}
	
	/**
	 * @see com.jdroid.android.fragment.DatePickerDialogFragment.OnDateSetListener#onDateSet(java.util.Date, int)
	 */
	@Override
	public void onDateSet(Date date, int requestCode) {
		dateEditText.setDate(date);
		mediaSessionSetupUseCase.setDate(date);
	}
	
	/**
	 * @see com.jdroid.android.fragment.TimePickerDialogFragment.OnTimeSetListener#onTimeSet(java.util.Date)
	 */
	@Override
	public void onTimeSet(Date time) {
		timeEditText.setTime(time);
		mediaSessionSetupUseCase.setTime(time);
	}
	
	/**
	 * @see com.jdroid.android.fragment.AbstractFragment#onFinishUseCase()
	 */
	@Override
	public void onFinishUseCase() {
		// TODO Change this target activity
		ActivityLauncher.launchHomeActivity();
	}
}
