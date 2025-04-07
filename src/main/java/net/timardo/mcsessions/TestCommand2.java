package net.timardo.mcsessions;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import static net.timardo.mcsessions.MCSessions.*;

public class TestCommand2 implements ICommand {
	
	private final List<String> aliases = new ArrayList<String>();
	
	public TestCommand2() {
		this.aliases.add("teston2");
	}

	@Override
	public int compareTo(ICommand arg0) {
		return 0;
	}

	@Override
	public String getName() {
		return "testonly2";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/testonly2";
	}

	@Override
	public List<String> getAliases() {
		return this.aliases;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		proxy.handleSessionConnection(server, sender, args); //the only important line here
		
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
