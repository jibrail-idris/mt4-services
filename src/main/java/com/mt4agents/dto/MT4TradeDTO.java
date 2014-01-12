package com.mt4agents.dto;

import java.util.Date;

public class MT4TradeDTO {
	private Integer ticket;
	private Integer login;
	private String symbol;
	private Integer digits;
	private Integer cmd;
	private String cmdLabel;
	private Double volume;
	private Date openTime;
	private Double openPrice;
	private Double sl;
	private Double tp;
	private Date closeTime;
	private Date expiration;
	private Double convRate1;
	private Double convRate2;
	private Double commission;
	private Integer commissionAgent;
	private Double swaps;
	private Double closePrice;
	private Double profit;
	private Double taxes;
	private String comment;
	private Integer internalID;
	private Double marginRate;
	private Long timestamp;
	private Date modifyTime;
	private Integer agentId;
	private AgentDTO agentDTO;
	private AgentClientDTO agentClientDTO;
	public Integer getTicket() {
		return ticket;
	}
	public void setTicket(Integer ticket) {
		this.ticket = ticket;
	}
	public Integer getLogin() {
		return login;
	}
	public void setLogin(Integer login) {
		this.login = login;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public Integer getDigits() {
		return digits;
	}
	public void setDigits(Integer digits) {
		this.digits = digits;
	}
	public Integer getCmd() {
		return cmd;
	}
	public void setCmd(Integer cmd) {
		this.cmd = cmd;
	}
	public String getCmdLabel() {
		return cmdLabel;
	}
	public void setCmdLabel(String cmdLabel) {
		this.cmdLabel = cmdLabel;
	}
	public Double getVolume() {
		return volume;
	}
	public void setVolume(Double volume) {
		this.volume = volume;
	}
	public Date getOpenTime() {
		return openTime;
	}
	public void setOpenTime(Date openTime) {
		this.openTime = openTime;
	}
	public Double getOpenPrice() {
		return openPrice;
	}
	public void setOpenPrice(Double openPrice) {
		this.openPrice = openPrice;
	}
	public Double getSl() {
		return sl;
	}
	public void setSl(Double sl) {
		this.sl = sl;
	}
	public Double getTp() {
		return tp;
	}
	public void setTp(Double tp) {
		this.tp = tp;
	}
	public Date getCloseTime() {
		return closeTime;
	}
	public void setCloseTime(Date closeTime) {
		this.closeTime = closeTime;
	}
	public Date getExpiration() {
		return expiration;
	}
	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}
	public Double getConvRate1() {
		return convRate1;
	}
	public void setConvRate1(Double convRate1) {
		this.convRate1 = convRate1;
	}
	public Double getConvRate2() {
		return convRate2;
	}
	public void setConvRate2(Double convRate2) {
		this.convRate2 = convRate2;
	}
	public Double getCommission() {
		return commission;
	}
	public void setCommission(Double commission) {
		this.commission = commission;
	}
	public Integer getCommissionAgent() {
		return commissionAgent;
	}
	public void setCommissionAgent(Integer commissionAgent) {
		this.commissionAgent = commissionAgent;
	}
	public Double getSwaps() {
		return swaps;
	}
	public void setSwaps(Double swaps) {
		this.swaps = swaps;
	}
	public Double getClosePrice() {
		return closePrice;
	}
	public void setClosePrice(Double closePrice) {
		this.closePrice = closePrice;
	}
	public Double getProfit() {
		return profit;
	}
	public void setProfit(Double profit) {
		this.profit = profit;
	}
	public Double getTaxes() {
		return taxes;
	}
	public void setTaxes(Double taxes) {
		this.taxes = taxes;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Integer getInternalID() {
		return internalID;
	}
	public void setInternalID(Integer internalID) {
		this.internalID = internalID;
	}
	public Double getMarginRate() {
		return marginRate;
	}
	public void setMarginRate(Double marginRate) {
		this.marginRate = marginRate;
	}
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	public Date getModifyTime() {
		return modifyTime;
	}
	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}
	public Integer getAgentId() {
		return agentId;
	}
	public void setAgentId(Integer agentId) {
		this.agentId = agentId;
	}
	public AgentDTO getAgentDTO() {
		return agentDTO;
	}
	public void setAgentDTO(AgentDTO agentDTO) {
		this.agentDTO = agentDTO;
	}
	public AgentClientDTO getAgentClientDTO() {
		return agentClientDTO;
	}
	public void setAgentClientDTO(AgentClientDTO agentClientDTO) {
		this.agentClientDTO = agentClientDTO;
	}
}
