package tdpkg;

import java.util.ArrayList;
import java.util.List;
import org.json.*;
public class Strings{
	private Strings(){}
	public static String NL = "\n";
	public static String TOO_FEW_ARGS = "too few arguments";
	public static String NO_INPUT = "no input";
	public static String UNRECOGNIZED_INPUT = "unrecognized input";

	public static String INVALID_TASK_ADD_ACTION = "you can only add an event or a task to an entry";
	public static String NO_ENTRY_ACTION = "no action command for entry recognized";
	public static String WRONG_ENTRYTYPE = "wrong entrytpe, use job/memory";
	public static String WRONG_STATUSTYPE = "wrong statustype, use open/waiting/closed";

	public static String FAIL_SAVE = "list could not be saved, changes will get lost, you can try save manually by typing save";


	public static String definition(String cas, int idx){
		switch(cas){
			case "help":
				return new String[]{"help",
					"print out valid commands"}[idx];
			case "add":
				return new String[]{"add [job|memory] title text [--prop --value]",
					"adds a new entry based upn given values, title is mandatory"}[idx];
			case "show":
				return new String[]{"show",
					"shows classic overview"}[idx];
			case "remember":
				return new String[]{"remember search values [--prop --value]",
					"search functionality"}[idx];
			case "id":
				return new String[]{"[id] [delete]",
					"shows details of given entry id, or gives option do deletion"
				}[idx];
			case "add_sub":
				return new String[]{"[id] add [task|event] text value",
					"adds a task or entry to the given entry id"
				}[idx];

			case "task":
				return new String[]{"[id] task [taskid] [done|undone|delete|update] [update value]",
					"sets done or undone, deletes or updates the task with given value"
				}[idx];
			case "event":
				return new String[]{"[id] event [eventid] [delete|update] [update value]",
					"deletes or updates the event with given value"
				}[idx];
			case "recurrence":
				return new String[]{"[id] recurrence set --type [WEEKLY|MONTHLY] --nth [int] --weekday [weekday] --date [int]",
					"weekday flag only for weekly, dateflag only for monthly, nth stands for the nth time... so every week pass 1"
				}[idx];
			case "q":
				return new String[]{"q",
					"quits the cli tool"}[idx];
			default:
				return new String[]{"[definition not found]",
					"[definition not found]"}[idx];
		}
	}
	public static String help(){
		String lf = "%-30s - %-30s";
		return Utils.build_lines(
			String.format(lf, definition("help", 0), definition("help",1)),
			String.format(lf, definition("add", 0), definition("add",1)),
			String.format(lf, definition("show", 0), definition("show",1)),
			String.format(lf, definition("remember", 0), definition("remember",1)),
			String.format(lf, definition("id", 0), definition("id",1)),
			String.format(lf, definition("add_sub", 0), definition("add_sub",1)),
			String.format(lf, definition("task", 0), definition("task",1)),
			String.format(lf, definition("event", 0), definition("event",1)),
			String.format(lf, definition("recurrence", 0), definition("recurrence",1)),
			String.format(lf, definition("q", 0), definition("q",1))
				);

	}
	public static String welcome(Entrylist el){
		ArrayList<String> lines = new ArrayList<>();
		lines.add("");
		lines.add("====================");
		lines.add(String.format("=       TODO       ="));
		lines.add("====================");
		lines.add(" good morning,");
		lines.add("  ...grab some coffee");
		lines.add("====================");
		lines.add(String.format(" => %s MEMORIES", el.count("memories")));
		lines.add(String.format(" => %s UNDONE TASKS", el.count("undone")));
		lines.add(String.format("       + %s REQUIRE YOUR ACTION", el.count("open")));
		lines.add(String.format("       + %s WAITING FOR SOMEONE ELSE", el.count("waiting")));
		lines.add("====================");
		lines.add("");
		return String.join("\n", lines);
	}
	public static String unrecognized_id(String sid){
		return String.format("unrecognized id: %s", sid);
	}
	public static String entry_not_found(String id){
		return String.format("no such entry found: %s", id);
	}	
	// regarding entry
	public static String entry_added(Entry e){
		return Utils.build_lines(
			" entry added ...",
			entry_addedview(e)
		);
	}
	public static String entry_updated(Entry e, String key){
		return Utils.build_lines(
			" entry updated...",
			String.format("#%s %s %s %s", e.id, e.title, key, e.get(key))
		);
	}
	public static String entry_task_added(Entry e){
		return Utils.build_lines(
			" task has been added...",
			String.format("#%s %s", e.id, e.title),
			task_list(e.tasks)
		);

	}
	public static String entry_event_added(Entry e){
		return Utils.build_lines(
			" event has been added...",
			String.format("#%s %s", e.id, e.title),
			event_list(e.history)
		);

	}
	public static String entry_addedview(Entry e){
		return String.format("#%s %s %s %s %s", e.id, e.type, e.status, e.title, Utils.strangle(e.description, 30));
	}
	public static String list(Entrylist el){
		ArrayList<String> lines = new ArrayList<>();
		lines.add("====================");
		lines.add(entries_overview(el.entrylist));
		lines.add("====================");
		lines.add(String.format(" => %s total", el.entrylist.size()));
		return String.join("\n", lines);
	}
	public static String list(ArrayList<Entry> el){
		ArrayList<String> lines = new ArrayList<>();
		lines.add("====================");
		lines.add(entries_overview(el));
		lines.add("====================");
		lines.add(String.format(" => %s total", el.size()));
		return String.join("\n", lines);
	}
	public static String overview(Entrylist el){
		int open_jobs_count = el.open_jobs.size();
		int waiting_jobs_count = el.waiting_jobs.size();
		int undone_jobs_count = open_jobs_count + waiting_jobs_count;
		ArrayList<String> lines = new ArrayList<>();
		
		lines.add("====================");
		if(open_jobs_count > 0){lines.add(entries_overview(el.open_jobs));}
		if(waiting_jobs_count>0){lines.add(entries_overview(el.waiting_jobs));}
		lines.add("====================");
		lines.add(String.format(" + %s open", open_jobs_count));
		lines.add(String.format(" + %s waiting", waiting_jobs_count));
		lines.add("====================");
		lines.add(String.format(" => %s total", undone_jobs_count));
		return String.join("\n", lines);
	}
	public static String entries_overview(ArrayList<Entry> es){
		ArrayList<String> lines = new ArrayList<>();
		for(Entry e: es){
			lines.add(entry_overview(e));
		}
		return String.join("\n", lines);
	}
	public static String entry_overview(Entry e){
		String type = e.type == ENTRYTYPE.JOB ? String.valueOf(e.status) : String.valueOf(e.type);
		String hist = "000"+String.valueOf(e.history.size());
		String relevance_date = e.relevant_date_label()+Utils.ms_d(e.relevant_date());
		return String.format("%s #%-3s %9s %s/%s [%2s] %-40s",
				relevance_date,
				e.id, e.relevant_status(), e.count_done_tasks(), e.tasks.size(),
				hist.substring(hist.length()-3),
				Utils.strangle(e.title, 40) 
		);
	}
	public static String entry_details(Entry e){
		e.sort_tasks();
		String type = e.type == ENTRYTYPE.JOB ? String.valueOf(e.status) : String.valueOf(e.type);
		String hist = "000"+String.valueOf(e.history.size());
		String relevance_date = e.relevant_date_label()+Utils.ms_d(e.relevant_date());
		return Utils.build_lines(
			String.format(" %s old, last edited: %s, created at: %s", 
				Utils.age(e.timestamp, System.currentTimeMillis()), Utils.ms_d(e.last_edited), Utils.ms_d(e.timestamp)),
			"====================",
			String.format(" #%3s %s", e.id, e.title.toUpperCase()),
			"====================",
			String.format(" ... %s", (e.description == null) ? "no description" : e.description),
			String.format(" ==> %s/%s tasks done",e.count_done_tasks(), e.tasks.size()),
			task_list(e.tasks),
			String.format(" ==> %s events", e.history.size()),
			event_list(e.history),
			"===================="
			
		);
	}
	public static String event_list(ArrayList<Event> list){
		ArrayList<String> lines = new ArrayList<>();
		for (Event ev: list){
			lines.add(event_details(ev));
		}
		return lines.size() <= 0 ? "" : String.join("\n", lines);
	}
	public static String event_details(Event ev){
		return String.format(
			"%s #%-3s %s", Utils.ms_d(ev.timestamp), ev.id, ev.text);
	}public static String task_list(ArrayList<Task> list){
		ArrayList<String> lines = new ArrayList<>();
		for (Task t: list){
			lines.add(task_details(t));
		}
		return lines.size() <= 0 ? "" : String.join("\n", lines);
	}
	public static String task_details(Task t){
		return String.format(
			"%s #%-3s %-5s %s", Utils.ms_d(t.timestamp), t.id, t.done?"DONE":"OPEN", t.text);
	}
}
