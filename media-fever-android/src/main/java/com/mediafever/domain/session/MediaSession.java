package com.mediafever.domain.session;

import java.util.Date;
import java.util.List;
import com.jdroid.android.domain.Entity;
import com.mediafever.android.AndroidErrorCode;
import com.mediafever.domain.watchable.WatchableType;

/**
 * 
 * @author Maxi Rosson
 */
public class MediaSession extends Entity {
	
	private Date date;
	private Date time;
	private List<MediaSessionUser> users;
	private List<WatchableType> watchableTypes;
	private Boolean accepted;
	
	public MediaSession(Long id, Date date, List<MediaSessionUser> users, List<WatchableType> watchableTypes,
			Boolean accepted) {
		super(id);
		this.date = date;
		this.users = users;
		this.watchableTypes = watchableTypes;
		this.accepted = accepted;
	}
	
	public MediaSession(Date date, Date time, List<WatchableType> watchableTypes, List<MediaSessionUser> users) {
		if (watchableTypes.isEmpty()) {
			throw AndroidErrorCode.REQUIRED_SESSION_TYPE.newBusinessException();
		}
		this.date = date;
		this.time = time;
		this.users = users;
		this.watchableTypes = watchableTypes;
	}
	
	public Date getDate() {
		return date;
	}
	
	public Date getTime() {
		return time;
	}
	
	public List<MediaSessionUser> getUsers() {
		return users;
	}
	
	public List<WatchableType> getWatchableTypes() {
		return watchableTypes;
	}
	
	public Boolean isAccepted() {
		return (accepted != null) && accepted;
	}
	
}
