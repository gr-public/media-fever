package com.mediafever.repository;

import java.util.Collections;
import java.util.List;
import com.google.inject.Inject;
import com.jdroid.android.context.SecurityContext;
import com.jdroid.android.repository.SynchronizedInMemoryRepository;
import com.jdroid.java.collections.Lists;
import com.jdroid.java.repository.ObjectNotFoundException;
import com.mediafever.domain.session.MediaSession;
import com.mediafever.service.APIService;

/**
 * 
 * @author Maxi Rosson
 */
public class InMemoryMediaSessionsRepository extends SynchronizedInMemoryRepository<MediaSession> implements
		MediaSessionsRepository {
	
	private APIService apiService;
	
	@Inject
	public InMemoryMediaSessionsRepository(APIService apiService) {
		this.apiService = apiService;
	}
	
	/**
	 * @see com.jdroid.java.repository.InMemoryRepository#get(java.lang.Long)
	 */
	@Override
	public MediaSession get(Long id) throws ObjectNotFoundException {
		MediaSession mediaSession = null;
		if (isOutdated()) {
			mediaSession = apiService.getMediaSession(id);
			update(mediaSession);
		} else {
			mediaSession = super.get(id);
		}
		return mediaSession;
	}
	
	/**
	 * @see com.jdroid.java.repository.InMemoryRepository#getAll()
	 */
	@Override
	public List<MediaSession> getAll() {
		List<MediaSession> mediaSessions = null;
		if (isOutdated()) {
			mediaSessions = apiService.getMediaSessions(SecurityContext.get().getUser().getId());
			replaceAll(mediaSessions);
			refreshUpdateTimestamp();
		} else {
			mediaSessions = super.getAll();
		}
		return mediaSessions;
	}
	
	/**
	 * @see com.mediafever.repository.MediaSessionsRepository#getPendingMediaSessions()
	 */
	@Override
	public List<MediaSession> getPendingMediaSessions() {
		List<MediaSession> pendingMediaSessions = Lists.newArrayList();
		for (MediaSession mediaSession : getAll()) {
			if (mediaSession.isPending()) {
				pendingMediaSessions.add(mediaSession);
			}
		}
		Collections.sort(pendingMediaSessions);
		return pendingMediaSessions;
	}
	
	/**
	 * @see com.mediafever.repository.MediaSessionsRepository#getActiveMediaSessions()
	 */
	@Override
	public List<MediaSession> getActiveMediaSessions() {
		List<MediaSession> activeMediaSessions = Lists.newArrayList();
		for (MediaSession mediaSession : getAll()) {
			if (mediaSession.isActive()) {
				activeMediaSessions.add(mediaSession);
			}
		}
		Collections.sort(activeMediaSessions);
		return activeMediaSessions;
	}
	
	/**
	 * @see com.mediafever.repository.MediaSessionsRepository#getExpiredMediaSessions()
	 */
	@Override
	public List<MediaSession> getExpiredMediaSessions() {
		List<MediaSession> expiredMediaSessions = Lists.newArrayList();
		for (MediaSession mediaSession : getAll()) {
			if (mediaSession.isExpired()) {
				expiredMediaSessions.add(mediaSession);
			}
		}
		Collections.sort(expiredMediaSessions);
		return expiredMediaSessions;
	}
}
