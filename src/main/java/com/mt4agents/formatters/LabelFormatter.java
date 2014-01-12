package com.mt4agents.formatters;

import org.springframework.util.StringUtils;

import com.mt4agents.dto.AgentClientDTO;
import com.mt4agents.dto.AgentDTO;
import com.mt4agents.entities.Agent;

public class LabelFormatter {
	public static void formatLabel(AgentClientDTO agentClientDTO) {
		if (agentClientDTO != null) {
			String clientName = agentClientDTO.getName();
			if (StringUtils.hasLength(clientName)) {
				StringBuilder sb = new StringBuilder(clientName);
				Integer mt4Login = agentClientDTO.getLogin();
				if (mt4Login != null) {
					sb.append(" (").append(mt4Login).append(")");
				}
				agentClientDTO.setLabel(sb.toString());
			}
		}
	}

	public static void formatLabel(AgentDTO agentDTO) {
		if (agentDTO != null) {
			String agentName = agentDTO.getName();
			if (StringUtils.hasLength(agentName)) {
				StringBuilder sb = new StringBuilder(agentName);
				Integer mt4Login = agentDTO.getLogin();
				if (mt4Login != null) {
					sb.append(" (").append(mt4Login).append(")");
				}
				agentDTO.setLabel(sb.toString());
			}
		}
	}

	public static void formatLabel(Agent agent, AgentDTO agentDTO) {
		String agentName = agent.getName();
		if (StringUtils.hasLength(agentName)) {
			StringBuilder sb = new StringBuilder(agentName);
			Integer mt4Login = agent.getMt4Login();
			if (mt4Login != null) {
				sb.append(" (").append(mt4Login).append(")");
			}
			if (agentDTO != null) {
				agentDTO.setLabel(sb.toString());
			}
		}
	}

	public static void formatAgentLabel(Agent agent,
			AgentClientDTO agentClientDTO) {
		String agentName = agent.getName();
		if (StringUtils.hasLength(agentName)) {
			StringBuilder sb = new StringBuilder(agentName);
			Integer mt4Login = agent.getMt4Login();
			if (mt4Login != null) {
				sb.append(" (").append(mt4Login).append(")");
			}
			if (agentClientDTO != null) {
				agentClientDTO.setAgentLabel(sb.toString());
			}
		}
	}

	public static void formatParentAgentLabel(Agent parentAgent,
			AgentDTO agentDTO) {
		String parentAgentName = parentAgent.getName();
		if (StringUtils.hasLength(parentAgentName)) {
			StringBuilder sb = new StringBuilder(parentAgentName);
			Integer mt4Login = parentAgent.getMt4Login();
			if (mt4Login != null) {
				sb.append(" (").append(mt4Login).append(")");
			}
			if (agentDTO != null) {
				agentDTO.setParentAgentLabel(sb.toString());
			}
		}
	}
	
	public static String formatLabelToString(Agent agent) {
		String agentName = agent.getName();
		if (StringUtils.hasLength(agentName)) {
			StringBuilder sb = new StringBuilder(agentName);
			Integer mt4Login = agent.getMt4Login();
			if (mt4Login != null) {
				sb.append(" (").append(mt4Login).append(")");
			}
			return sb.toString();
		} else {
			return "";
		}
	}
}
