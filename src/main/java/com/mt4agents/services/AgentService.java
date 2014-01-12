package com.mt4agents.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ValidationUtils;

import com.mt4agents.dao.AgentDAO;
import com.mt4agents.dto.AgentDTO;
import com.mt4agents.dto.MT4UserDTO;
import com.mt4agents.dto.UserDTO;
import com.mt4agents.entities.Agent;
import com.mt4agents.entities.AgentRelationship;
import com.mt4agents.entities.users.AgentUser;
import com.mt4agents.entities.users.User;
import com.mt4agents.exceptions.AgentException;
import com.mt4agents.exceptions.AgentRelationshipException;
import com.mt4agents.transformers.AgentToAgentDTO;
import com.mt4agents.util.ValidationUtils_;
import com.mt4agents.validation.SaveAgentValidator;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class AgentService {

	private static final Logger logger = Logger.getLogger(AgentService.class);

	private AgentDAO agentDAO;
	private AgentRelationshipService agentRelationshipService;
	private UserService userService;
	private MT4RemoteService mt4RemoteService;
	private SaveAgentValidator saveAgentValidator;
	private AgentToAgentDTO agentToAgentDTO;
	private MessageSource messageSource;

	public void setAgentDAO(AgentDAO agentDAO) {
		this.agentDAO = agentDAO;
	}

	public void setAgentRelationshipService(
			AgentRelationshipService agentRelationshipService) {
		this.agentRelationshipService = agentRelationshipService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setMt4RemoteService(MT4RemoteService mt4RemoteService) {
		this.mt4RemoteService = mt4RemoteService;
	}

	public void setSaveAgentValidator(SaveAgentValidator saveAgentValidator) {
		this.saveAgentValidator = saveAgentValidator;
	}

	public void setAgentToAgentDTO(AgentToAgentDTO agentToAgentDTO) {
		this.agentToAgentDTO = agentToAgentDTO;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public Agent saveAgent(AgentDTO agentDTO) throws Exception {
		BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
				agentDTO, "agent");
		ValidationUtils.invokeValidator(saveAgentValidator, agentDTO, errors);
		ValidationUtils_.handleValidationErrors(errors, messageSource,
				AgentService.class);
		return saveAgent(agentDTO.getAgentId(), agentDTO.getCommission(),
				agentDTO.getLogin(), agentDTO.getParentAgentId());
	}

	public Agent saveAgentWithNewAgentUser(AgentDTO agentDTO, String username,
			String password1, String password2) throws Exception {

		// TODO: write test for username is in use
		if (userService.isUsernameInUse(username)) {
			throw new AgentException(messageSource.getMessage(
					"mt4agents.exception.user.usernameinuse", null, Locale.US));
		}

		Agent agent = saveAgent(agentDTO);
		agentDTO.setAgentId(agent.getId());
		UserDTO userDTO = new UserDTO();
		userDTO.setUsername(username);
		userDTO.setPassword(password1);
		userDTO.setNewPassword1(password1);
		userDTO.setNewPassword2(password2);
		userDTO.setAgentDTO(agentDTO);
		userDTO.assignAgentRole();
		userService.saveUser(userDTO);
		return agent;
	}

	private Agent saveAgent(Integer id, Double commission, Integer mt4Login,
			Integer parentId) throws AgentException, AgentRelationshipException {

		Agent agent = getAgentById(id);
		if (agent == null) {
			agent = new Agent();
			agent.setMt4Login(mt4Login);

			/*
			 * Only set the name when agent is new.
			 */
			MT4UserDTO userDTO = mt4RemoteService.getUserByMT4Login(mt4Login);
			if (userDTO != null) {
				agent.setName(userDTO.getName());
			} else {
				throw new AgentException(messageSource.getMessage(
						"mt4agents.exception.agent.mt4login.invalidlogin",
						new Object[] { mt4Login }, Locale.US));
			}
		}

		logger.info("Setting commission " + commission + " to agent " + id);
		if (commission != null) {
			agent.setCommission(commission);
		}

		agentDAO.save(agent);

		Integer agentId = agent.getId();
		agentRelationshipService.saveRelationship(parentId, agentId);

		AgentRelationship parentAgentRelationship = agentRelationshipService
				.getParentRelationship(agentId);

		agent.setParentRelationship(parentAgentRelationship);

		agentDAO.save(agent);

		return agent;
	}

	public void deleteAgent(Integer agentId) throws AgentException {
		Agent agent = getAgentById(agentId);
		boolean hasClients = agent.getClients().size() > 0;
		List<AgentRelationship> childrenRelationships = agentRelationshipService
				.getChildrenRelationships(agentId);
		boolean hasDownlines = childrenRelationships.size() > 0;

		if (hasClients) {
			throw new AgentException(messageSource.getMessage(
					"mt4agents.exception.agent.delete.hasclients", null,
					Locale.US));
		}

		if (hasDownlines) {
			throw new AgentException(messageSource.getMessage(
					"mt4agents.exception.agent.delete.hasdownlines", null,
					Locale.US));
		}

		AgentUser user = userService.getAgentUserById(agentId);
		userService.deleteUser(user.getId());
		agentDAO.delete(agentId);
	}

	public List<Agent> getAgents() {
		return agentDAO.get();
		// List<Integer> agentsLogins = getAllAgentsLogins();
		// mt4RemoteService.getUsersByMT4Logins(agentsLogins);
		// TODO: N+1, NEEDS TO BE RESOLVED!!!!
		// for (Agent agent : agents) {
		// MT4UserDTO mt4User = mt4RemoteService.getUserByMT4Login(agent
		// .getMt4Login());
		//
		// //agent.setName(mt4User.getName());
		// }
		// return agents;
	}

	public Map<Integer, AgentDTO> findAgentDTOs(String search, int offset,
			int limit) {
		Map<Integer, AgentDTO> agentDTOs = getAgentDTOsOrganisedByLogin(search,
				offset, limit);
		List<Integer> agentMt4Logins = new ArrayList<Integer>(
				agentDTOs.keySet());
		List<MT4UserDTO> mt4Users = mt4RemoteService
				.getUsersByMT4Logins(agentMt4Logins);

		// Double for loop to avoid N+1
		// Find matching mt4 user and set that against the matching agent
		for (Integer agentLogin : agentMt4Logins) {
			for (MT4UserDTO mt4User : mt4Users) {
				if (agentLogin.intValue() == mt4User.getLogin().intValue()) {
					agentDTOs.get(agentLogin).setMt4User(mt4User);
					break;
				}
			}
		}
		return agentDTOs;
	}

	public List<AgentDTO> getAgentDTOs() {
		List<Agent> agents = getAgents();
		List<AgentDTO> agentDTOs = agentToAgentDTO.transformMany(agents);
		for (AgentDTO agentDTO : agentDTOs) {
			MT4UserDTO mt4User = mt4RemoteService.getUserByMT4Login(agentDTO
					.getLogin());
			if (mt4User != null) {
				agentDTO.setMt4User(mt4User);
			}
		}
		return agentDTOs;
	}

	public List<AgentDTO> getAgentDTOs(String search) {
		// 1. Get all agents matched by search term.
		// 2. Transform all agents into dtos.
		// 3. Acquire all agents' logins.
		// 4. Do a batch mt4_user select on agents' logins.
		// 5. Set MT4UserDTO into agent dto.
		// 6. Return agents.
		List<Agent> agents = agentDAO.get(search);
		List<AgentDTO> agentDTOs = agentToAgentDTO.transformMany(agents);
		List<Integer> agentLogins = new ArrayList<Integer>();
		for (AgentDTO agentDTO : agentDTOs) {
			agentLogins.add(agentDTO.getLogin());
		}
		List<MT4UserDTO> userDTOs = mt4RemoteService
				.getUsersByMT4Logins(agentLogins);
		for (AgentDTO agentDTO : agentDTOs) {
			for (MT4UserDTO userDTO : userDTOs) {
				if (userDTO.getLogin() == agentDTO.getLogin()) {
					agentDTO.setMt4User(userDTO);
					break;
				}
			}
		}
		return agentDTOs;
	}

	public Map<Integer, AgentDTO> getAgentDTOsOrganisedByLogin() {
		List<Agent> agents = getAgents();
		return agentToAgentDTO.transformManyOrganisedByLogin(agents);
	}

	public Map<Integer, AgentDTO> getAgentDTOsOrganisedByLogin(String search,
			int offset, int limit) {
		List<Agent> agents = agentDAO.find(search, offset, limit);
		return agentToAgentDTO.transformManyOrganisedByLogin(agents);
	}

	public Agent getAgentById(Integer agentId) {
		if (agentId != null) {
			return agentDAO.read(agentId);
		} else {
			return null;
		}
	}

	public Agent getAgentByLogin(Integer mt4Login) {
		return agentDAO.readByMT4Login(mt4Login);
	}

	public Agent getAgentByUsername(String username)
			throws NoSuchMessageException, AgentException {
		User user = userService.getUserByUsername(username);
		if (!(user instanceof AgentUser)) {
			throw new AgentException(messageSource.getMessage(
					"mt4agents.exception.agent.username.notagent", null,
					Locale.US));
		} else {
			AgentUser agentUser = (AgentUser) user;
			return agentUser.getAgent();
		}
	}

	public AgentDTO getAgentDTOByLogin(Integer mt4Login) {
		return agentToAgentDTO.transform(getAgentByLogin(mt4Login));
	}

	public AgentDTO getAgentDTOByUsername(String username)
			throws NoSuchMessageException, AgentException {
		return agentToAgentDTO.transform(getAgentByUsername(username));
	}

	public List<Integer> getAllAgentsLogins() {
		return agentDAO.getAllAgentsLogins();
	}

	public Integer getAgentsCount(String search) {
		return agentDAO.count(search);
	}

	public boolean checkExistsById(Integer agentId) {
		if (agentId != null) {
			return agentDAO.checkById(agentId);
		} else {
			return false;
		}
	}

	public boolean checkExistsByMT4Login(Integer mt4Login) {
		return agentDAO.checkByMT4Login(mt4Login);
	}
}
