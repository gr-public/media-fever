package com.mediafever.core.domain.session;

import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import org.apache.commons.collections.CollectionUtils;
import com.jdroid.java.collections.Lists;
import com.jdroid.java.utils.DateUtils;
import com.jdroid.javaweb.domain.Entity;
import com.mediafever.api.exception.ServerErrorCode;
import com.mediafever.core.domain.User;
import com.mediafever.core.domain.watchable.Watchable;
import com.mediafever.core.domain.watchable.WatchableType;

/**
 * 
 * @author Maxi Rosson
 */
@javax.persistence.Entity
public class MediaSession extends Entity {
	
	private Date date;
	
	private Date time;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "mediaSessionId")
	private List<MediaSessionUser> users;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "mediaSessionId")
	private List<MediaSelection> selections;
	
	@ElementCollection
	@JoinTable(name = "MediaSession_WatchableType", joinColumns = @JoinColumn(name = "mediaSessionId"))
	@Column(name = "watchableType")
	@Enumerated(value = EnumType.STRING)
	private List<WatchableType> watchableTypes;
	
	/**
	 * Default constructor.
	 */
	@SuppressWarnings("unused")
	private MediaSession() {
		// Do nothing, is required by hibernate
	}
	
	public MediaSession(List<WatchableType> watchableTypes, Date date, Date time, List<MediaSessionUser> users) {
		if (CollectionUtils.isEmpty(watchableTypes)) {
			this.watchableTypes = Lists.newArrayList(WatchableType.MOVIE, WatchableType.SERIES);
		} else {
			this.watchableTypes = watchableTypes;
		}
		this.date = date;
		this.time = time;
		this.users = users;
		selections = Lists.newArrayList();
		if (isExpired()) {
			throw ServerErrorCode.MEDIA_SESSION_CREATION_EXPIRED.newBusinessException();
		}
	}
	
	public void modify(List<WatchableType> watchableTypes, Date date, Date time, List<MediaSessionUser> newUsers) {
		if (CollectionUtils.isEmpty(watchableTypes)) {
			watchableTypes = Lists.newArrayList(WatchableType.MOVIE, WatchableType.SERIES);
		}
		
		for (MediaSelection mediaSelection : selections) {
			// If a another user added a media session for removed watchable type, we add it to avoid data inconsistency
			if (!watchableTypes.contains(mediaSelection.getWatchable().getType())) {
				watchableTypes.add(mediaSelection.getWatchable().getType());
			}
		}
		this.watchableTypes.clear();
		this.watchableTypes.addAll(watchableTypes);
		this.date = date;
		this.time = time;
		users.addAll(newUsers);
		if (isExpired()) {
			throw ServerErrorCode.MEDIA_SESSION_EDITION_EXPIRED.newBusinessException();
		}
	}
	
	public void leave(User user) {
		MediaSessionUser mediaSessionUser = null;
		for (MediaSessionUser each : users) {
			if (each.getUser().equals(user)) {
				mediaSessionUser = each;
				break;
			}
		}
		
		if (mediaSessionUser != null) {
			users.remove(mediaSessionUser);
			List<MediaSelection> mediaSelectionsToRemove = Lists.newArrayList();
			for (MediaSelection each : selections) {
				if (each.getOwner().equals(user)) {
					mediaSelectionsToRemove.add(each);
				} else {
					each.leave(user);
				}
			}
			selections.removeAll(mediaSelectionsToRemove);
		}
	}
	
	public void thumbsUp(MediaSelection mediaSelection, User user) {
		mediaSelection.thumbsUp(user);
	}
	
	public void thumbsDown(MediaSelection mediaSelection, User user) {
		mediaSelection.thumbsDown(user);
	}
	
	public MediaSelection addSelection(User user, Watchable watchable) {
		for (MediaSelection each : selections) {
			if (each.getWatchable().equals(watchable)) {
				throw ServerErrorCode.MEDIA_SELECTION_DUPLICATED.newBusinessException();
			}
		}
		
		if (!watchableTypes.contains(watchable.getType())) {
			throw ServerErrorCode.MEDIA_SELECTION_INVALID_WATCAHBLE_TYPE.newBusinessException();
		}
		MediaSelection mediaSelection = new MediaSelection(user, watchable);
		selections.add(mediaSelection);
		return mediaSelection;
	}
	
	public void removeSelection(MediaSelection mediaSelection) {
		selections.remove(mediaSelection);
	}
	
	public Date getDate() {
		return date;
	}
	
	public Date getTime() {
		return time;
	}
	
	public Boolean isExpired() {
		if (date != null) {
			Date fullDate = DateUtils.getDate(date, time);
			if (DateUtils.now().after(fullDate)) {
				return true;
			}
		}
		return false;
	}
	
	public void rejectUser(MediaSessionUser mediaSessionUser) {
		users.remove(mediaSessionUser);
	}
	
	public List<MediaSessionUser> getUsers() {
		return users;
	}
	
	public List<MediaSelection> getSelections() {
		return selections;
	}
	
	public List<WatchableType> getWatchableTypes() {
		return watchableTypes;
	}
	
}
