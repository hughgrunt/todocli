package tdpkg;

import java.util.ArrayList;
import java.util.HashSet;
import org.json.*;
import java.time.*;

public class Entry{
	int id;

	public ENTRYTYPE type;
	public STATUSTYPE status;

	public long timestamp;
	public long last_edited;

	public String title; 
	public String description;

	public long deadline;
	public Recurrence recurrence;

	// how to set it
	// 1 recurrint rhytm
	// 1 recurring daily
	// 1 recurring weekdaily 
	// 1 recurring weekly
	// 1 recurring monthly
	// 1 recurring every weekday
	// 1 recurring every 1 every 2
	// 1 recurring everymonth
	//
	// 1 recurring done
	// 1 recurring undone
	//
	// that means i will have a public reccurring_dones

	public ArrayList<Task> tasks = new ArrayList<Task>();
	public ArrayList<Event> history = new ArrayList<Event>();
	
	public Entry(ENTRYTYPE etype){
		timestamp = System.currentTimeMillis();
		type = etype;
		status = (type == ENTRYTYPE.MEMORY) ? STATUSTYPE.CLOSED :STATUSTYPE.OPEN;
		updated();
	}
	public Entry(JSONObject j){
		id = j.getInt("id");
		type = ENTRYTYPE.valueOf(j.getString("type"));
		status = STATUSTYPE.valueOf(j.getString("status"));
		timestamp = j.getLong("timestamp");
		last_edited = j.getLong("last_edited");
		title = j.getString("title");
		if(j.has("description")){
			description = j.getString("description");
		}
		if(j.has("tasks")){
			JSONArray j_tasks = j.getJSONArray("tasks");
			for(int i=0;i<j_tasks.length();i++){
				tasks.add(new Task(j_tasks.getJSONObject(i)));
			}
		}
		if(j.has("history")){
			JSONArray j_history = j.getJSONArray("history");
			for(int i=0;i<j_history.length();i++){
				history.add(new Event(j_history.getJSONObject(i)));
			}
		}
		if(j.has("recurrence")){
			recurrence = new Recurrence(j.getJSONObject("recurrence"));
		}
	}
	public JSONObject to_json(){
		JSONObject j = new JSONObject();
		j.put("id", id);
		j.put("type", String.valueOf(type));
		j.put("status", String.valueOf(status));
		j.put("timestamp", timestamp);
		j.put("last_edited", last_edited);
		j.put("title", title);
		if(description != null){
			j.put("description", description);
		}
		if(deadline > 0){
			j.put("deadline", deadline);
		}
		if(recurrence != null){
			j.put("recurrence", recurrence.to_json());
		}
		for(Task t: tasks){
			if(!j.has("tasks")){j.put("tasks", new JSONArray());}
			j.getJSONArray("tasks").put(t.to_json());
		}
		for(Event e:history){
			if(!j.has("history")){j.put("history", new JSONArray());}
			j.getJSONArray("history").put(e.to_json());
		}
		return j;
	}
	public void updated(){
		last_edited = System.currentTimeMillis();
	}
	// getters
	public Integer count_done_tasks(){
		int d = 0;
		for(Task t:tasks){if(t.done){d+=1;}}
		return d;
	}
	public Integer count_open_tasks(){
		int o = 0;
		for(Task t:tasks){if(!t.done){o+=1;}}
		return o;
	}
	public long relevant_date(){
		if(deadline > 0){
			return deadline;
		}
		else if(recurrence != null){
			return recurrence.get_next();
		}
		else{
			return last_edited;
		}
	}
	public String relevant_date_label(){
		if(recurrence != null){
			return "R";
		}else if(deadline >0){
			return "!";
		} else{
			return " ";
		}
	}
	public String relevant_status(){
		if(recurrence != null){
			return "RECURRING";
		} else if(type == ENTRYTYPE.JOB){
			return String.valueOf(status);
		} else{
			return String.valueOf(type);
		}
	}
	// getters
	public String get(String key){
		String val = "";
		switch(key){
			case "type":
				val = String.valueOf(type);
				break;
			case "status":
				val = String.valueOf(status);
				break;
			case "title":
				val = title;
				break;
			case "description":
				val = description;
				break;
			case "deadline":
				val = String.valueOf(deadline);
				break;
			case "recurrance":
				val = recurrence != null ? "true" : "false";
			default:
				val = "ERR: KEY NOT FOUND";
				break;
		}
		return val;
	}
	// setters
	public Entry set_id(Integer i){
		id = i;
		updated();
		return this;
	}
	public Entry set(String key, Object value){
		boolean any_update = true;
		switch(key){
			case "type":
				String str_type = (String)value;
				type = ENTRYTYPE.valueOf(str_type.toUpperCase());
				status = (type == ENTRYTYPE.MEMORY) ? status = STATUSTYPE.CLOSED : STATUSTYPE.OPEN;
				break;
			case "status":
				String str_status = (String)value;
				status = STATUSTYPE.valueOf(str_status.toUpperCase());
				break;
			case "title":
				title = (String)value;
				break;
			case "description":
				description = (String)value;
				break;
			case "deadline":
				try{deadline = Utils.date_to((String)value, "dd.MM.yyyy" );}catch(Exception e){System.out.println(e.getMessage());}
				System.out.println(String.format("set deadline %s",deadline));
				break;
			case "recurrance":
				recurrence = (Recurrence)value;
			default:
				any_update = false;
				break;
		}
		if(any_update){updated();}
		return this;
	}
	public void render(){
		System.out.println(String.format("%-5s, %-6s, %-30s, ",id, type, title));
	}
	// tasks
	public void sort_tasks(){
		tasks.sort((a,b)->Boolean.compare(b.done, a.done));	
	}
	public int get_unique_task_id(){
		HashSet<Integer> tids = new HashSet<Integer>();
		int tid = 1;
		for(Task t: tasks){
			if(!tids.contains(t.id)){
				tids.add(t.id);
			}
		}
		while(tids.contains(tid)){
			tid += 1;
		}
		return tid;
	}
	public Task get_task(int tid){
		for(Task t: tasks){
			if(t.id == tid){return t;}
		}	
		return null;
	}
	public void add_task(String text) throws Exception{
		tasks.add(new Task(get_unique_task_id(), text));
	}
	public void delete_task(int tid) throws Exception{
		int del_idx = -1;
		for(int i=0;i<tasks.size();i++){
			Task t = tasks.get(i);	
			if(t.id == tid){
				del_idx = i;
				break;
			}
		}
		tasks.remove(del_idx);
	}
	public void update_task(int tid,String key, Object value) throws Exception{
		switch(key){
			case "text":
				get_task(tid).set_text((String)value);
				break;
			case "done":
				get_task(tid).set_done((boolean)value);
				break;
			default:
				break;
		}
	}
	// events 
	public int get_unique_event_id(){
		HashSet<Integer> eids = new HashSet<Integer>();
		int eid = 1;
		for(Event e: history){
			if(!eids.contains(e.id)){
				eids.add(e.id);
			}
		}
		while(eids.contains(eid)){
			eid += 1;
		}
		return eid;
	}
	public Event get_event(int eid){
		for(Event e: history){
			if(e.id == eid){return e;}
		}	
		return null;
	}
	public void add_event(String text) throws Exception{
		history.add(new Event(get_unique_event_id(), text));
	}
	public void delete_event(int eid) throws Exception{
		int del_idx = -1;
		for(int i=0;i<history.size();i++){
			Event e = history.get(i);	
			if(e.id == eid){
				del_idx = i;
				break;
			}
		}
		history.remove(del_idx);
	}
	public void update_event(int eid,String text) throws Exception{
		get_event(eid).set_text(text);
	}
	// recurrance
	public boolean set_recurrence(RECURRENCETYPE type, String type_info, Integer nth){
		switch (type){
			case WEEKLY:
				DayOfWeek dow;
				try{
					dow = DayOfWeek.valueOf(type_info.toUpperCase());
				} catch (Exception err){
					Utils.print(err.getMessage(), "");
					return false;
				}
				recurrence = new Recurrence(timestamp).weekly(dow, nth);
				return true;
			case MONTHLY:
				Integer m_date;
				try{
					m_date = Integer.parseInt(type_info);
				}catch(Exception err){
					Utils.print(err.getMessage(), "");
					return false;
				}
				recurrence = new Recurrence(timestamp).monthly(m_date, nth);
				return true;
			default:
				return false;

	
		}
	}
}
