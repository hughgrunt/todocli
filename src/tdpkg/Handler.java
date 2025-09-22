package tdpkg;

import tdpkg.*;
import java.util.HashMap;

public class Handler{
	Entrylist entrylist;
	public Handler(Entrylist elist){
		entrylist = elist;	
	}
	public void search(Input inp){
		Utils.print(Strings.list(entrylist.search(inp)));
	}
	public void add(Input inp){
		if(inp.arguments.size()<2){
			Utils.print(Strings.TOO_FEW_ARGS, Strings.definition("add", 0),"");
			return;
		}
		String type = inp.arguments.get(0);
		inp.arguments.remove(0);
		ENTRYTYPE entrytype;
		try {entrytype = ENTRYTYPE.valueOf(type.toUpperCase());}
		catch(Exception e){
			Utils.print(Strings.WRONG_ENTRYTYPE, e.getMessage(), "");  
			return;
		}
		String title = String.join(" ", inp.arguments);
		Entry entry = new Entry(entrytype).set("title", title);
		for(String key:inp.flags.keySet()){
			entry.set(key, inp.flags.get(key));
		}
		try{
			entrylist.add(entry);
			entrylist.save();
			Utils.print(Strings.entry_added(entry), "");
		}catch(Exception e){
			Utils.print("err while adding", e.getMessage());	
		}
	}
	public void show_entry(Input inp){
		String pid = inp.command;
		if(!Utils.is_number(pid)){
			Utils.print(Strings.unrecognized_id(pid), "");
			return;
		}
		int eid = Integer.parseInt(pid);
		Entry e = entrylist.get_by_id(eid);
		if(e==null){
			Utils.print(Strings.entry_not_found(pid), "");
			return; 
		}
		Utils.print("",Strings.entry_details(e),"");
	}
	public void update_entry(Input inp){
		if(inp.arguments.size() < 3){
			Utils.print(Strings.TOO_FEW_ARGS, Strings.definition("update", 0));
			return;
		}
		String pid = inp.command;
		if(!Utils.is_number(pid)){
			Utils.print(Strings.unrecognized_id(pid), "");
			return;
		}
		int eid = Integer.parseInt(pid);
		Entry e = entrylist.get_by_id(eid);
		if(e==null){
			Utils.print(Strings.entry_not_found(pid), "");
			return; 
		}
		String key = inp.arguments.get(1);
		String value = String.join(" ", inp.arguments.subList(2, inp.arguments.size()));
		try{
			e.set(key,value);
			Utils.print("entry upated..", Strings.entry_updated(e,key), "");

		}catch(Exception err){
			Utils.print("err while updating", "potentially wrong id", err.getMessage(), "");
		}

	}
	public void delete_entry(Input inp){
		if(inp.arguments.size() < 1){
			Utils.print(Strings.TOO_FEW_ARGS, Strings.definition("id", 0), "");
			return;
		}
		int eid = Integer.parseInt(inp.command);
		try{
			entrylist.delete(eid);
			entrylist.save();
			Utils.print("entry deleted", "");
		}catch(Exception e){
			Utils.print("err while updating", "potentially wrong id", e.getMessage(), "");
		}
	}
	public void add_task(Input inp){
		int eid = Integer.parseInt(inp.command);
		Entry e = entrylist.get_by_id(eid);
		String value = String.join(" ", inp.arguments.subList(1, inp.arguments.size()));
		try{
			e.add_task(value);
			entrylist.save();
			Utils.print(Strings.entry_task_added(e), "");
		}catch(Exception err){
			Utils.print("err while adding a task", err.getMessage(), "");
		}
	}
	public void add_event(Input inp){
		int eid = Integer.parseInt(inp.command);
		Entry e = entrylist.get_by_id(eid);
		String value = String.join(" ", inp.arguments.subList(1, inp.arguments.size()));
		try{
			e.add_event(value);
			entrylist.save();
			Utils.print(Strings.entry_event_added(e), "");
		}catch(Exception err){
			Utils.print("err while adding an event", err.getMessage(), "");
		}
	}
	// tasks
	public void task(Input inp){
		// id task id action
		if(inp.arguments.size() < 3){
			Utils.print(Strings.TOO_FEW_ARGS,  Strings.definition("task", 0), "");
			return;
		}	
		int eid = Integer.parseInt(inp.command);
		Entry e = entrylist.get_by_id(eid);
		if(e == null){
			Utils.print(Strings.entry_not_found(inp.command), "");
			return;
		}
		String ptid = inp.arguments.get(1);
		String act = inp.arguments.get(2);
		if(act.equals("update") && inp.arguments.size() < 4){
			Utils.print("missing value to update task", "");
			return;
		}
		if(!Utils.is_number(ptid)){
			Utils.print("invalid task id given", "");
			return;
		}
		int tid = Integer.parseInt(ptid);
		switch(act){
			case "delete":
				try{
					e.delete_task(tid);
					entrylist.save();
					Utils.print("task has been deleted", "");
				}
				catch (Exception err){
					Utils.print("error while deleting task", err.getMessage(), "");
				}
				break;
			case "update":
				try{
					e.update_task(tid, "text", String.join(" ", inp.arguments.subList(3, inp.arguments.size())));
					entrylist.save();
				Utils.print("task has been updated", Strings.entry_task_added(e), "");

				}
				catch (Exception err){
					Utils.print("err while updating task", err.getMessage(), "");
				}
				break;
			case "done":
				try{
					e.update_task(tid, "done", true);
					entrylist.save();
					Utils.print("task is done", Strings.entry_task_added(e), "");
				}
				catch (Exception err){
					Utils.print("err while finishing task", err.getMessage(), "");
				}
				break;
			case "undone":
				try{
					e.update_task(tid, "done", false);
					entrylist.save();
					Utils.print("task is open", Strings.entry_task_added(e), "");
				}
				catch (Exception err){
					Utils.print("err while opening task", err.getMessage(), "");
				}
				break;
		}
	}
	// events
	public void event(Input inp){
		//id event id action
		if(inp.arguments.size() < 3){
			Utils.print(Strings.TOO_FEW_ARGS,  Strings.definition("event", 0), "");
			return;
		}	
		int eid = Integer.parseInt(inp.command);
		Entry e = entrylist.get_by_id(eid);
		if(e == null){
			Utils.print(Strings.entry_not_found(inp.command), "");
			return;
		}
		String pevid = inp.arguments.get(1);
		String act = inp.arguments.get(2);
		if(act.equals("update") && inp.arguments.size() < 4){
			Utils.print("missing value to update event", "");
			return;
		}
		if(!Utils.is_number(pevid)){
			Utils.print("invalid event id given", "");
			return;
		}
		int evid = Integer.parseInt(pevid);
		switch(act){
			case "delete":
				try{
					e.delete_event(evid);
					entrylist.save();
					Utils.print("event has been deleted", "");
				}
				catch (Exception err){
					Utils.print("err while deleting event", err.getMessage() ,"");
				}
				break;
			case "update":
				try{
					e.update_task(evid, "text", String.join(" ", inp.arguments.subList(3, inp.arguments.size())));
					entrylist.save();
					Utils.print("event has been upated", "");
				}
				catch (Exception err){
					Utils.print("err while updating event", err.getMessage() ,"");
				}
				break;
		}
	}
	// recurrance
	public void recurrence(Input inp){
		if(inp.arguments.size() < 2){Utils.print("missing recurrence command","");return;}
		String r_com = inp.arguments.get(1);
		int eid = Integer.parseInt(inp.command);
		Entry e = entrylist.get_by_id(eid);
		if(e==null){Utils.print("no such entry unter given id: " + inp.command, "");return;}
		switch(r_com){
			case "set":
				if(!inp.flags.containsKey("type")){Utils.print("flag: type missing", ""); return;}
				if(!inp.flags.containsKey("nth")){Utils.print("flag: nth missing", ""); return;}

				int r_nth;
				RECURRENCETYPE r_type;
				Utils.print("nth", inp.flags.get("nth") + "|");
				try{
					r_type = RECURRENCETYPE.valueOf(inp.flags.get("type").trim().toUpperCase());
					r_nth = Integer.parseInt(inp.flags.get("nth").trim());
				}catch(Exception err){
						Utils.print("err while setting recurrence",err.getMessage(), "");
						return;
				}

				switch (r_type){
					case WEEKLY:
						if(!inp.flags.containsKey("weekday")){
							Utils.print("flag weekday missing", "");
							return;
						}
						if(e.set_recurrence(r_type, inp.flags.get("weekday").trim(), r_nth)){
							try{
								entrylist.save();
								Utils.print("a weekly recurrence was set", "");
							}catch(Exception err){Utils.print(Strings.FAIL_SAVE, "");}
						}else{
							Utils.print("err while setting recurrence", "");
						}
						break;
					case MONTHLY:
						if(!inp.flags.containsKey("date")){
							Utils.print("flag date missing", "");
							break;
						}
						if(e.set_recurrence(r_type, inp.flags.get("date").trim(), r_nth)){
							try{
								entrylist.save();
								Utils.print("a monthly recurrence was set", "");
							}catch(Exception err){Utils.print(Strings.FAIL_SAVE, "");}
						}else{
							Utils.print("could net set monthly recurrence", "");
						}
						break;
					default:
						Utils.print("could not find recurrence type", "");
						break;
				}
				break;
			case "done":
				if(e.recurrence == null){
					Utils.print("no recurrence set", "");
					break;
				}
				try{
					e.recurrence.done();
					entrylist.save();
					Utils.print("current recurrence set to done", "");
				}catch(Exception err){Utils.print(Strings.FAIL_SAVE, "");}
				break;
			case "undone":
				if(e.recurrence == null){
					Utils.print("no recurrence set", "");
					break;
				}
				try{
					e.recurrence.undone();
					entrylist.save();
					Utils.print("last indixed recurrence was undone", "");
				}catch(Exception err){Utils.print(Strings.FAIL_SAVE, "");}
				break;
			default:
				Utils.print("cant find recurrence command", "");
				break;
		}
	}
}
