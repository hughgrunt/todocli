package tdpkg;

import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.time.*;

public class Render{
	private Render(){}
	public static void block(String... output){
		empty_line();
		System.out.println(String.join("\n", output));
		empty_line();
	}
	public static void empty_line(){
		System.out.println(String.format(""));
	}
	public static String strangle(String victim, int caps_length){
		if (victim.length() <= caps_length){return victim;}
		return victim.substring(0, caps_length-3)+"..."; 
		
	}
	public static String age(long from, long to){
		Instant instant_from = Instant.ofEpochMilli(from);
		Instant instant_to = Instant.ofEpochMilli(to);
		LocalDate from_localdate = instant_from 
			.atZone(ZoneId.systemDefault())
			.toLocalDate(); 
		LocalDate to_localdate = instant_to 
			.atZone(ZoneId.systemDefault())
			.toLocalDate(); 
		Duration duration = Duration.between(instant_from, instant_to);
		long minutes = duration.toMinutes();
		long hours = duration.toHours();
		long days = duration.toDays();
		long weeks = days/7;

		Period period = Period.between(from_localdate, to_localdate);	
		long months = period.getMonths();
		long years = period.getYears();

		if(hours < 1){return String.format("%s minutes", minutes);}
		else if(days < 1){return String.format("%s hours", hours);}
		else if(weeks < 1){return String.format("%s days", days);}
		else if(months < 1){return String.format("%s weeks", weeks);}
		else if(years < 1){return String.format("%s months", months);}
		else{return String.format("%s years", years);}
	}
	public static String ms_to(long ms, String f){
		return new SimpleDateFormat(f).format(new Date(ms));
	}
	public static void entrylist(ArrayList<Entry> el){
		empty_line();
		System.out.println( String.format("there are %s entries...",
					el.size()));	
		empty_line(); 
		for(Entry e:el){
			embedded_entry(e);	
		 }
	}
	public static void embedded_entry(Entry e){
		String simplified_status = e.type == ENTRYTYPE.MEMORY 
						? String.valueOf(e.type) 
						: String.valueOf(e.status);
		System.out.println(
		 String.format("#%-3s|%-6s| %-30s",
			 e.id, simplified_status,  strangle(e.title, 30))
		);

	}
	public static void entry(Entry e){
		empty_line();
		embedded_entry(e);
		empty_line();
	}
	public static void details(Entry e){
		empty_line();
		System.out.println(String.format("%4s | %6s | %6s", e.id, e.status, e.type));
		System.out.println(String.format("%s old, last edited: %s, created at: %s", age(e.timestamp, System.currentTimeMillis()),ms_to(e.last_edited, "dd.MM.yyyy HH:mm:ss"), ms_to(e.timestamp, "dd.MM.yyyy HH:mm:ss")));
		System.out.println(String.format("===== %s", e.title));
		System.out.println(String.format("%s", e.description));
		for(Task t: e.tasks){
			System.out.println(String.format("%-9s #%-2s %-5s - %s", ms_to(t.timestamp, "dd.MM.yyyy HH:mm:ss"), t.id, t.done ? "DONE" : "OPEN",t.text));
		}
		System.out.println(Strings.event_list(e.history));
		empty_line();
	}

}
