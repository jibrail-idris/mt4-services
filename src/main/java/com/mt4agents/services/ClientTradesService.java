package com.mt4agents.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mt4agents.dto.MT4CommissionDTO;
import com.mt4agents.dto.MT4TradeDTO;
import com.mt4agents.entities.Agent;
import com.mt4agents.entities.AgentClient;
import com.mt4agents.entities.AgentRelationship;
import com.mt4agents.exceptions.ClientTradesServiceException;
import com.mt4agents.exceptions.MT4RemoteServiceException;
import com.mt4agents.services.MT4RemoteService.TradeType;
import com.mt4agents.transformers.AgentClientToAgentClientDTO;
import com.mt4agents.transformers.AgentToAgentDTO;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class ClientTradesService {

	private AgentService agentService;
	private AgentClientService agentClientService;
	private AgentRelationshipService agentRelationshipService;
	private AgentToAgentDTO agentToAgentDTO;
	private AgentClientToAgentClientDTO agentClientToAgentClientDTO;
	private MT4RemoteService mt4RemoteService;
	private MessageSource messageSource;

	public void setAgentService(AgentService agentService) {
		this.agentService = agentService;
	}

	public void setAgentClientService(AgentClientService agentClientService) {
		this.agentClientService = agentClientService;
	}

	public void setAgentRelationshipService(
			AgentRelationshipService agentRelationshipService) {
		this.agentRelationshipService = agentRelationshipService;
	}

	public void setAgentToAgentDTO(AgentToAgentDTO agentToAgentDTO) {
		this.agentToAgentDTO = agentToAgentDTO;
	}

	public void setAgentClientToAgentClientDTO(
			AgentClientToAgentClientDTO agentClientToAgentClientDTO) {
		this.agentClientToAgentClientDTO = agentClientToAgentClientDTO;
	}

	public void setMt4RemoteService(MT4RemoteService mt4RemoteService) {
		this.mt4RemoteService = mt4RemoteService;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public List<MT4TradeDTO> getClientTrades(Integer agentClientId,
			Double commission, Date startOpenTime, Date endOpenTime,
			Date startCloseTime, Date endCloseTime)
			throws NoSuchMessageException, ClientTradesServiceException {

		AgentClient agentClient = agentClientService
				.getClientById(agentClientId);

		if (agentClient == null) {
			throw new ClientTradesServiceException(messageSource.getMessage(
					"mt4agents.exception.agentclient.invalidid",
					new Object[] { agentClientId }, Locale.US));
		}

		return getClientTrades(agentClient, commission, startOpenTime,
				endOpenTime, startCloseTime, endCloseTime);
	}

	public List<MT4TradeDTO> getClientTrades(AgentClient agentClient,
			Double commission, Date startOpenTime, Date endOpenTime,
			Date startCloseTime, Date endCloseTime) {
		return mt4RemoteService.getClientTrades(agentClient.getMt4Login(),
				commission, startOpenTime, endOpenTime, startCloseTime,
				endCloseTime);
	}

	public List<MT4TradeDTO> getClientOpenTrades(Integer clientLogin,
			Double commission, Date startOpenTime, Date endOpenTime)
			throws MT4RemoteServiceException {
		List<Integer> clients = new ArrayList<Integer>();
		clients.add(clientLogin);
		return mt4RemoteService.getClientsTrades(Arrays.asList(clientLogin),
				commission, TradeType.OPEN, startOpenTime, endOpenTime, null,
				null);
	}

	public List<MT4TradeDTO> getClientCloseTrades(Integer clientLogin,
			Double commission, Date startCloseTime, Date endCloseTime)
			throws MT4RemoteServiceException {
		return mt4RemoteService.getClientsTrades(Arrays.asList(clientLogin),
				commission, TradeType.CLOSE, null, null, startCloseTime,
				endCloseTime);
	}

	public List<MT4TradeDTO> getClientBalanceTrades(Integer clientLogin,
			Date startCloseTime, Date endCloseTime)
			throws MT4RemoteServiceException {
		return mt4RemoteService.getClientsTrades(Arrays.asList(clientLogin),
				0.00, TradeType.BALANCE, null, null, startCloseTime,
				endCloseTime);
	}

	public List<MT4TradeDTO> getClientsOpenTrades(Integer agentId,
			Date startOpenTime, Date endOpenTime)
			throws MT4RemoteServiceException {
		Agent agent = agentService.getAgentById(agentId);
		List<Integer> clients = new ArrayList<Integer>(agent
				.getClientsByLogin().keySet());
		return mt4RemoteService.getClientsTrades(clients,
				agent.getCommission(), TradeType.OPEN, startOpenTime,
				endOpenTime, null, null);
	}

	public List<MT4TradeDTO> getClientsOpenTrades(Integer agentId,
			Date startOpenTime, Date endOpenTime, Integer offset,
			Integer rowcount, String search, Integer sortColumn,
			String sortDirection) throws MT4RemoteServiceException {
		Agent agent = agentService.getAgentById(agentId);
		List<Integer> clients = new ArrayList<Integer>(agent
				.getClientsByLogin().keySet());
		return mt4RemoteService.getClientsTrades(clients,
				agent.getCommission(), TradeType.OPEN, startOpenTime,
				endOpenTime, null, null, offset, rowcount, search, sortColumn,
				sortDirection);
	}

	public Integer getClientsOpenTradesCount(Integer agentId,
			Date startOpenTime, Date endOpenTime, String search) {
		Agent agent = agentService.getAgentById(agentId);
		List<Integer> clients = new ArrayList<Integer>(agent
				.getClientsByLogin().keySet());
		return mt4RemoteService.getClientsTradesCount(clients,
				agent.getCommission(), TradeType.OPEN, startOpenTime,
				endOpenTime, null, null, search);
	}

	public List<MT4TradeDTO> getClientsCloseTrades(Integer agentId,
			Date startCloseTime, Date endCloseTime)
			throws MT4RemoteServiceException {
		Agent agent = agentService.getAgentById(agentId);
		List<Integer> clients = new ArrayList<Integer>(agent
				.getClientsByLogin().keySet());
		return mt4RemoteService.getClientsTrades(clients,
				agent.getCommission(), TradeType.CLOSE, null, null,
				startCloseTime, endCloseTime);
	}

	public List<MT4TradeDTO> getClientsCloseTrades(Integer agentId,
			Date startCloseTime, Date endCloseTime, Integer offset,
			Integer rowcount, String search, Integer sortColumn,
			String sortDirection) throws MT4RemoteServiceException {
		Agent agent = agentService.getAgentById(agentId);
		List<Integer> clients = new ArrayList<Integer>(agent
				.getClientsByLogin().keySet());
		return mt4RemoteService.getClientsTrades(clients,
				agent.getCommission(), TradeType.CLOSE, null, null,
				startCloseTime, endCloseTime, offset, rowcount, search,
				sortColumn, sortDirection);
	}

	public Integer getClientsCloseTradesCount(Integer agentId,
			Date startCloseTime, Date endCloseTime, String search) {
		Agent agent = agentService.getAgentById(agentId);
		List<Integer> clients = new ArrayList<Integer>(agent
				.getClientsByLogin().keySet());
		return mt4RemoteService.getClientsTradesCount(clients,
				agent.getCommission(), TradeType.CLOSE, null, null,
				startCloseTime, endCloseTime, search);
	}

	public List<MT4TradeDTO> getClientsBalanceTrades(Integer agentId,
			Date startCloseTime, Date endCloseTime)
			throws MT4RemoteServiceException {
		Agent agent = agentService.getAgentById(agentId);
		List<Integer> clients = new ArrayList<Integer>(agent
				.getClientsByLogin().keySet());
		return mt4RemoteService.getClientsTrades(clients,
				agent.getCommission(), TradeType.BALANCE, null, null,
				startCloseTime, endCloseTime);
	}

	public List<MT4TradeDTO> getClientsBalanceTrades(Integer agentId,
			Date startCloseTime, Date endCloseTime, Integer offset,
			Integer rowcount, String search, Integer sortColumn,
			String sortDirection) throws MT4RemoteServiceException {
		Agent agent = agentService.getAgentById(agentId);
		List<Integer> clients = new ArrayList<Integer>(agent
				.getClientsByLogin().keySet());
		return mt4RemoteService.getClientsTrades(clients,
				agent.getCommission(), TradeType.BALANCE, null, null,
				startCloseTime, endCloseTime, offset, rowcount, search,
				sortColumn, sortDirection);
	}

	public Integer getClientsBalanceTradesCount(Integer agentId,
			Date startCloseTime, Date endCloseTime, String search) {
		Agent agent = agentService.getAgentById(agentId);
		List<Integer> clients = new ArrayList<Integer>(agent
				.getClientsByLogin().keySet());
		return mt4RemoteService.getClientsTradesCount(clients,
				agent.getCommission(), TradeType.BALANCE, null, null,
				startCloseTime, endCloseTime, search);
	}

	public MT4CommissionDTO getTotalVolumeFromOpenTrades(Integer agentId,
			Date startOpenTime, Date endOpenTime) {
		Agent agent = agentService.getAgentById(agentId);
		Map<Integer, AgentClient> clients = agent.getClientsByLogin();
		return mt4RemoteService.getVolumeCommission(new ArrayList<Integer>(
				clients.keySet()), agent.getCommission(), TradeType.OPEN,
				startOpenTime, endOpenTime, null, null);
	}

	public MT4CommissionDTO getCommissionsEarnedFromCloseTrades(
			Integer agentId, Date startCloseTime, Date endCloseTime) {
		Agent agent = agentService.getAgentById(agentId);
		Map<Integer, AgentClient> clients = agent.getClientsByLogin();
		return mt4RemoteService.getVolumeCommission(new ArrayList<Integer>(
				clients.keySet()), agent.getCommission(), TradeType.CLOSE,
				null, null, startCloseTime, endCloseTime);
	}

	public List<MT4CommissionDTO> getCommissionsEarnedFromDownline(
			Integer agentId, Date startOpenTime, Date endOpenTime,
			Date startCloseTime, Date endCloseTime)
			throws MT4RemoteServiceException {

		Agent agent = agentService.getAgentById(agentId);

		// 1. Get all downlines.
		// 2. For each downline, get clients.
		List<Integer> downlinesClients = getAllDownlinesClientsList(
				agent.getId(), null);

		return mt4RemoteService.getCommissions(downlinesClients,
				agent.getCommission(), startOpenTime, endOpenTime,
				startCloseTime, endCloseTime);
	}

	public List<MT4TradeDTO> getDownlineClientsTrades(Integer agentId,
			Date startOpenTime, Date endOpenTime, Date startCloseTime,
			Date endCloseTime) throws MT4RemoteServiceException {
		// 1. Get all downlines.
		// 2. For each downline, get clients.
		List<Map<String, Object>> downlines = getAllDownlinesClientsMap(
				agentId, null);
		// 3. Pass list of clients into getClientsTrades.
		List<MT4TradeDTO> clientsTrades = mt4RemoteService
				.getDownlinesClientsTrades(downlines, null, startOpenTime,
						endOpenTime, startCloseTime, endCloseTime);
		// 4. Loop through each trade,
		// and match against the correct AgentDTO and
		// AgentClientDTO.
		for (MT4TradeDTO trade : clientsTrades) {
			Integer clientLogin = trade.getLogin();
			Integer downlineAgentId = trade.getAgentId();

			AgentClient agentClient = agentClientService
					.getClientByLogin(clientLogin);
			Agent agent = agentService.getAgentById(downlineAgentId);

			trade.setAgentClientDTO(agentClientToAgentClientDTO
					.transform(agentClient));
			trade.setAgentDTO(agentToAgentDTO.transform(agent));
		}

		return clientsTrades;
	}

	public List<MT4TradeDTO> getDownlineClientsTrades(Integer agentId,
			Date startOpenTime, Date endOpenTime, Date startCloseTime,
			Date endCloseTime, Integer offset, Integer rowcount, String search,
			Integer sortColumn, String sortDirection) {
		// 1. Get all downlines.
		// 2. For each downline, get clients.
		List<Map<String, Object>> downlines = getAllDownlinesClientsMap(
				agentId, null);
		// 3. Pass list of clients into getClientsTrades.
		List<MT4TradeDTO> clientsTrades = mt4RemoteService
				.getDownlinesClientsTrades(downlines, null, startOpenTime,
						endOpenTime, startCloseTime, endCloseTime, offset,
						rowcount, search, sortColumn, sortDirection);
		// 4. Loop through each trade,
		// and match against the correct AgentDTO and
		// AgentClientDTO.
		for (MT4TradeDTO trade : clientsTrades) {
			Integer clientLogin = trade.getLogin();
			Integer downlineAgentId = trade.getAgentId();

			AgentClient agentClient = agentClientService
					.getClientByLogin(clientLogin);
			Agent agent = agentService.getAgentById(downlineAgentId);

			trade.setAgentClientDTO(agentClientToAgentClientDTO
					.transform(agentClient));
			trade.setAgentDTO(agentToAgentDTO.transform(agent));
		}

		return clientsTrades;
	}

	public Integer getDownlineClientsTradesCount(Integer agentId,
			Date startOpenTime, Date endOpenTime, Date startCloseTime,
			Date endCloseTime, String search) {
		// 1. Get all downlines.
		// 2. For each downline, get clients.
		List<Map<String, Object>> downlines = getAllDownlinesClientsMap(
				agentId, null);
		return mt4RemoteService.getDownlinesClientsTradesCount(downlines, null,
				startOpenTime, endOpenTime, startCloseTime, endCloseTime,
				search);
	}

	public List<MT4TradeDTO> getDownlineClientsBalanceTrades(Integer agentId,
			Date startOpenTime, Date endOpenTime, Date startCloseTime,
			Date endCloseTime) {
		// 1. Get all downlines.
		// 2. For each downline, get clients.
		List<Map<String, Object>> downlines = getAllDownlinesClientsMap(
				agentId, null);
		// 3. Pass list of clients into getClientsTrades.
		List<MT4TradeDTO> clientsTrades = mt4RemoteService
				.getDownlinesClientsTrades(downlines, Arrays.asList(6),
						startOpenTime, endOpenTime, startCloseTime,
						endCloseTime);
		// 4. Loop through each trade,
		// and match against the correct AgentDTO and
		// AgentClientDTO.
		for (MT4TradeDTO trade : clientsTrades) {
			Integer clientLogin = trade.getLogin();
			Integer downlineAgentId = trade.getAgentId();

			AgentClient agentClient = agentClientService
					.getClientByLogin(clientLogin);
			Agent agent = agentService.getAgentById(downlineAgentId);

			trade.setAgentClientDTO(agentClientToAgentClientDTO
					.transform(agentClient));
			trade.setAgentDTO(agentToAgentDTO.transform(agent));
		}

		return clientsTrades;
	}

	public List<MT4TradeDTO> getDownlineClientsBalanceTrades(Integer agentId,
			Date startOpenTime, Date endOpenTime, Date startCloseTime,
			Date endCloseTime, Integer offset, Integer rowcount, String search,
			Integer sortColumn, String sortDirection) {
		// 1. Get all downlines.
		// 2. For each downline, get clients.
		List<Map<String, Object>> downlines = getAllDownlinesClientsMap(
				agentId, null);
		// 3. Pass list of clients into getClientsTrades.
		List<MT4TradeDTO> clientsTrades = mt4RemoteService
				.getDownlinesClientsTrades(downlines, Arrays.asList(6),
						startOpenTime, endOpenTime, startCloseTime,
						endCloseTime, offset, rowcount, search, sortColumn,
						sortDirection);
		// 4. Loop through each trade,
		// and match against the correct AgentDTO and
		// AgentClientDTO.
		for (MT4TradeDTO trade : clientsTrades) {
			Integer clientLogin = trade.getLogin();
			Integer downlineAgentId = trade.getAgentId();

			AgentClient agentClient = agentClientService
					.getClientByLogin(clientLogin);
			Agent agent = agentService.getAgentById(downlineAgentId);

			trade.setAgentClientDTO(agentClientToAgentClientDTO
					.transform(agentClient));
			trade.setAgentDTO(agentToAgentDTO.transform(agent));
		}

		return clientsTrades;
	}

	public Integer getDownlineClientsBalanceTradesCount(Integer agentId,
			Date startOpenTime, Date endOpenTime, Date startCloseTime,
			Date endCloseTime, String search) {
		// 1. Get all downlines.
		// 2. For each downline, get clients.
		List<Map<String, Object>> downlines = getAllDownlinesClientsMap(
				agentId, null);
		return mt4RemoteService.getDownlinesClientsTradesCount(downlines,
				Arrays.asList(6), startOpenTime, endOpenTime, startCloseTime,
				endCloseTime, search);
	}

	public List<AgentClient> getAllDownlinesClients(Integer agentId,
			List<AgentClient> downlineClients) {
		if (downlineClients == null) {
			downlineClients = new ArrayList<AgentClient>();
			Agent agent = agentService.getAgentById(agentId);
			downlineClients.addAll(agent.getClientsByLogin().values());
		}

		List<AgentRelationship> downlinesRelationships = agentRelationshipService
				.getChildrenRelationships(agentId);

		for (AgentRelationship downlineRelationship : downlinesRelationships) {
			Agent downline = downlineRelationship.getChildAgent();
			if (downline != null) {
				getAllDownlinesClients(downline.getId(), downlineClients);
			}
			downlineClients.addAll(downline.getClientsByLogin().values());
		}

		return downlineClients;
	}

	public List<Integer> getAllDownlinesClientsList(Integer agentId,
			List<Integer> downlinesClients) {
		if (downlinesClients == null) {
			downlinesClients = new ArrayList<Integer>();
			Agent agent = agentService.getAgentById(agentId);
			downlinesClients.addAll(agent.getClientsByLogin().keySet());
		}

		List<AgentRelationship> downlinesRelationships = agentRelationshipService
				.getChildrenRelationships(agentId);

		for (AgentRelationship downlineRelationship : downlinesRelationships) {
			Agent downline = downlineRelationship.getChildAgent();
			if (downline != null) {
				getAllDownlinesClientsList(downline.getId(), downlinesClients);
			}
			Map<Integer, AgentClient> clientsByLogin = downline
					.getClientsByLogin();
			downlinesClients.addAll(clientsByLogin.keySet());
		}

		return downlinesClients;
	}

	public List<Map<String, Object>> getAllDownlinesClientsMap(Integer agentId,
			List<Map<String, Object>> downlinesClients) {
		if (downlinesClients == null) {
			downlinesClients = new ArrayList<Map<String, Object>>();
			Agent agent = agentService.getAgentById(agentId);
			Map<String, Object> agentMap = new HashMap<String, Object>();
			agentMap.put("agentId", agent.getId());
			agentMap.put("downline", agent);
			agentMap.put("commission", agent.getCommission());
			agentMap.put("clients", new ArrayList<Integer>(agent
					.getClientsByLogin().keySet()));
			downlinesClients.add(agentMap);
		}

		List<AgentRelationship> downlinesRelationships = agentRelationshipService
				.getChildrenRelationships(agentId);

		for (AgentRelationship downlineRelationship : downlinesRelationships) {
			Agent downline = downlineRelationship.getChildAgent();
			if (downline != null) {
				getAllDownlinesClientsMap(downline.getId(), downlinesClients);
			}
			Map<Integer, AgentClient> clientsByLogin = downline
					.getClientsByLogin();
			if (clientsByLogin.size() > 0) {
				Map<String, Object> downlineMap = new HashMap<String, Object>();
				downlineMap.put("agentId", downline.getId());
				downlineMap.put("downline", downline);
				downlineMap.put("commission", downline.getCommission());
				downlineMap.put("clients", new ArrayList<Integer>(
						clientsByLogin.keySet()));
				downlinesClients.add(downlineMap);
			}
		}

		return downlinesClients;
	}

	// TODO: Write test.
	public List<Map<String, Object>> getAllDownlinesClientsMap(Integer agentId,
			Double commission, List<Map<String, Object>> downlinesClients) {
		if (downlinesClients == null) {
			downlinesClients = new ArrayList<Map<String, Object>>();
			Agent agent = agentService.getAgentById(agentId);
			Map<String, Object> agentMap = new HashMap<String, Object>();
			agentMap.put("agentId", agent.getId());
			agentMap.put("downline", agent);
			agentMap.put("commission", agent.getCommission());
			agentMap.put("clients", new ArrayList<Integer>(agent
					.getClientsByLogin().keySet()));
			downlinesClients.add(agentMap);
		}

		List<AgentRelationship> downlinesRelationships = agentRelationshipService
				.getChildrenRelationships(agentId);

		for (AgentRelationship downlineRelationship : downlinesRelationships) {
			Agent downline = downlineRelationship.getChildAgent();
			if (downline != null) {
				getAllDownlinesClientsMap(downline.getId(), commission,
						downlinesClients);
			}
			Map<Integer, AgentClient> clientsByLogin = downline
					.getClientsByLogin();
			if (clientsByLogin.size() > 0) {
				Map<String, Object> downlineMap = new HashMap<String, Object>();
				downlineMap.put("agentId", downline.getId());
				downlineMap.put("downline", downline);
				downlineMap.put("commission", commission);
				downlineMap.put("clients", new ArrayList<Integer>(
						clientsByLogin.keySet()));
				downlinesClients.add(downlineMap);
			}
		}

		return downlinesClients;
	}
}
