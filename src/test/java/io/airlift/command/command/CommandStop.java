package io.airlift.command.command;

import io.airlift.command.Command;
import io.airlift.command.Option;
import io.airlift.command.OptionType;

@Command(name = "stop", description = "Stop command with -v option")
public class CommandStop {

	@Option(type = OptionType.GLOBAL, name = { "-v" }, description = "Verbose mode")
	public Boolean verbose = false;

	public CommandStop() {
	}
}
