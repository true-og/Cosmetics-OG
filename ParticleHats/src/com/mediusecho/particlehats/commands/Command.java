package com.mediusecho.particlehats.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.locale.Message;

public abstract class Command {
	
	/**
	 * Determine if we can include this command in the help menu
	 */
	protected boolean visible = true;
	
	/**
	 * Keep track of which sub commands belong to this command
	 */
	protected Map<String, Command> subCommands;
	
	public Command ()
	{		
		subCommands = new HashMap<String, Command>();
	}
	
	/**
	 * Generic command execute method
	 * @param plugin
	 * @param sender
	 * @param label
	 * @param args
	 * @return
	 */
	public abstract boolean execute (Core core, Sender sender, String label, ArrayList<String> args);
	
	/**
	 * Generic tab complete method
	 * @param plugin
	 * @param sender
	 * @param label
	 * @param args
	 * @return
	 */
	public List<String> tabCompelete (Core core, Sender sender, String label, ArrayList<String> args)
	{
		if (args.size() == 1)
		{
			List<String> arguments = new ArrayList<String>();
			for (Entry<String, Command> entry : subCommands.entrySet())
			{
				if (sender.hasPermission(entry.getValue().getPermission())) {
					arguments.add(entry.getKey());
				}
			}
			return arguments;
		}
		
		else
		{
			Command subCommand = subCommands.get(args.get(0));
			if (subCommand != null)
			{
				args.remove(0);
				return subCommand.tabCompelete(core, sender, label, args);
			}
		}
		return Arrays.asList("");
	}
	
	/**
	 * Return this commands name
	 * @return
	 */
	public abstract String getName ();
	
	/**
	 * Returns this commands arguments
	 * @return
	 */
	public abstract Message getUsage ();
	
	/**
	 * Returns a brief description of what this command does
	 * @return
	 */
	public abstract Message getDescription ();
	
	/**
	 * Returns this commands permission
	 * @return
	 */
	public abstract CommandPermission getPermission ();
	
	/**
	 * Registers a sub-command under this command
	 * @param command
	 */
	public void register (Command command) {
		subCommands.put(command.getName(), command);
	}
	
	/**
	 * Returns a map of all sub-commands registered under this command
	 * @return
	 */
	public Map<String, Command> getSubCommands ()
	{
		final Map<String, Command> commands = new LinkedHashMap<String, Command>(subCommands);
		return commands;
	}
	
	/**
	 * Recursively adds all sub-commands under this command to the Map
	 * @param commands
	 */
	public void getSubCommands (LinkedHashMap<String, Command> commands)
	{
		for (Entry<String, Command> entry : subCommands.entrySet())
		{
			Command cmd = entry.getValue();
			commands.put(cmd.getName(), cmd);
			cmd.getSubCommands(commands);
		}
	}
	
}