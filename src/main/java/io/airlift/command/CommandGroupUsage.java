package io.airlift.command;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import io.airlift.command.model.CommandGroupMetadata;
import io.airlift.command.model.CommandMetadata;
import io.airlift.command.model.GlobalMetadata;
import io.airlift.command.model.OptionMetadata;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newTreeMap;
import static io.airlift.command.UsageHelper.DEFAULT_COMMAND_COMPARATOR;
import static io.airlift.command.UsageHelper.DEFAULT_OPTION_COMPARATOR;

public class CommandGroupUsage
{
    private final int columnSize;
    private final boolean hideGlobalOptions;
    private final Comparator<? super OptionMetadata> optionComparator;
    private final Comparator<? super CommandMetadata> commandComparator = DEFAULT_COMMAND_COMPARATOR;

    public CommandGroupUsage()
    {
        this(79, false, DEFAULT_OPTION_COMPARATOR);
    }

    public CommandGroupUsage(int columnSize)
    {
        this(columnSize, false, DEFAULT_OPTION_COMPARATOR);
    }

    public CommandGroupUsage(int columnSize, boolean hideGlobalOptions)
    {
        this(columnSize, hideGlobalOptions, DEFAULT_OPTION_COMPARATOR);
    }

    public CommandGroupUsage(int columnSize, boolean hideGlobalOptions, @Nullable Comparator<? super OptionMetadata> optionComparator)
    {
        Preconditions.checkArgument(columnSize > 0, "columnSize must be greater than 0");
        this.columnSize = columnSize;
        this.hideGlobalOptions = hideGlobalOptions;
        this.optionComparator = optionComparator;
    }

    /**
     * Display the help on System.out.
     */
    public void usage(@Nullable GlobalMetadata global, CommandGroupMetadata group)
    {
        StringBuilder stringBuilder = new StringBuilder();
        usage(global, group, stringBuilder);
        System.out.println(stringBuilder);
    }

    /**
     * Store the help in the passed string builder.
     */
    public void usage(@Nullable GlobalMetadata global, CommandGroupMetadata group, StringBuilder out)
    {
        usage(global, group, new UsagePrinter(out, columnSize));
    }

