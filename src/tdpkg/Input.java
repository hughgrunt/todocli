package tdpkg;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Input{
	public String command = "";
	public ArrayList<String> arguments = new ArrayList<String>();
	public HashMap<String, String> flags = new HashMap();

	public Input(String str){
		String[] first_split = str.split(" ", 2);
		command = first_split[0];
		if(first_split.length<2){return;}

		String args_and_flags = first_split[1];


		arguments = Utils.split(args_and_flags);
		int startOfFlags = args_and_flags.indexOf("--");
		if(startOfFlags < 0){return;}

		ArrayList<String> arguments_without_flags = new ArrayList<String>();
		for(String arg:arguments){
			if(arg.startsWith("--")){break;}
			arguments_without_flags.add(arg);
		}
		arguments = arguments_without_flags;

		String flag_block_str = args_and_flags.substring(startOfFlags, args_and_flags.length());
		for(String flag_block : flag_block_str.split("--")){
			if(flag_block.isEmpty()){continue;}
			int first_blank_idx = flag_block.indexOf(" ");
			String[] flag_args = flag_block.split(" ", 2);
			if (flag_args.length < 2){continue;}
			if(flag_args[0].isEmpty() || flag_args[1].isEmpty()){continue;}
			flags.put(flag_args[0], flag_args[1]);
		}
	}
	public void debug(){
		System.out.println(String.format("command: %s", command));
		System.out.println(String.format("arguments: %s", arguments));
		System.out.println(String.format("flags: %s", flags));
	}

}
