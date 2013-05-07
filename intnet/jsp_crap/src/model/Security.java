package model;

public class Security extends DatabaseObject<Security> {

	@Override
	protected String table_name() {
		return "securities";
	}
	
	public String getName() {
		return (String)get("name");
	}
	
	public void setName(String name) {
		set("name", name);
	}

	@Override
	protected Class<Security> cls() {
		return Security.class;
	}
}
