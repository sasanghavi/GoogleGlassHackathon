package glass.remindme;

public class Reminder {

	public int id;
	public String descr;
	public String time;
	public String path ="";
	
	public Reminder(int id,String description,String time){
		this.id=id;
		this.descr=description;
		this.time=time;
	}
	
	public int getId(){
		return id;
	}
	
	public String getDescr(){
		return descr;
	}
	
	public String getTime(){
		return time;
	}
	
	public String getPath(){
		return path;
	}
}
