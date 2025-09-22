package tdpkg;

import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.text.SimpleDateFormat;
import java.time.*;

public class Utils{
	private Utils(){}
	public static void print(String... str){
		for(String s: str){
			System.out.println(s);
		}
	}
	public static ArrayList split(String split_me){
		String split_regex = "(\"[^\"]*\"|\\S+)";
		Pattern pattern = Pattern.compile(split_regex);
        	Matcher matcher = pattern.matcher(split_me);
        	ArrayList<String> al = new ArrayList<>();
        	while (matcher.find()) {
            		String match = matcher.group();
            		if (match.startsWith("\"") && match.endsWith("\"")) {
                		al.add(match.substring(1, match.length() - 1));
            		} else {
                		al.add(match);
            		}
        	}
		return al;
	}
	public static boolean is_number(String s){
		try{
			Integer.parseInt(s);
			return true;
		}
		catch(Exception e){
			return false;
		}
	}
	// string utils 
	public static String build_lines(String... str){
		ArrayList<String> lines = new ArrayList<>();
		for(String line:str){
			if(line.isEmpty()){continue;}
			lines.add(line);
		}
		return String.join("\n", lines);
	}
	public static String strangle(String victim, int caps_length){
		if(victim == null){return "null";}
		if(victim.length()<=0){return victim;}
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
	public static String ms_dt(long ms){
		return ms_to(ms, "dd.MM.yyyy HH:mm:ss");
	}
	public static String ms_d(long ms){
		return ms_to(ms, "dd.MM.yyyy");
	}
	public static long date_to(String date, String f)throws Exception{
		return new SimpleDateFormat(f).parse(date).getTime();
	}
	public static long days_as_ms(int amount){
		return amount*1000*60*60*24;
	}
}
