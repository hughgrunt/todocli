package tdpkg;

import org.json.*;

public class Event{
	public int id;
	public long timestamp;
	public String text;

	public Event(int eid, String t){
		id = eid;
		timestamp = System.currentTimeMillis();
		text = t;
	}
	public Event(JSONObject j){
		id = j.getInt("id");
		timestamp = j.getLong("timestamp");
		text = j.getString("text");
	}
	public JSONObject to_json(){
		JSONObject j = new JSONObject();
		j.put("id", id);
		j.put("timestamp", System.currentTimeMillis());
		j.put("text", text);
		return j;
	}
	public void set_text(String t){
		text = t;
	}
}
