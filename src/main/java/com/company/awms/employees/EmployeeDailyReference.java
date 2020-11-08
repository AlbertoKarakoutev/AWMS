public class EmployeeDailyReference extends Employee{
	
	//Work time should be in the {startHour, startMinutes, endHour. endMinutes} format
	public int[] workTime = new int[4];
	public ArrayList<String> tasks = new ArrayList<String>();
	public String date;
	
	public EmployeeDailyReference(int nationalID) {
		this.nationalID = nationalID;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public void setWorkTime(int[] workTime) {
		this.workTime = workTime;
	}
	public String getWorkTime() {
		return String.format("%d:%d - %d:%d", workTime[0], workTime[1], workTime[2], workTime[3])
	}
}