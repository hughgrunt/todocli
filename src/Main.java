import tdpkg.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Date;
import java.util.regex.*;
import java.text.SimpleDateFormat;
import java.util.stream.Collectors;
import java.io.Serializable;
import java.nio.file.*;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.*;
import org.json.*;
import java.time.*;

public class Main{
	public static void main(String[] args){
		Scanner scanner = new Scanner(System.in);
		Data data = new Data();
		boolean is_active = true;

		Entrylist el = new Entrylist(data);
		Handler handle = new Handler(el);
		
		Utils.print(Strings.welcome(el), "");
		while(is_active){
			System.out.print(">");
			String input = scanner.nextLine();

			if(input.isEmpty()){Utils.print(Strings.NO_INPUT,"");continue;}
			try{
				RECURRENCETYPE rt = RECURRENCETYPE.valueOf("WEEKLY");
			}catch(Exception err){
				Utils.print(err.getMessage(), "");
			}

			Input inp = new Input(input);
			String cmd = inp.command;
			if(Utils.is_number(cmd)){cmd="id";}

			switch (cmd){
				case "help":
					Utils.print(Strings.help(), "");
					break;
				case "add":
					handle.add(inp);
					break;
				case "show":
					Utils.print(Strings.overview(el), "");
					break;
				case "remember":
					handle.search(inp);
					break;
				case "id":
					if(inp.arguments.size() < 1){
						handle.show_entry(inp);
						continue;
					}
					String act = inp.arguments.get(0); 
					String s_cmd = "";
					if(inp.arguments.size()>1){
						s_cmd = inp.arguments.get(1);
					}
					switch (act){
					case "delete":
						handle.delete_entry(inp);
						break;
					case "update":
						handle.update_entry(inp);
						break;
					case "add":
						 if(s_cmd.equals("task")){
							handle.add_task(inp);
						} else if(s_cmd.equals("event")){
							handle.add_event(inp);
						}
						else {
							Utils.print(Strings.INVALID_TASK_ADD_ACTION, "");
						}
						break;
					case "task":
						handle.task(inp);
						break;
					case "event":
						handle.event(inp);
						break;
					case "recurrence":
						handle.recurrence(inp);
						break;
					default:
						Utils.print(Strings.NO_ENTRY_ACTION,"");
						break;
					}
					break;
				case "path":
					String new_path = "";
					if(inp.arguments.size()> 0){
						new_path = String.join(" ", inp.arguments);
					}
					if(data.set_path(new_path)){
						Utils.print("path has been set to",
								" : " +data.place.toString(),
								"");
						break;
					}
					Utils.print("path could not be set","");
					break;
				case "list":
					Utils.print(Strings.list(el), "");
					break;
				case "q":
					is_active = false;
					break;
				default:
					Utils.print(Strings.UNRECOGNIZED_INPUT, "");
					break;
			}
		}
	}
}
