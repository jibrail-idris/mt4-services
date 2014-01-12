package com.mt4agents.transformers;

import com.mt4agents.dto.UserDTO;
import com.mt4agents.entities.Agent;
import com.mt4agents.entities.users.AgentUser;
import com.mt4agents.entities.users.User;

public class UserToUserDTO extends AbstractTransformer<User, UserDTO> {

	private AgentToAgentDTO agentToAgentDTO;

	public void setAgentToAgentDTO(AgentToAgentDTO agentToAgentDTO) {
		this.agentToAgentDTO = agentToAgentDTO;
	}

	public UserDTO transform(User user) {

		UserDTO userDTO = new UserDTO();
		userDTO.setUserId(user.getId());
		userDTO.setUsername(user.getUsername());
		if (user instanceof AgentUser) {
			Agent agent = ((AgentUser) user).getAgent();
			userDTO.setAgentDTO(agentToAgentDTO.transform(agent));
		}

		return userDTO;
	}

}
