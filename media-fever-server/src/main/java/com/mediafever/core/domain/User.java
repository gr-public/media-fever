package com.mediafever.core.domain;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import org.apache.commons.lang.StringUtils;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.jdroid.javaweb.domain.Entity;
import com.jdroid.javaweb.domain.FileEntity;
import com.jdroid.javaweb.domain.Password;
import com.jdroid.javaweb.exception.InvalidAuthenticationException;
import com.jdroid.javaweb.guava.predicate.EqualsPropertyPredicate;
import com.jdroid.javaweb.push.Device;
import com.jdroid.javaweb.utils.CollectionUtils;
import com.mediafever.api.exception.ServerErrorCode;
import com.mediafever.context.ApplicationContext;

/**
 * 
 * @author Maxi Rosson
 */
@javax.persistence.Entity
public class User extends Entity {
	
	private String email;
	
	@Embedded
	private Password password;
	
	private String userToken;
	
	private String firstName;
	private String lastName;
	
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "imageId", nullable = true)
	private FileEntity image;
	
	@ManyToMany
	@JoinTable(name = "Friendship", joinColumns = @JoinColumn(name = "userId"), inverseJoinColumns = @JoinColumn(
			name = "friendId"))
	@OrderBy("firstName, lastName")
	private List<User> friends = Lists.newArrayList();
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<FriendRequest> friendRequests = Sets.newHashSet();
	
	private Boolean publicProfile;
	
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "facebookAccountId", nullable = true)
	private FacebookAccount facebookAccount;
	
	@OneToMany(targetEntity = Device.class, fetch = FetchType.LAZY)
	@JoinTable(name = "User_Device", joinColumns = @JoinColumn(name = "userId"), inverseJoinColumns = @JoinColumn(
			name = "deviceId"))
	private List<Device> devices = Lists.newArrayList();
	
	/**
	 * Default constructor.
	 */
	@SuppressWarnings("unused")
	private User() {
		// Do nothing, is required by hibernate
	}
	
	public User(String email, String password, String firstName, String lastName, Boolean publicProfile) {
		validate(password);
		this.password = new Password(password);
		userToken = UUID.randomUUID().toString();
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.publicProfile = publicProfile != null ? publicProfile : true;
	}
	
	private void validate(String password) {
		if (StringUtils.isBlank(password)) {
			throw ServerErrorCode.PASSWORD_REQUIRED.newBusinessException();
		}
	}
	
	public void modify(String email, String password, String firstName, String lastName, Boolean publicProfile,
			FileEntity image) {
		this.email = email;
		if (password != null) {
			this.password = new Password(password);
		}
		this.firstName = firstName;
		this.lastName = lastName;
		this.publicProfile = publicProfile;
		if (image != null) {
			// This is to avoid Hibernate Bug: https://hibernate.onjira.com/browse/HHH-6484. But also is a performance
			// improvement, to avoid an update and insert on the database
			if (this.image != null) {
				this.image.modify(image.getContent(), image.getName());
			} else {
				this.image = image;
			}
		}
	}
	
	/**
	 * Links the {@link User} to his {@link FacebookAccount}. If the user already has a {@link FacebookAccount}, then
	 * its token and expiration date are updated.
	 * 
	 * @param facebookUserId The FB user id.
	 * @param facebookAccessToken The FB access token.
	 */
	public void linkToFacebookAccount(String facebookUserId, String facebookAccessToken) {
		if (facebookAccount == null) {
			facebookAccount = new FacebookAccount(facebookUserId, facebookAccessToken);
		} else {
			facebookAccount.update(facebookAccessToken);
		}
	}
	
	/**
	 * Unlinks the {@link User} to his {@link FacebookAccount}.
	 */
	public void unlinkFacebookAccount() {
		facebookAccount = null;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public String getFullName() {
		return firstName + " " + lastName;
	}
	
	public void verifyPassword(String password) throws InvalidAuthenticationException {
		this.password.verify(password);
	}
	
	public String getUserToken() {
		return userToken;
	}
	
	public List<User> getFriends() {
		return friends;
	}
	
	public Boolean isFriendOf(User user) {
		return friends.contains(user);
	}
	
	public String getImageUrl() {
		String imageUrl = getLocalImageUrl();
		return imageUrl != null ? imageUrl : getFacebookImageUrl();
	}
	
	private String getLocalImageUrl() {
		String imageUrl = null;
		if (image != null) {
			imageUrl = ApplicationContext.get().getAppURL() + "/images/" + image.getName();
		}
		return imageUrl;
	}
	
	private String getFacebookImageUrl() {
		return facebookAccount != null ? facebookAccount.getProfilePictureURL() : null;
	}
	
	public Set<FriendRequest> getFriendRequests() {
		return friendRequests;
	}
	
	public Boolean hasFriendRequest(User user) {
		return Iterables.any(friendRequests, new EqualsPropertyPredicate<FriendRequest>("sender", user));
	}
	
	public FriendRequest getFriendRequest(User user) {
		return CollectionUtils.find(friendRequests, new EqualsPropertyPredicate<FriendRequest>("sender", user));
	}
	
	/**
	 * Request a user to be his friend. If I have already asked him, then we do nothing.
	 * 
	 * @param friend An user I want to ask his friendship
	 * @return the {@link FriendRequest}
	 */
	public FriendRequest inviteFriend(User friend) {
		FriendRequest friendRequest = null;
		// Don't invite a user that is my friend.
		if (!friend.hasFriendRequest(this) && !friends.contains(friend)) {
			friendRequest = new FriendRequest(friend, this);
			friend.friendRequests.add(friendRequest);
		} else {
			// Add friend if the already have a pending request
			FriendRequest friendRequestToAccept = getFriendRequest(friend);
			if (friendRequestToAccept != null) {
				friendRequestToAccept.accept();
			}
		}
		return friendRequest;
	}
	
	/**
	 * Remove a friend
	 * 
	 * @param friend The friend to be removed
	 */
	public void removeFriend(User friend) {
		if (friends.contains(friend)) {
			friends.remove(friend);
			friend.friends.remove(this);
		}
	}
	
	public void acceptFriendRequest(FriendRequest friendRequest) {
		User sender = friendRequest.getSender();
		friends.add(sender);
		sender.friends.add(this);
		friendRequests.remove(friendRequest);
	}
	
	public void rejectFriendRequest(FriendRequest friendRequest) {
		friendRequests.remove(friendRequest);
	}
	
	public Boolean hasPublicProfile() {
		return publicProfile;
	}
	
	public FileEntity getImage() {
		return image;
	}
	
	/**
	 * @return the linked {@link FacebookAccount}.
	 */
	public FacebookAccount getFacebookAccount() {
		return facebookAccount;
	}
	
	public void addDevice(Device device) {
		if (!devices.contains(device)) {
			devices.add(device);
		}
	}
	
	public void removeDevice(Device device) {
		devices.remove(device);
	}
	
	/**
	 * @return the devices
	 */
	public List<Device> getDevices() {
		return devices;
	}
}
