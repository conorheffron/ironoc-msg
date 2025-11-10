package net.ironoc.message.domain;

public class BroadcastUserJoinedReq {

	private String name;

	public BroadcastUserJoinedReq() {
	}

	public BroadcastUserJoinedReq(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