    public void usage(@Nullable GlobalMetadata global, CommandGroupMetadata group, UsagePrinter out)
    {
        //
        // NAME
        //
        out.append("NAME").newline();

        out.newIndentedPrinter(8)
                .append(global.getName())
                .append(group.getName())
                .append("-")
                .append(group.getDescription())
                .newline()
                .newline();

        //
        // SYNOPSIS
        //
        out.append("SYNOPSIS").newline();
        UsagePrinter synopsis = out.newIndentedPrinter(8).newPrinterWithHangingIndent(8);

        List<CommandMetadata> commands = newArrayList(group.getCommands());
        Collections.sort(commands, commandComparator);

        // Populate group info via an extra for loop through commands
        String defaultCommand = "";
        if (group.getDefaultCommand() != null) {
            defaultCommand = group.getDefaultCommand().getName();
        }
        List<OptionMetadata> commonGroupOptions = null;
        String commonGroupArgs = null;
        List<String> allCommandNames = newArrayList();
        boolean hasCommandSpecificOptions = false, hasCommandSpecificArgs = false;
        for (CommandMetadata command : commands) {
            if (command.getName().equals(defaultCommand)) {
                allCommandNames.add(command.getName() + "*");
            }
            else {
                allCommandNames.add(command.getName());
            }
            if (commonGroupOptions == null) {
                commonGroupOptions = newArrayList(command.getCommandOptions());
            }
            if (commonGroupArgs == null) {
                commonGroupArgs = (command.getArguments() != null ? UsageHelper.toUsage(command.getArguments()) : "");
            }

            commonGroupOptions.retainAll(command.getCommandOptions());
            if (command.getCommandOptions().size() > commonGroupOptions.size()) {
                hasCommandSpecificOptions = true;
            }
            if (commonGroupArgs != (command.getArguments() != null ? UsageHelper.toUsage(command.getArguments()) : "")) {
                hasCommandSpecificArgs = true;
            }
        }
        // Print group summary line
        if (global != null) {
            synopsis.append(global.getName());
            if (!hideGlobalOptions) {
                synopsis.appendWords(UsageHelper.toSynopsisUsage(commands.get(0).getGlobalOptions()));
            }
        }
        synopsis.append(group.getName()).appendWords(UsageHelper.toSynopsisUsage(commands.get(0).getGroupOptions()));
        synopsis.append(" {").append(allCommandNames.get(0));
        for (int i = 1; i < allCommandNames.size(); i++) {
            synopsis.append(" | ").append(allCommandNames.get(i));
        }
        synopsis.append("} [--]");
        if (!commonGroupOptions.isEmpty()) {
            synopsis.appendWords(UsageHelper.toSynopsisUsage(commonGroupOptions));
        }
        if (hasCommandSpecificOptions) {
            synopsis.append(" [cmd-options]");
        }
        if (hasCommandSpecificArgs) {
            synopsis.append(" <cmd-args>");
        }
        synopsis.newline();
        Map<String, String> cmdOptions = newTreeMap();
        Map<String, String> cmdArguments = newTreeMap();

        for (CommandMetadata command : commands) {

            if(!command.isHidden())
            {
                if (hasCommandSpecificOptions) {
                    List<OptionMetadata> thisCmdOptions = newArrayList(command.getCommandOptions());
                    thisCmdOptions.removeAll(commonGroupOptions);
                    StringBuilder optSB = new StringBuilder();
                    for (String s : UsageHelper.toSynopsisUsage(thisCmdOptions)) {
                        optSB.append(s).append(" ");
                    }
                    cmdOptions.put(command.getName(), optSB.toString());
                }
                if (hasCommandSpecificArgs) {
                    cmdArguments.put(command.getName(), (command.getArguments() != null ? UsageHelper.toUsage(command.getArguments()) : ""));
                }
            }
        }
        if (hasCommandSpecificOptions) {
            synopsis.newline().append("Where command-specific options [cmd-options] are:").newline();
            UsagePrinter opts = synopsis.newIndentedPrinter(4);
            for (String cmd : cmdOptions.keySet()) {
                opts.append(cmd + ": " + cmdOptions.get(cmd)).newline();
            }
        }
        if (hasCommandSpecificArgs) {
            synopsis.newline().append("Where command-specific arguments <cmd-args> are:").newline();
            UsagePrinter args = synopsis.newIndentedPrinter(4);
            for (String arg : cmdArguments.keySet()) {
                args.append(arg + ": " + cmdArguments.get(arg)).newline();
            }
        }
        if (!Objects.equals(defaultCommand, "")) {
            synopsis.newline().append(String.format("* %s is the default command", defaultCommand));
        }
        synopsis.newline().append("See").append("'" + global.getName()).append("help ").append(group.getName()).appendOnOneLine(" <command>' for more information on a specific command.").newline();

        //
        // OPTIONS
        //
        List<OptionMetadata> options = newArrayList();
        options.addAll(group.getOptions());
        if (global != null && !hideGlobalOptions) {
            options.addAll(global.getOptions());
        }
        if (!options.isEmpty()) {
            if (optionComparator != null) {
                options.sort(optionComparator);
            }

            out.append("OPTIONS").newline();

            for (OptionMetadata option : options) {
                
                if(option.isHidden())
                {
                    continue;
                }

                // option names
                UsagePrinter optionPrinter = out.newIndentedPrinter(8);
                optionPrinter.append(UsageHelper.toDescription(option)).newline();

                // description
                UsagePrinter descriptionPrinter = optionPrinter.newIndentedPrinter(4);
                descriptionPrinter.append(option.getDescription()).newline();

                descriptionPrinter.newline();
            }
        }
    }

    private static String longest(Iterable<String> iterable)
    {
        String longest = "";
        for (String value : iterable) {
            if (value.length() > longest.length()) {
                longest = value;
            }
        }
        return longest;
    }

    public String usageMD(GlobalMetadata global, CommandGroupMetadata group) {
        List<CommandMetadata> commands = ImmutableList.sortedCopyOf(Comparator.comparing(CommandMetadata::getName),
                                                                    group.getCommands());

        StringBuilder builder = new StringBuilder();

        // for jekyll to pick up these pages on the website
        builder.append("---\n");
        builder.append("layout: default\n");
        builder.append("title: ").append(group.getName()).append("\n");

        if (global.getName().equals("stardog")){
            builder.append("parent: Stardog CLI Reference\n");
        }
        else {
            builder.append("parent: Stardog Admin CLI Reference\n");
        }
        builder.append("has_children: ").append(!commands.isEmpty()).append("\n");
        builder.append("has_toc: false\n");
        builder.append("description: This page contains the commands available in the ")
               .append(global.getName())
               .append(" ")
               .append(group.getName())
               .append(" command group.\n");
        builder.append("---\n\n");
        builder.append("# `").append(group.getName()).append("`\n\n");
	    builder.append(!Strings.isNullOrEmpty(group.getMarkdownDescription())
	                   ? group.getMarkdownDescription()
	                   : group.getDescription());
        builder.append("\n");
        if (!commands.isEmpty()) {
            builder.append("\n\n");
            builder.append("Select any of the commands to view their manual page.\n\n");
            builder.append("| Command | Description |\n");
            builder.append("|---------|-------------|\n");
            for (CommandMetadata command: commands) {
                builder.append("| [`")
                       .append(group.getName())
                       .append(" ")
                       .append(command.getName())
                       .append("`](./")
                       .append(group.getName())
                       .append("-")
                       .append(command.getName())
                       .append(") | ")
                       .append(command.getDescription())
                       .append(" |\n");
            }
        }

        return builder.toString();
    }
}
