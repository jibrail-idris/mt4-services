package com.mt4agents.services;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.mt4agents.dao.UserDAO;
import com.mt4agents.dto.AgentDTO;
import com.mt4agents.dto.UserDTO;
import com.mt4agents.entities.Agent;
import com.mt4agents.entities.users.AdminUser;
import com.mt4agents.entities.users.AgentUser;
import com.mt4agents.entities.users.User;
import com.mt4agents.exceptions.UserServiceException;
import com.mt4agents.transformers.UserToUserDTO;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class UserService implements UserDetailsService {

	private UserDAO userDAO;
	private AgentService agentService;
	private UserToUserDTO userToUserDTO;
	private MessageSource messageSource;
	private SaltSource saltSource;
	private PasswordEncoder passwordEncoder;

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	public void setAgentService(AgentService agentService) {
		this.agentService = agentService;
	}

	public void setUserToUserDTO(UserToUserDTO userToUserDTO) {
		this.userToUserDTO = userToUserDTO;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setSaltSource(SaltSource saltSource) {
		this.saltSource = saltSource;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public User saveUser(UserDTO userDTO) throws UserServiceException {
		User user = makeUser(userDTO);
		saveUser(user);
		return user;
	}

	public void saveUser(User user) {
		userDAO.save(user);
	}

	// TODO: Write test
	public void deleteUser(Integer userId) {
		userDAO.delete(userId);
	}

	// TODO: Write test
	public Boolean isUsernameInUse(String username) {
		return userDAO.isUsernameInUse(username);
	}

	public User getUserByUsername(String username) {
		return userDAO.readByUsername(username);
	}

	public UserDTO getUserDTOByUsername(String username) {
		return userToUserDTO.transform(getUserByUsername(username));
	}

	public AgentUser getAgentUserById(Integer agentId) {
		return userDAO.readAgentUserById(agentId);
	}

	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		User user = getUserByUsername(username);
		return new org.springframework.security.core.userdetails.User(
				user.getUsername(), user.getPassword(), user.getAuthorities());
	}
	
	public void resetAgentPassword(Integer agentId, String newPassword) {
		AgentUser agentUser = getAgentUserById(agentId);
		resetPassword(agentUser.getUsername(), newPassword);
	}

	public void resetPassword(String username, String newPassword) {
		User user = getUserByUsername(username);
		user.setPassword(passwordEncoder.encodePassword(newPassword,
				saltSource.getSalt(user)));
		userDAO.save(user);
	}

	/**
	 * Simplified user factory
	 * 
	 * @return
	 * @throws UserServiceException
	 */
	private User makeUser(UserDTO userDTO) throws UserServiceException {
		if (userDTO != null) {
			String username = userDTO.getUsername();
			String role = userDTO.getRole();
			User user = getUserByUsername(username);

			// TODO: Refactor this block of code to SaveUserValidator class

			// If new user and username is null or blank, throw exception.
			if (user == null
					&& (username == null || !StringUtils.hasLength(username))) {
				throw new UserServiceException(messageSource.getMessage(
						"mt4agents.exception.user.usernamerequired",
						new Object[] { role }, Locale.US));
			} else {
				if (user instanceof AgentUser) {
					role = AgentUser.ROLE;
				} else if (user instanceof AdminUser) {
					role = AdminUser.ROLE;
				}
			}

			if (role != null && StringUtils.hasLength(role)) {

				boolean newUser = false;

				if (role.equals(AdminUser.ROLE)) {
					if (user == null) {
						newUser = true;
						user = new AdminUser();
						user.setUsername(username);
					}

				} else if (role.equals(AgentUser.ROLE)) {
					if (user == null) {
						newUser = true;
						user = new AgentUser();
						user.setUsername(username);

						AgentDTO agentDTO = userDTO.getAgentDTO();
						if (agentDTO != null) {
							Integer agentId = agentDTO.getAgentId();
							Agent agent = agentService.getAgentById(agentId);
							if (agent != null) {
								((AgentUser) user).setAgent(agent);
							} else {
								throw new UserServiceException(
										messageSource
												.getMessage(
														"mt4agents.exception.user.invalidagent",
														new Object[] { role },
														Locale.US));
							}
						} else {
							throw new UserServiceException(
									messageSource
											.getMessage(
													"mt4agents.exception.user.agentrequired",
													null, Locale.US));
						}
					}

				} else {
					throw new UserServiceException(messageSource.getMessage(
							"mt4agents.exception.user.invalidrole",
							new Object[] { role }, Locale.US));
				}

				if (newUser && isUsernameInUse(username)) {
					throw new UserServiceException(messageSource.getMessage(
							"mt4agents.exception.user.usernameinuse", null,
							Locale.US));
				}

				String password = userDTO.getPassword();
				String newPassword1 = userDTO.getNewPassword1();
				String newPassword2 = userDTO.getNewPassword2();

				boolean password1HasLength = StringUtils
						.hasLength(newPassword1);
				boolean password2HasLength = StringUtils
						.hasLength(newPassword2);

				if ((!password1HasLength && password2HasLength)
						|| (!password1HasLength && !password2HasLength)) {
					throw new UserServiceException(messageSource.getMessage(
							"mt4agents.exception.user.newpasswordblank", null,
							Locale.US));
				}

				if (password1HasLength && !password2HasLength) {
					throw new UserServiceException(
							messageSource
									.getMessage(
											"mt4agents.exception.user.confirmationpasswordblank",
											null, Locale.US));
				}

				if (password1HasLength && password2HasLength) {

					if (!newPassword1.equals(newPassword2)) {
						throw new UserServiceException(
								messageSource
										.getMessage(
												"mt4agents.exception.user.newpasswordsnotmatch",
												null, Locale.US));
					}
					password = passwordEncoder.encodePassword(password,
							saltSource.getSalt(user));
					if (!newUser && !password.equals(user.getPassword())) {
						throw new UserServiceException(
								messageSource
										.getMessage(
												"mt4agents.exception.user.currentpasswordwrong",
												null, Locale.US));
					}
					user.setPassword(passwordEncoder.encodePassword(
							newPassword1, saltSource.getSalt(user)));
				}

				// if (newUser) {
				// user.setPassword(passwordEncoder.encodePassword(password,
				// saltSource.getSalt(user)));
				// }

			} else {
				throw new UserServiceException(messageSource.getMessage(
						"mt4agents.exception.user.role.notassigned",
						new Object[] { role }, Locale.US));
			}

			return user;
		} else {
			return null;
		}
	}
}
