package io.airlift.command;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import io.airlift.command.model.CommandGroupMetadata;
import io.airlift.command.model.CommandMetadata;
import io.airlift.command.model.GlobalMetadata;
import io.airlift.command.model.OptionMetadata;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static io.airlift.command.UsageHelper.DEFAULT_OPTION_COMPARATOR;

public class GlobalUsage
{
    private final int columnSize;
    private final Comparator<? super OptionMetadata> optionComparator;

    public GlobalUsage()
    {
        this(79, DEFAULT_OPTION_COMPARATOR);
    }

    public GlobalUsage(int columnSize)
    {
        this(columnSize, DEFAULT_OPTION_COMPARATOR);
    }

    public GlobalUsage(int columnSize, @Nullable Comparator<? super OptionMetadata> optionComparator)
    {
        Preconditions.checkArgument(columnSize > 0, "columnSize must be greater than 0");
        this.columnSize = columnSize;
        this.optionComparator = optionComparator;
    }

    /**
     * Display the help on System.out.
     */
    public void usage(GlobalMetadata global)
    {
        StringBuilder stringBuilder = new StringBuilder();
        usage(global, stringBuilder);
        System.out.println(stringBuilder);
    }

    /**
     * Store the help in the passed-in string builder.
     */
    public void usage(GlobalMetadata global, StringBuilder out)
    {
        usage(global, new UsagePrinter(out, columnSize));
    }

    public void usage(GlobalMetadata global, UsagePrinter out)
    {
        //
        // NAME
        //
        out.append("NAME").newline();

        out.newIndentedPrinter(8)
                .append(global.getName())
                .append("-")
                .append(global.getDescription())
                .newline()
                .newline();

        //
        // SYNOPSIS
        //
        out.append("SYNOPSIS").newline();
        out.newIndentedPrinter(8).newPrinterWithHangingIndent(8)
                .append(global.getName())
                .appendWords(UsageHelper.toSynopsisUsage(global.getOptions()))
                .append("<command> [ <args> ]")
                .newline()
                .newline();

        //
        // OPTIONS
        //
        List<OptionMetadata> options = newArrayList(global.getOptions());
        if (!options.isEmpty()) {
            if (optionComparator != null) {
                options.sort(optionComparator);
            }

            out.append("OPTIONS").newline();

            for (OptionMetadata option : options) {

                if (option.isHidden())
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

        //
        // COMMANDS
        //
        out.append("COMMANDS").newline();
        UsagePrinter commandPrinter = out.newIndentedPrinter(8);

        for (CommandMetadata command : global.getDefaultGroupCommands()) {
            printCommandDescription(commandPrinter, null, command);
        }
        for (CommandGroupMetadata group : global.getCommandGroups()) {
            for (CommandMetadata command : group.getCommands()) {
                printCommandDescription(commandPrinter, group, command);
            }
        }
    }

    private void printCommandDescription(UsagePrinter commandPrinter, @Nullable CommandGroupMetadata group, CommandMetadata command)
    {
        if(!command.isHidden())
        {
            if (group != null) {
                commandPrinter.append(group.getName());
            }
            commandPrinter.append(command.getName()).newline();
            if (command.getDescription() != null) {
                commandPrinter.newIndentedPrinter(4).append(command.getDescription()).newline();
            }
            commandPrinter.newline();
        }
    }

    public String usageMD(GlobalMetadata global) {
        List<CommandGroupMetadata> groups = ImmutableList.sortedCopyOf(Comparator.comparing(CommandGroupMetadata::getName),
                                                                       global.getCommandGroups());
        List<CommandMetadata> commands = ImmutableList.sortedCopyOf(Comparator.comparing(CommandMetadata::getName),
                                                                    global.getDefaultGroupCommands());

        StringBuilder builder = new StringBuilder();

        // for jekyll to pick up these pages on the website
        builder.append("---\n");
        builder.append("layout: default\n");
        String friendlyName = "stardog".equals(global.getName())
                              ? "Stardog CLI Reference"
                              : "stardog-admin".equals(global.getName())
                                ? "Stardog Admin CLI Reference"
                                : global.getName();
        builder.append("title: ").append(friendlyName).append("\n");
        if (global.getNavOrder() != null) {
            builder.append("nav_order: ").append(global.getNavOrder()).append("\n");
        }
        builder.append("has_children: true\n");
        builder.append("has_toc: false\n");
        builder.append("description: This chapter contains details of all ")
               .append(global.getName())
               .append(" CLI commands.\n");
        builder.append("---\n\n");

        builder.append("# ").append(friendlyName).append("\n");

        if (!Strings.isNullOrEmpty(global.getDescription())) {
            builder.append("\n");
            builder.append(global.getDescription());
            builder.append("\n");
        }

//        if (!commands.isEmpty()) {
//            builder.append("\n");
//            builder.append("Below you'll find all the top-level commands in the `")
//                   .append(global.getName())
//                   .append("` default group. Select any of the commands in the table below to view detailed help for that command.");
//            builder.append("\n\n");
//            builder.append("| Command | Description |\n");
//            builder.append("|---------|-------------|\n");
//            for (CommandMetadata command : commands) {
//                builder.append("| [`")
//                       .append(command.getName())
//                       .append("`](./")
//                       .append(command.getName())
//                       .append(") | ")
//                       .append(command.getDescription())
//                       .append(" |\n");
//            }
//        }

        if (!groups.isEmpty()) {
            builder.append("\n");
            builder.append("Below you'll find all command groups in the `")
                   .append(global.getName())
                   .append("` client. Select any of the command groups in the table below to view the specific commands in that command group.");
            builder.append("\n\n");
            builder.append("| Command Group | Description |\n");
            builder.append("|---------------|-------------|\n");
            for (CommandGroupMetadata group: groups) {
                builder.append("| [`")
                       .append(group.getName())
                       .append("`](./")
                       .append(group.getName())
                       .append(") | ")
                       .append(group.getDescription())
                       .append(" |\n");
            }
        }

        return builder.toString();
    }
}
