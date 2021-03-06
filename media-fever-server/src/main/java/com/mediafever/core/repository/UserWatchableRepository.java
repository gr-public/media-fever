package com.mediafever.core.repository;

import java.util.List;
import com.jdroid.java.repository.Repository;
import com.jdroid.javaweb.search.Filter;
import com.jdroid.javaweb.search.PagedResult;
import com.mediafever.core.domain.UserWatchable;
import com.mediafever.core.domain.watchable.Watchable;
import com.mediafever.core.domain.watchable.WatchableType;

/**
 * Repository that handles the persistence of {@link UserWatchable}s.
 * 
 * @author Maxi Rosson
 */
public interface UserWatchableRepository extends Repository<UserWatchable> {
	
	/**
	 * Searches for {@link UserWatchable}s that comply with the given {@link Filter}.
	 * 
	 * @param filter The {@link Filter}.
	 * @return A {@link PagedResult} of {@link UserWatchable}s
	 */
	public PagedResult<UserWatchable> search(Filter filter);
	
	public UserWatchable get(Long userId, Long watchableId);
	
	public List<UserWatchable> findAll(Long userId, List<Long> watchablesIds);
	
	public List<UserWatchable> getWatchedBy(Long watchableExternalId, WatchableType watchableType);
	
	public List<UserWatchable> getWatchedBy(List<Long> userIds, Long watchableId);
	
	public List<UserWatchable> getWatchedBy(Watchable watchable);
	
	public List<UserWatchable> getOnTheWishListOf(List<Long> userIds, Long watchableId);
	
}
