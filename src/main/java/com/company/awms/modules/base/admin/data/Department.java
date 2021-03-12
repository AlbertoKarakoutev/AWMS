package com.company.awms.modules.base.admin.data;

import java.util.ArrayList;
import java.util.stream.Stream;

import org.json.simple.JSONObject;
import org.springframework.data.annotation.Id;

public class Department {
	
	@Id
	private String id;
	
	private Character departmentCode;
	
	private String name;
	private String scheduleType;
	
	private Boolean universalSchedule;
	private Boolean workOnWeekends;
	
	ArrayList<Department> levels;
	
	private Integer breakBetweenShifts;
	private Integer dailyBreakDurationTotal;
	private int[] dailyHours;
	private Integer shiftLength;
	private Integer employeesPerShift;
	private Integer monthlyWorkDays;
	
	public Department(char departmentCode, String name, Boolean universalSchedule) {
		this.departmentCode = departmentCode;
		this.name = name;
		this.universalSchedule = universalSchedule;
		levels = new ArrayList<Department>();
	}
	
	public Department() {
	}

	@Override
	public String toString() {
		String id = "ID: " + this.id + "\n";
		String code = "Code: " + this.departmentCode + "\n";
		String name = "Name: " + this.name + "\n";
		String universal = "Universal Schedule: " + this.universalSchedule + "\n";
		String scheduleType = "Schedule Type: " + this.scheduleType + "\n";
		String workOnWeekends = "WOW: " + this.workOnWeekends + "\n";
		String breakBetweenShifts = "BBS: " + this.breakBetweenShifts + "\n";
		String shiftLength = "Shift Length: " + this.shiftLength + "\n";
		return id + code + name + universal + scheduleType + workOnWeekends + breakBetweenShifts + shiftLength;
	}
	
	
	public void update(JSONObject json) {
		String scheduleType = (String) json.get("scheduleType");
		String workOnWeekends = (String) json.get("workOnWeekends");
		String breakBetweenShifts = (String) json.get("breakBetweenShifts");
		String dailyBreakDurationTotal = (String) json.get("dailyBreakDurationTotal");
		String dailyHours = (String) json.get("dailyHours");
		String shiftLength = (String) json.get("shiftLength");
		String employeesPerShift = (String) json.get("employeesPerShift");
		String monthlyWorkDays = (String) json.get("monthlyWorkDays");
		
		if(validate(scheduleType))this.setScheduleType(scheduleType);
		if(validate(workOnWeekends))this.setWorkOnWeekends(Boolean.parseBoolean(workOnWeekends));
		if(validate(breakBetweenShifts))this.setBreakBetweenShifts(Integer.parseInt(breakBetweenShifts));
		if(validate(dailyBreakDurationTotal))this.setDailyBreakDurationTotal(Integer.parseInt(dailyBreakDurationTotal));
		if(validate(dailyHours))this.setDailyHours(Stream.of((dailyHours).split(",")).mapToInt(Integer::parseInt).toArray());
		if(validate(shiftLength))this.setShiftLength(Integer.parseInt(shiftLength));
		if(validate(employeesPerShift))this.setEmployeesPerShift(Integer.parseInt(employeesPerShift));
		if(validate(monthlyWorkDays))this.setMonthlyWorkDays(Integer.parseInt(monthlyWorkDays));

	}
	
	private boolean validate(String input) {
		return input!=null && !input.equals("");
	}
	
	public String getID() {
		return id;
	}
	
	public ArrayList<Department> getLevels() {
		return this.levels;
	}
	
	public Department getLevel(int level) {
		return this.levels.get(level);
	}
	
	public void setLevel(int index, Department level) {
		this.levels.set(index, level);
	}
	
	public void addLevel(Department level) {
		this.levels.add(level);
	}
	
	public Character getDepartmentCode() {
		return departmentCode;
	}
	public void setDepartmentCode(char departmentCode) {
		this.departmentCode = departmentCode;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getScheduleType() {
		return scheduleType;
	}
	public void setScheduleType(String scheduleType) {
		this.scheduleType = scheduleType;
	}
	public Boolean isUniversalSchedule() {
		return universalSchedule;
	}
	public void setUniversalSchedule(boolean universalSchedule) {
		this.universalSchedule = universalSchedule;
	}
	public Boolean isWorkOnWeekends() {
		return workOnWeekends;
	}
	public void setWorkOnWeekends(Boolean workOnWeekends) {
		this.workOnWeekends = workOnWeekends;
	}
	public Integer getBreakBetweenShifts() {
		return breakBetweenShifts;
	}
	public void setBreakBetweenShifts(Integer breakBetweenShifts) {
		this.breakBetweenShifts = breakBetweenShifts;
	}
	public Integer getDailyBreakDurationTotal() {
		return dailyBreakDurationTotal;
	}
	public void setDailyBreakDurationTotal(Integer dailyBreakDurationTotal) {
		this.dailyBreakDurationTotal = dailyBreakDurationTotal;
	}
	public int[] getDailyHours() {
		return dailyHours;
	}
	public void setDailyHours(int[] dailyHours) {
		this.dailyHours = dailyHours;
	}
	public Integer getShiftLength() {
		return shiftLength;
	}
	public void setShiftLength(Integer shiftLength) {
		this.shiftLength = shiftLength;
	}
	public Integer getEmployeesPerShift() {
		return employeesPerShift;
	}
	public void setEmployeesPerShift(Integer employeesPerShift) {
		this.employeesPerShift = employeesPerShift;
	}
	public Integer getMonthlyWorkDays() {
		return monthlyWorkDays;
	}
	public void setMonthlyWorkDays(Integer monthlyWorkDays) {
		this.monthlyWorkDays = monthlyWorkDays;
	}
	
	
}

