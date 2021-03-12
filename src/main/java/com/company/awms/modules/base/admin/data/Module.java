package com.company.awms.modules.base.admin.data;

import org.springframework.data.annotation.Id;

public class Module {
	
	@Id
	String id;
	
	String name;
	boolean active;
	boolean base;
	
	public Module(String name, boolean active, boolean base) {
		this.name = name;
		this.active = active;
		this.base = base;
	}
	
	public String getID() {
		return id;
	}
	public String getName() {
		return name;
	}
	public boolean isActive() {
		return active;
	}
	public boolean isBase() {
		return base;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public void setBase(boolean base) {
		this.base = base;
	}
}
