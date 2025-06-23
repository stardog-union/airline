package io.airlift.command.model;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class CommandGroupMetadata
{
    private final String name;
    private final String description;
	private final String markdownDescription;
	private final List<OptionMetadata> options;
    private final CommandMetadata defaultCommand;
    private final List<CommandMetadata> commands;

    public CommandGroupMetadata(String name, String description, String markdownDescription, Iterable<OptionMetadata> options, CommandMetadata defaultCommand, Iterable<CommandMetadata> commands)
    {
        this.name = name;
        this.description = description;
        this.markdownDescription = markdownDescription;
	    this.options = ImmutableList.copyOf(options);
        this.defaultCommand = defaultCommand;
        this.commands = Lists.newArrayList(commands);
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public String getMarkdownDescription()
    {
        return markdownDescription;
    }

    public List<OptionMetadata> getOptions()
    {
        return options;
    }

    public CommandMetadata getDefaultCommand()
    {
        return defaultCommand;
    }

    public List<CommandMetadata> getCommands()
    {
        return ImmutableList.copyOf(commands);
    }
    
    public void addCommand(CommandMetadata command)
    {
        if(!commands.contains(command))
        {
            commands.add(command);
        }
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        sb.append("CommandGroupMetadata");
        sb.append("{name='").append(name).append('\'');
        sb.append(", description='").append(description).append('\'');
        if (markdownDescription != null) {
            sb.append(", markdownDescription='").append(markdownDescription).append('\'');
        }
        sb.append(", options=").append(options);
        sb.append(", defaultCommand=").append(defaultCommand);
        sb.append(", commands=").append(commands);
        sb.append('}');
        return sb.toString();
    }

    public static Function<CommandGroupMetadata, String> nameGetter()
    {
        return CommandGroupMetadata::getName;
    }
}
