package com.mt4agents.services;

import java.util.Locale;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.mt4agents.dto.UserDTO;
import com.mt4agents.entities.Agent;
import com.mt4agents.entities.users.AgentUser;
import com.mt4agents.entities.users.User;
import com.mt4agents.exceptions.UserServiceException;
import com.mt4agents.transformers.AgentToAgentDTO;
import com.mt4agents.util.DataGenerator;

public class UserServiceTest extends BaseTest {

	@Autowired
	private UserService userService;

	@Autowired
	private AgentToAgentDTO agentToAgentDTO;

	@Autowired
	private SaltSource saltSource;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private DataGenerator dataGenerator;
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	@Transactional
	public void saveAgentUser_ValidAgent() throws UserServiceException {

		String username = "user1";
		String password = "password";

		Agent agent = dataGenerator.createRandomAgent();
		UserDTO userDTO = new UserDTO();
		userDTO.setUsername(username);
		userDTO.setPassword(password);
		userDTO.setNewPassword1(password);
		userDTO.setNewPassword2(password);
		userDTO.setAgentDTO(agentToAgentDTO.transform(agent));
		userDTO.assignAgentRole();
		User user = userService.saveUser(userDTO);

		Assert.assertNotNull(user);
		Assert.assertEquals(username, user.getUsername());
		// Assert.assertEquals(password, user.getPassword());
		Assert.assertTrue(user instanceof AgentUser);

		AgentUser agentUser = (AgentUser) user;
		Assert.assertSame(agent, agentUser.getAgent());
	}

