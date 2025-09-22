package tdpkg;

import org.json.*;

public class Task{
	public int id;
	public long timestamp;
	public String text;
	public boolean done;

	public Task(int tid, String t){
		id = tid;
		timestamp = System.currentTimeMillis();
		text = t;
		done = false;
	}
	public Task(JSONObject j){
		id = j.getInt("id");
		timestamp = j.getLong("timestamp");
		text = j.getString("text");
		done = j.getBoolean("done");
	}
	public String get_text(){
		return text;
	}
	public void set_text(String t){
		text = t;
	}
	public void set_done(boolean d){
		done = d;
	}
	public JSONObject to_json(){
		JSONObject j = new JSONObject();
		j.put("id", id);
		j.put("timestamp", timestamp);
		j.put("text", text);
		j.put("done", done);
		return j;
	}
}
