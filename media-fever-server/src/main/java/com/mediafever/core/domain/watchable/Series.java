package com.mediafever.core.domain.watchable;

import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mediafever.core.domain.watchable.visitor.WatchableVisitor;

/**
 * 
 * @author Maxi Rosson
 */
@javax.persistence.Entity
public class Series extends Watchable {
	
	@OneToMany(targetEntity = Season.class, fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
	@JoinTable(name = "Series_Season", joinColumns = @JoinColumn(name = "seriesId"), inverseJoinColumns = @JoinColumn(
			name = "seasonId"))
	private List<Season> seasons;
	
	/**
	 * Default constructor.
	 */
	@SuppressWarnings("unused")
	private Series() {
		// Do nothing, is required by hibernate
	}
	
	public Series(Long externalId, String name, String imageURL, String overview, List<Person> actors,
			List<Genre> genres, Float rating, Integer ratingCount, Date releaseDate, Long lastupdated) {
		super(externalId, name, imageURL, overview, actors, genres, rating, ratingCount, releaseDate, lastupdated);
		seasons = Lists.newArrayList();
	}
	
	public void addSeason(Season season) {
		seasons.add(season);
	}
	
	public List<Season> getSeasons() {
		return seasons;
	}
	
	public List<Season> getStartedSeasons() {
		return Lists.newArrayList(Iterables.filter(seasons, new Predicate<Season>() {
			
			@Override
			public boolean apply(Season season) {
				return !season.getReleasedEpisodes().isEmpty();
			}
		}));
		
	}
	
	/**
	 * Get all the {@link Episode}s of the {@link Series} that were released on the given date.
	 * 
	 * @param date The release {@link Date}.
	 * @return The list of episodes.
	 */
	public List<Episode> getReleasedEpisodes(Date date) {
		List<Episode> episodes = Lists.newArrayList();
		for (Season season : seasons) {
			List<Episode> seasonEpisodes = season.getReleasedEpisodes(date);
			if (!seasonEpisodes.isEmpty()) {
				episodes.addAll(seasonEpisodes);
			}
		}
		return episodes;
	}
	
	@Override
	public void updateFrom(Watchable series, WatchableVisitor watchableVisitor) {
		super.updateFrom(series, watchableVisitor);
		Series otherSeries = Series.class.cast(series);
		
		// Update the seasons
		if (otherSeries.seasons != null) {
			if (seasons == null) {
				seasons = Lists.newArrayList();
			}
			Map<Long, Season> seasonsMap = Maps.newHashMap();
			for (Season season : otherSeries.seasons) {
				seasonsMap.put(season.getExternalId(), season);
			}
			List<Season> seasonsToRemove = Lists.newArrayList();
			for (Season season : seasons) {
				Season otherSeason = seasonsMap.get(season.getExternalId());
				if (otherSeason != null) {
					season.updateFrom(otherSeason, watchableVisitor);
					seasonsMap.remove(otherSeason.getExternalId());
				} else {
					seasonsToRemove.add(season);
				}
			}
			seasons.removeAll(seasonsToRemove);
			seasons.addAll(seasonsMap.values());
		}
	}
	
	/**
	 * @see com.mediafever.core.domain.watchable.Watchable#getType()
	 */
	@Override
	public WatchableType getType() {
		return WatchableType.SERIES;
	}
}