	@Test(expected = UserServiceException.class)
	@Transactional
	public void saveAgentUser_InvalidAgent() throws UserServiceException {
		String username = "user1";
		String password = "password";

		try {
			UserDTO userDTO = new UserDTO();
			userDTO.setUsername(username);
			userDTO.setPassword(password);
			userDTO.assignAgentRole();
			userService.saveUser(userDTO);
		} catch (UserServiceException ex) {
			if (ex.getMessage().equals(
					messageSource.getMessage(
							"mt4agents.exception.user.agentrequired", null,
							Locale.US))) {
				throw ex;
			} else {
				ex.printStackTrace();
				Assert.fail("Expected exception message failure.");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Assert.fail("Incorrect exception thrown");
		}
	}

	@Test
	@Transactional
	public void saveAgentUser_ValidRole() {
	}

	@Test
	@Transactional
	public void saveAgentUser_InvalidRole() {
	}

	@Test(expected = UserServiceException.class)
	@Transactional
	public void saveAgentUser_WithNoUsername() throws UserServiceException {
		try {
			UserDTO userDTO = new UserDTO();
			userDTO.assignAgentRole();
			userService.saveUser(userDTO);
		} catch (UserServiceException ex) {
			if (ex.getMessage().equals(
					messageSource.getMessage(
							"mt4agents.exception.user.usernamerequired", null,
							Locale.US))) {
				throw ex;
			} else {
				ex.printStackTrace();
				Assert.fail("Expected exception message failure.");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Assert.fail("Incorrect exception thrown");
		}
	}

	@Test
	@Transactional
	public void saveAgentUser_WithBlankPassword() {

	}

	@Test
	@Transactional
	public void changePassword() throws UserServiceException {

		String username = "User1111";
		String password = "password123";
		String newPassword = "pppqqwe1";

		UserDTO userDTO = new UserDTO();
		userDTO.setUsername(username);
		userDTO.setPassword(password);
		userDTO.setNewPassword1(password);
		userDTO.setNewPassword2(password);
		userDTO.assignAdminRole();
		User user = userService.saveUser(userDTO);

		UserDTO changePassword = new UserDTO();
		changePassword.setUsername(user.getUsername());
		changePassword.setPassword(password);
		changePassword.setNewPassword1(newPassword);
		changePassword.setNewPassword2(newPassword);
		userService.saveUser(changePassword);

		// Assert.assertEquals(newPassword, updatedUser.getPassword());
	}

	@Test(expected = UserServiceException.class)
	@Transactional
	public void changePassword_NoMatch() throws UserServiceException {
		String username = "User1111";
		String password = "password123";
		String newPassword = "pppqqwe1";

		try {
			UserDTO userDTO = new UserDTO();
			userDTO.setUsername(username);
			userDTO.setPassword(password);
			userDTO.setNewPassword1(password);
			userDTO.setNewPassword2(password);
			userDTO.assignAdminRole();
			User user = userService.saveUser(userDTO);

			UserDTO changePassword = new UserDTO();
			changePassword.setUsername(user.getUsername());
			changePassword.setPassword(password);
			changePassword.setNewPassword1(newPassword);
			changePassword.setNewPassword2(newPassword + "!!!");
			userService.saveUser(changePassword);
		} catch (UserServiceException ex) {
			if (ex.getMessage().equals(
					messageSource.getMessage(
							"mt4agents.exception.user.newpasswordsnotmatch",
							null, Locale.US))) {
				throw ex;
			} else {
				ex.printStackTrace();
				Assert.fail("Expected exception message failure.");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Assert.fail("Incorrect exception thrown");
		}
	}

	@Test(expected = UserServiceException.class)
	@Transactional
	public void changePassword_CurrentPasswordWrong()
			throws UserServiceException {
		String username = "User1111";
		String password = "password123";
		String newPassword = "pppqqwe1";

		try {
			UserDTO userDTO = new UserDTO();
			userDTO.setUsername(username);
			userDTO.setPassword(password);
			userDTO.setNewPassword1(password);
			userDTO.setNewPassword2(password);
			userDTO.assignAdminRole();
			User user = userService.saveUser(userDTO);

			UserDTO changePassword = new UserDTO();
			changePassword.setUsername(user.getUsername());
			changePassword.setPassword(password + "!!!");
			changePassword.setNewPassword1(newPassword);
			changePassword.setNewPassword2(newPassword);
			userService.saveUser(changePassword);
		} catch (UserServiceException ex) {
			if (ex.getMessage().equals(
					messageSource.getMessage(
							"mt4agents.exception.user.currentpasswordwrong",
							null, Locale.US))) {
				throw ex;
			} else {
				ex.printStackTrace();
				Assert.fail("Expected exception message failure.");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Assert.fail("Incorrect exception thrown");
		}
	}

	@Test
	@Transactional
	public void resetPassword_Agent() throws UserServiceException {
		String username = "user1";
		String password = "password";
		String newPassword = "passwordDDDSSSXX";

		Agent agent = dataGenerator.createRandomAgent();
		UserDTO userDTO = new UserDTO();
		userDTO.setUsername(username);
		userDTO.setPassword(password);
		userDTO.setNewPassword1(password);
		userDTO.setNewPassword2(password);
		userDTO.setAgentDTO(agentToAgentDTO.transform(agent));
		userDTO.assignAgentRole();
		userService.saveUser(userDTO);

		userService.resetPassword(username, newPassword);

		User user = userService.getUserByUsername(username);
		String hashedPassword = passwordEncoder.encodePassword(newPassword,
				saltSource.getSalt(user));

		Assert.assertEquals(hashedPassword, user.getPassword());
	}

	@Test
	@Transactional
	public void resetPassword_AgentID() throws UserServiceException {
		String username = "user1";
		String password = "password";
		String newPassword = "passwordDDDSSSXX";

		Agent agent = dataGenerator.createRandomAgent();
		UserDTO userDTO = new UserDTO();
		userDTO.setUsername(username);
		userDTO.setPassword(password);
		userDTO.setNewPassword1(password);
		userDTO.setNewPassword2(password);
		userDTO.setAgentDTO(agentToAgentDTO.transform(agent));
		userDTO.assignAgentRole();
		userService.saveUser(userDTO);

		userService.resetAgentPassword(agent.getId(), newPassword);

		User user = userService.getUserByUsername(username);
		String hashedPassword = passwordEncoder.encodePassword(newPassword,
				saltSource.getSalt(user));

		Assert.assertEquals(hashedPassword, user.getPassword());
	}
}
