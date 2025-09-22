package tdpkg;

import java.util.ArrayList;
import java.time.*;
import java.time.temporal.*;
import java.time.temporal.TemporalAdjusters;
import org.json.*;

public class Recurrence {
   	long origin_ms;
	RECURRENCETYPE type; 

	DayOfWeek weekday;
	Integer date;

	Integer nth;

	ArrayList<Long> dones = new ArrayList<Long>();

	public Recurrence(long oms) {
		origin_ms = oms;
		dones = new ArrayList<Long>();
	}
	public Recurrence(JSONObject j){
		origin_ms = j.getLong("origin_ms");
		type = RECURRENCETYPE.valueOf(j.getString("type"));
		nth = j.getInt("nth");
		if(j.has("weekday")){
			weekday = DayOfWeek.valueOf(j.getString("weekday"));
		}
		if(j.has("date")){
			date = j.getInt("date");
		}
		if(j.has("dones")){
			for(int i=0;i<j.getJSONArray("dones").length();i++){
			dones.add(j.getJSONArray("dones").getLong(i));
			}
		}
	}
	public JSONObject to_json(){
		JSONObject j = new JSONObject();
		j.put("origin_ms", origin_ms);
		j.put("type", String.valueOf(type));
		if(weekday != null){
			j.put("weekday", String.valueOf(weekday));
		}
		if(date != null){
			j.put("date", date);
		}
		j.put("nth", nth);
		j.put("dones", new JSONArray());
		for(long d : dones){
			j.getJSONArray("dones").put(d);
		}
		return j;
	}
	public Recurrence weekly(DayOfWeek d, Integer n){
		type = RECURRENCETYPE.WEEKLY;
		nth = n;
		weekday = d;
		return this;
	}
	public Recurrence monthly(Integer d, Integer n){
		type = RECURRENCETYPE.MONTHLY;
		nth = n;
		date = d;
		return this;
	}
	public long get_next(){
		int nth_multiplier = 1;
		int possible_nth = nth *1;
		long possible_ms;
		switch (type){
			case WEEKLY:
				possible_ms = get_next_weekday(weekday, possible_nth);
				while(dones.contains(possible_ms)){
					nth_multiplier += 1;
					possible_nth *= nth_multiplier;	
					possible_ms = get_next_weekday(weekday, possible_nth);
				}
				return possible_ms;
			case MONTHLY:
				possible_ms = get_next_date_of_month(date, possible_nth);
				while(dones.contains(possible_ms)){
					nth_multiplier += 1;
					possible_nth *= nth_multiplier;	
					possible_ms = get_next_date_of_month(date, possible_nth);
				}
				return possible_ms;
			default:
				return 0L;
		}
	}
	public long get_next_weekday(DayOfWeek wd, Integer n) {
		LocalDate today = LocalDate.now();
		LocalDate ref = Instant.ofEpochMilli(origin_ms).
			atZone(ZoneId.systemDefault()).toLocalDate();
        	ref = ref.with(TemporalAdjusters.nextOrSame(wd)).plusWeeks((n-1) * 1);

		while (ref.isBefore(today)) {
        	ref = ref.with(TemporalAdjusters.nextOrSame(wd)).plusWeeks((n-1) * 1);
   		}
		return ref.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}
	public long get_next_date_of_month(int date, int nth) {
		LocalDate today = LocalDate.now();
		LocalDate ref = Instant.ofEpochMilli(origin_ms).
			atZone(ZoneId.systemDefault()).toLocalDate();
		ref = ref.withDayOfMonth(date);
		while (ref.isBefore(today)) {
        		ref = ref.plusMonths(nth);
   		}
		return ref.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}
	public long remember_ms(){
		switch(type){
			case WEEKLY:
				return Utils.days_as_ms(10);
			case MONTHLY:
				return Utils.days_as_ms(14);
			default:
				return Utils.days_as_ms(15);
		}
	}
	public void done(){
		dones.add(get_next());
	}
	public void undone(){
		dones.remove(dones.size()-1);
	}
	public void remove(long target){
		dones.remove(dones.indexOf(target));
	}
	public void add(long ms){
		dones.add(ms);
	}
}

