package cosmeticsOG.managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.commands.Command;
import cosmeticsOG.commands.MainCommand;
import cosmeticsOG.commands.Sender;
import cosmeticsOG.commands.subcommands.BukkitHelpCommand;
import cosmeticsOG.commands.subcommands.ClearCommand;
import cosmeticsOG.commands.subcommands.CreateCommand;
import cosmeticsOG.commands.subcommands.DebugCommand;
import cosmeticsOG.commands.subcommands.DebugDeleteMenu;
import cosmeticsOG.commands.subcommands.EditCommand;
import cosmeticsOG.commands.subcommands.GroupsCommand;
import cosmeticsOG.commands.subcommands.ImportCommand;
import cosmeticsOG.commands.subcommands.MetaCommand;
import cosmeticsOG.commands.subcommands.OpenCommand;
import cosmeticsOG.commands.subcommands.ParticlesCommand;
import cosmeticsOG.commands.subcommands.ReloadCommand;
import cosmeticsOG.commands.subcommands.SetCommand;
import cosmeticsOG.commands.subcommands.SpigotHelpCommand;
import cosmeticsOG.commands.subcommands.ToggleCommand;
import cosmeticsOG.commands.subcommands.TypeCommand;
import cosmeticsOG.commands.subcommands.UnsetCommand;

public class CommandManager implements CommandExecutor, TabCompleter {

	private final CosmeticsOG core;

	private final MainCommand mainCommand;

	public CommandManager (final CosmeticsOG core, final String command)
	{
		this.core = core;

		mainCommand = new MainCommand();
		mainCommand.register(new ReloadCommand());
		mainCommand.register(new OpenCommand(core));
		mainCommand.register(new EditCommand(core));
		mainCommand.register(new CreateCommand());
		mainCommand.register(new ClearCommand());
		mainCommand.register(new SetCommand());
		mainCommand.register(new UnsetCommand());
		mainCommand.register(new ToggleCommand());
		mainCommand.register(new ParticlesCommand());
		mainCommand.register(new GroupsCommand());
		mainCommand.register(new TypeCommand());
		mainCommand.register(new ImportCommand());
		mainCommand.register(new MetaCommand(core));

		if (CosmeticsOG.debugging)
		{
			mainCommand.register(new DebugDeleteMenu());
			mainCommand.register(new DebugCommand());
		}

		if (core.canUseBungee()) {
			mainCommand.register(new SpigotHelpCommand(core, this));
		} else {
			mainCommand.register(new BukkitHelpCommand(core, this));
		}

		// Register our command executor
		core.getCommand(command).setExecutor(this);
	}

	@Override
	public List<String> onTabComplete(CommandSender commandSender, org.bukkit.command.Command cmd, String label, String[] args) 
	{
		Sender sender = new Sender(commandSender);

		List<String> arguments = mainCommand.onTabComplete(core, sender, label, new ArrayList<String>(Arrays.asList(args)));
		String currentCommand = args[args.length - 1];

		return sortCommandSuggestions(arguments, currentCommand);
	}

	@Override
	public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command cmd, String label, String[] args) {
		return mainCommand.onCommand(core, new Sender(commandSender), label, new ArrayList<String>(Arrays.asList(args)));
	}

	/**
	 * Get all registered commands
	 * @return
	 */
	public Map<String, Command> getCommands ()
	{
		LinkedHashMap<String, Command> commands = new LinkedHashMap<String, Command>();
		mainCommand.getSubCommands(commands);

		return commands;
	}

	/**
	 * Returns a list of commands matching the currentCommand
	 * @param commands List of the commands the player can execute
	 * @param currentCommand Command the player is currently typing
	 * @return
	 */
	private List<String> sortCommandSuggestions (List<String> commands, String currentCommand)
	{
		if (currentCommand.equals("")) {
			return commands;
		}

		List<String> matchingCommands = new ArrayList<String>();
		if (commands != null)
		{
			commandLoop:
				for (String s : commands)
				{
					for (int i = 0; i < s.length(); i++)
					{
						if (i < currentCommand.length())
						{
							if (s.charAt(i) != currentCommand.charAt(i)) {
								continue commandLoop;
							}
						}
					}
					matchingCommands.add(s);
				}
		}
		return matchingCommands;
	}
}
