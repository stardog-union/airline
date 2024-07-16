package io.airlift.command.args;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

@Command(name = "OneArg", description = "Command with one argument")
public class OneArg {

	@Arguments(description = "The single argument")
	public String arg;

	public OneArg() {
	}
}
