package net.timardo.mcsessions;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class TestCommand implements ICommand {
	
	private final List<String> aliases = new ArrayList<String>();
	
	public TestCommand() {
		this.aliases.add("teston");
	}

	@Override
	public int compareTo(ICommand arg0) {
		return 0;
	}

	@Override
	public String getName() {
		return "testonly";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/testonly";
	}

	@Override
	public List<String> getAliases() {
		return this.aliases;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		MCSessions.proxy.handleSessionCreation(server, sender, args); //the only important line here
		
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,	BlockPos targetPos) {
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		return false;
	}


}
