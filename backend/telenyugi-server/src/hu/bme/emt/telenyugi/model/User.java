package hu.bme.emt.telenyugi.model;

public class User {

	private int ID;
	private String name;
	private String state;

	public User(final int ID, final String name) {
		this.ID = ID;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public int getID() {
		return ID;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
}
