package tdpkg;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.json.*;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;


public class Data{
	Path path_config = Paths.get("config.json");
	public Path place = Paths.get("");
	String filename = "todo.json";
	public Data(){
	}
	public JSONObject read_config(){
		if(!Files.exists(path_config)){
			return new JSONObject();
		}
		try{
			List<String> lines = Files.readAllLines(path_config, StandardCharsets.UTF_8);
			return new JSONObject(String.join("", lines));
		}catch(Exception e){
			return new JSONObject();
		}
	}
	public boolean set_path(String path){
		if(Files.exists(Paths.get(path)) && Files.isDirectory(Paths.get(path))){
			place = Paths.get(path);
			return true;
		}
		return false;
	}
	public void write(JSONArray jarr) throws Exception{
		ArrayList<String> lines = new ArrayList<String>();
		for(int i=0;i<jarr.length();i++){
			lines.add(jarr.getString(i));
        		Files.write(Paths.get(place.toString(), filename), lines, StandardCharsets.UTF_8);	
		}
	}
	public JSONArray read(){
		try{
			List<String> lines = Files.readAllLines(Paths.get(place.toString(), filename), StandardCharsets.UTF_8);
			JSONArray ja = new JSONArray();
			for(String line : lines){
				ja.put(new JSONObject(line));
			}
			return ja;
		}catch(Exception e){
			System.out.println(e.getMessage());
			return new JSONArray();
		}

	}
	public JSONObject read_views(){
		try{
			List<String> lines = Files.readAllLines(Paths.get(place.toString(), "views.json"), StandardCharsets.UTF_8);
			return new JSONObject(String.join("\n",lines));
		}catch (Exception e){
			System.out.println(e.getMessage());
			return new JSONObject();
		}
	}

}
