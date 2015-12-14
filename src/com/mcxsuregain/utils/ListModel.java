package com.mcxsuregain.utils;

public class ListModel {

	private String calltext;

	public String getCalltext() {
		return calltext;
	}

	public void setCalltext(String calltext) {
		this.calltext = calltext;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPL() {
		return PL;
	}

	public void setPL(String pL) {
		PL = pL;
	}

	public String getLatestTime() {
		return latestTime;
	}

	public void setLatestTime(String latestTime) {
		this.latestTime = latestTime;
	}

	public String getIsStopLoss() {
		return isStopLoss;
	}

	public void setIsStopLoss(String isStopLoss) {
		this.isStopLoss = isStopLoss;
	}

	private String type;
	private String PL;
	private String latestTime;
	private String isStopLoss;

}
