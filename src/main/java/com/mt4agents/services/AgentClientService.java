package com.mt4agents.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ValidationUtils;

import com.mt4agents.dao.AgentClientDAO;
import com.mt4agents.dto.AgentClientDTO;
import com.mt4agents.dto.MT4CommissionDTO;
import com.mt4agents.dto.MT4UserDTO;
import com.mt4agents.entities.Agent;
import com.mt4agents.entities.AgentClient;
import com.mt4agents.exceptions.MT4RemoteServiceException;
import com.mt4agents.transformers.AgentClientToAgentClientDTO;
import com.mt4agents.util.ValidationUtils_;
import com.mt4agents.validation.SaveAgentClientValidator;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class AgentClientService {

	private AgentClientDAO agentClientDAO;
	private AgentService agentService;
	private ClientTradesService clientTradesService;
	private MT4RemoteService mt4RemoteService;
	private SaveAgentClientValidator saveAgentClientValidator;
	private AgentClientToAgentClientDTO agentClientToAgentClientDTO;
	private MessageSource messageSource;

	public void setAgentClientDAO(AgentClientDAO agentClientDAO) {
		this.agentClientDAO = agentClientDAO;
	}

	public void setAgentService(AgentService agentService) {
		this.agentService = agentService;
	}

	public void setClientTradesService(ClientTradesService clientTradesService) {
		this.clientTradesService = clientTradesService;
	}

	public void setMt4RemoteService(MT4RemoteService mt4RemoteService) {
		this.mt4RemoteService = mt4RemoteService;
	}

	public void setSaveAgentClientValidator(
			SaveAgentClientValidator saveAgentClientValidator) {
		this.saveAgentClientValidator = saveAgentClientValidator;
	}

	public void setAgentClientToAgentClientDTO(
			AgentClientToAgentClientDTO agentClientToAgentClientDTO) {
		this.agentClientToAgentClientDTO = agentClientToAgentClientDTO;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	/**
	 * Batch save clients per agent.
	 * 
	 * @param agentId
	 * @param agentClientDTOs
	 * @throws Exception
	 */
	public void saveClients(Integer agentId,
			List<AgentClientDTO> agentClientDTOs) throws Exception {
		if (agentClientDTOs != null) {
			List<Integer> clientLogins = new ArrayList<Integer>();
			for (AgentClientDTO client : agentClientDTOs) {
				clientLogins.add(client.getLogin());
				saveClient(client);
			}
			agentClientDAO.deleteNotInLoginList(agentId, clientLogins);
		}
	}

	public AgentClient saveClient(AgentClientDTO agentClientDTO)
			throws Exception {
		Integer mt4Login = agentClientDTO.getLogin();
		MT4UserDTO userDTO = mt4RemoteService.getUserByMT4Login(mt4Login);
		agentClientDTO.setMT4UserDTO(userDTO);
		BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
				agentClientDTO, "agentClient");
		ValidationUtils.invokeValidator(saveAgentClientValidator,
				agentClientDTO, errors);
		ValidationUtils_.handleValidationErrors(errors, messageSource,
				AgentClientService.class);
		// I need to get existing client object by login. If not found, create
		// new.
		AgentClient client = agentClientDAO.readByLogin(mt4Login);
		if (client == null) {
			client = new AgentClient();
			// only set registration date for new clients.
			client.setRegistrationDate(agentClientDTO.getRegistrationDate());
		}
		Agent agent = agentService.getAgentById(agentClientDTO.getAgentId());
		client.setMt4Login(mt4Login);
		client.setName(userDTO.getName());
		client.setEmail(userDTO.getEmail());
		client.setAgent(agent);
		agentClientDAO.save(client);
		return client;
	}

	public void deleteClient(Integer agentClientId) {
		agentClientDAO.delete(agentClientId);
	}

	// TODO: Write test
	public void deleteClientByLogin(Integer agentClientLogin) {
		AgentClient agentClient = getClientByLogin(agentClientLogin);
		agentClientDAO.delete(agentClient.getId());
	}

	/**
	 * Returns a list of clients assigned to an agent.
	 * 
	 * @param agentId
	 * @return
	 */
	public List<AgentClient> getClients(Integer agentId) {
		List<AgentClient> clients = agentClientDAO.getClientsByAgent(agentId);
		// Very bad N+1, clients requests for this.
		for (AgentClient client : clients) {
			MT4UserDTO mt4User = mt4RemoteService
					.getUserByMT4LoginSimple(client.getMt4Login());
			client.setName(mt4User.getName());
			client.setEmail(mt4User.getEmail());
		}
		return clients;
	}

	public List<Integer> getClientsIds(Integer agentId) {
		return agentClientDAO.getClientIdsByAgent(agentId);
	}

	// TODO: Write test
	public List<Integer> getClientsLogins(Integer agentId) {
		return agentClientDAO.getClientLoginsByAgent(agentId);
	}

	// TODO: Write test
	public List<Integer> getAllClientsLogins() {
		return agentClientDAO.getAllClientsLogins();
	}

	public List<AgentClientDTO> getClientsWithCommission(Integer agentId,
			Date startOpenTime, Date endOpenTime, Date startCloseTime,
			Date endCloseTime) throws MT4RemoteServiceException {
		Agent agent = agentService.getAgentById(agentId);
		List<Integer> clientIds = clientTradesService
				.getAllDownlinesClientsList(agentId, null);
		List<AgentClient> clients = clientTradesService.getAllDownlinesClients(
				agentId, null);		
		Double commission = agent.getCommission();
		List<MT4CommissionDTO> commissions = mt4RemoteService.getCommissions(
				clientIds, commission, startOpenTime, endOpenTime,
				startCloseTime, endCloseTime);
		return agentClientToAgentClientDTO.transformMany(clients, commissions);
	}

	public boolean isClientAssignedToAgent(Integer agentId,
			Integer agentClientId) {
		return agentClientDAO.checkIfAssigned(agentId, agentClientId);
	}

	public boolean isClientLoginAssignedToAgent(Integer agentId,
			Integer agentClientLogin) {
		return agentClientDAO.checkIfLoginAssigned(agentId, agentClientLogin);
	}

	public void removeClient(Integer agentClientId) {
		agentClientDAO.delete(agentClientId);
	}

	public AgentClient getClientById(Integer agentClientId) {
		return agentClientDAO.read(agentClientId);
	}

	public AgentClient getClientByLogin(Integer mt4Login) {
		return agentClientDAO.readByLogin(mt4Login);
	}

	public boolean checkExistsByMT4Login(Integer mt4Login) {
		return agentClientDAO.checkByMT4Login(mt4Login);
	}
}
