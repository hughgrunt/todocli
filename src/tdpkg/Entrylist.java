package tdpkg;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Comparator;
import java.util.stream.Collectors;
import org.json.*;

public class Entrylist{
	private Data data_handler;

	HashSet<Integer> ids = new HashSet<Integer>();
	public ArrayList<Entry> entrylist = new ArrayList<Entry>();
	// indices
	public ArrayList<Entry> open_jobs = new ArrayList<Entry>();
	public ArrayList<Entry> waiting_jobs = new ArrayList<Entry>();
	public ArrayList<Entry> memorys = new ArrayList<Entry>();
	public ArrayList<Entry> recent_entrys = new ArrayList<Entry>();
	public Entrylist(Data dh){
		data_handler = dh;
		from_json(data_handler.read());
		for(Entry e:entrylist){
			ids.add(e.id);
		}
		refresh();
	}
	public void save()throws Exception{
		data_handler.write(to_json());
	}
	public int new_id(){
		int id = 1;
		while(ids.contains(id)){
			id += 1;	
		}
		return id;
	}
	public Entry get_by_id(int eid){
		for(Entry e: entrylist){
			if(e.id == eid){
				return e;
			}
		}
		return null;
	}
	public Entry get_by_id(String eid){
		try{
			int eid_int = Integer.parseInt(eid);
			return get_by_id(eid_int);
		}catch(Exception e){
			return null;
		}
	}
	public JSONArray to_json(){
		JSONArray j = new JSONArray();
		for(Entry e: entrylist){
			j.put(e.to_json().toString());
		}
		return j;
	}
	public void from_json(JSONArray j){
		for(int i=0;i<j.length();i++){
			entrylist.add(new Entry(j.getJSONObject(i)));
		}	
	}
	public int count(String what){
		switch(what){
			case "total":
				return entrylist.size();
			case "memories":
				return memorys.size();
			case "open":
				return open_jobs.size();
			case "waiting":
				return waiting_jobs.size();
			case "undone":
				return open_jobs.size()+waiting_jobs.size();
			default:
				return 0;
		}
	}
	public void render(){
		Render.entrylist(entrylist); 
	}
	public void add(Entry e){
		int new_id = new_id();
		entrylist.add(e.set_id(new_id));
		ids.add(new_id);
		refresh();
	}
	public void update(int id, String key, String value) throws Exception{
		Entry e = get_by_id(id);
		e.set(key, value);	
		refresh();
	}
	public void delete(int id) throws Exception{
		int index = -1;
		for(int i=0;i<entrylist.size();i++){
			Entry e = entrylist.get(i);
			if(e.id == id){index = i;break;}
		}
		entrylist.remove(index);
		refresh();
	}
	public void sort(){
		entrylist.sort(
    			Comparator.comparing((Entry e) -> e.type)
        		.thenComparing((Entry e) -> e.status)
        		.thenComparingLong(Entry::relevant_date)
			);
	}
	public void refresh(){
		sort();
		open_jobs.clear();
		waiting_jobs.clear();
		memorys.clear();
		for(Entry e:entrylist){
			if(e.type == ENTRYTYPE.JOB){
				if(e.status == STATUSTYPE.OPEN){
					if(e.recurrence == null){
						open_jobs.add(e);
						continue;
					}
					if(e.relevant_date()-e.recurrence.remember_ms()< System.currentTimeMillis()){
						open_jobs.add(e);

					}
				}else if(e.status == STATUSTYPE.WAITING){
					waiting_jobs.add(e);
				}
			}
			else if(e.type == ENTRYTYPE.MEMORY){
				memorys.add(e);
			}

			if(e.last_edited > e.last_edited - (Utils.days_as_ms(7))){
				recent_entrys.add(e);
			}
		} 
	}
	public ArrayList<Entry> search(Input inp){
        	return entrylist.stream()
                .filter(e -> {
			boolean no_mismatches = true;
			for(String arg:inp.arguments){
				if(!fuzzy_match(arg,e)){
					no_mismatches = false;
					break;
				}
			}
			for(String key : inp.flags.keySet()){
				if(!specific_match(key, inp.flags.get(key), e)){
					no_mismatches = false;
					break;
				}	
			}
                	return no_mismatches;
                })
                .collect(Collectors.toCollection(ArrayList::new));
	}
	public boolean fuzzy_match(String arg, Entry e){
		arg = arg.toLowerCase();
		if(e.title.toLowerCase().contains(arg)
		|| String.valueOf(e.type).toLowerCase().contains(arg)
		|| String.valueOf(e.status).toLowerCase().contains(arg)
		){
			return true;	
		}
		if(e.description != null && e.description.toLowerCase().contains(arg)){return true;}
		for(Task t:e.tasks){
			if(t.text.toLowerCase().contains(arg)){return true;}
		}
		for(Event ev:e.history){
			if(ev.text.toLowerCase().contains(arg)){return true;}
		}
		return false;
	}
	public boolean specific_match(String key, String value, Entry e){
		value = value.toLowerCase();
		switch(key){
			case "title":
				return e.title.toLowerCase().contains(value);
			case "description":
				return (e.description != null) ? e.description.toLowerCase().contains(value) : false;
			case "type":
				if(value.equals("recurring")){
					return (e.recurrence != null);
				}
				return String.valueOf(e.type).toLowerCase().contains(value);
			case "status":
				return String.valueOf(e.status).toLowerCase().contains(value);
			case "history":
				for(Event ev:e.history){
					if(ev.text.toLowerCase().contains(value)){return true;}
				}
				return false;
			case "tasks":
				for(Task t:e.tasks){
					if(t.text.toLowerCase().contains(value)){return true;}
				}
				return false;
			default:
				return false;
		}			
	}
}
