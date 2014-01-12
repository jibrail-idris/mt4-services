package com.mt4agents.util;

import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ObjectError;

import com.mt4agents.exceptions.AgentClientException;
import com.mt4agents.exceptions.AgentException;
import com.mt4agents.services.AgentClientService;
import com.mt4agents.services.AgentService;

public class ValidationUtils_ {
	public static void handleValidationErrors(BeanPropertyBindingResult errors,
			MessageSource messageSource, Class<?> clazz) throws Exception {
		if (errors.hasErrors()) {
			List<ObjectError> errorsList = errors.getAllErrors();
			for (ObjectError objectError : errorsList) {
				if (clazz == AgentService.class) {
					throw new AgentException(messageSource.getMessage(
							objectError.getCode(), objectError.getArguments(),
							Locale.US));
				} else if (clazz == AgentClientService.class) {
					throw new AgentClientException(messageSource.getMessage(
							objectError.getCode(), objectError.getArguments(),
							Locale.US));
				}
			}
		}
	}
}
