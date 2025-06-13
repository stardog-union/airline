/*
 * Copyright (C) 2012 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.airlift.command;

import com.google.common.collect.ImmutableList;
import io.airlift.command.Cli.CliBuilder;
import io.airlift.command.Git.Add;
import io.airlift.command.Git.RemoteAdd;
import io.airlift.command.Git.RemoteShow;

import io.airlift.command.args.Args1;
import io.airlift.command.args.Args2;
import io.airlift.command.args.ArgsArityString;
import io.airlift.command.args.ArgsBooleanArity;
import io.airlift.command.args.ArgsInherited;
import io.airlift.command.args.ArgsRequired;
import io.airlift.command.args.CommandHidden;
import io.airlift.command.args.GlobalOptionsHidden;
import io.airlift.command.args.OptionsHidden;
import io.airlift.command.args.OptionsRequired;
import io.airlift.command.command.CommandRemove;

import org.testng.Assert;
import org.testng.annotations.Test;

import static io.airlift.command.Cli.buildCli;
import static io.airlift.command.SingleCommand.singleCommand;

@Test
public class HelpTest
{
    @SuppressWarnings("unchecked")
    public void testGit()
    {
        CliBuilder<Runnable> builder = Cli.<Runnable>builder("git")
                                          .withDescription("the stupid content tracker")
                                          .withDefaultCommand(Help.class)
                                          .withCommands(Help.class,
                                                        Add.class);

        builder.withGroup("remote")
               .withDescription("Manage set of tracked repositories")
               .withDefaultCommand(RemoteShow.class)
               .withCommands(RemoteShow.class,
                             RemoteAdd.class);

        Cli<Runnable> gitParser = builder.build();

        StringBuilder out = new StringBuilder();
        Help.help(gitParser.getMetadata(), ImmutableList.of(), out);
        Assert.assertEquals(out.toString(), "usage: git [ -v ] <command> [ <args> ]\n" +
                                            "\n" +
                                            "Commands are:\n" +
                                            "    add      Add file contents to the index\n" +
                                            "    help     Display help information\n" +
                                            "    remote   Manage set of tracked repositories\n" +
                                            "\n" +
                                            "See 'git help <command>' for more information on a specific command.\n");

        out = new StringBuilder();
        Help.help(gitParser.getMetadata(), ImmutableList.of("add"), out);
        Assert.assertEquals(out.toString(), "NAME\n" +
                                            "        git add - Add file contents to the index\n" +
                                            "\n" +
                                            "SYNOPSIS\n" +
                                            "        git [ -v ] add [ -i ] [--] [ <patterns>... ]\n" +
                                            "\n" +
                                            "OPTIONS\n" +
                                            "        -i\n" +
                                            "            Add modified contents interactively.\n" +
                                            "\n" +
                                            "        -v\n" +
                                            "            Verbose mode\n" +
                                            "\n" +
                                            "        --\n" +
                                            "            This option can be used to separate command-line options from the\n" +
                                            "            list of argument, (useful when arguments might be mistaken for\n" +
                                            "            command-line options\n" +
                                            "\n" +
                                            "        <patterns>\n" +
                                            "            Patterns of files to be added\n" +
                                            "\n");

        out = new StringBuilder();
        Help.help(gitParser.getMetadata(), ImmutableList.of("remote"), out);
        Assert.assertEquals(out.toString(), "NAME\n" +
                                            "        git remote - Manage set of tracked repositories\n" +
                                            "\n" +
                                            "SYNOPSIS\n" +
                                            "        git [ -v ] remote { add | show* } [--] [cmd-options] <cmd-args>\n" +
                                            "\n" +
                                            "        Where command-specific options [cmd-options] are:\n" +
                                            "            add: [ -t <branch> ]\n" +
                                            "            show: [ -n ]\n" +
                                            "\n" +
                                            "        Where command-specific arguments <cmd-args> are:\n" +
                                            "            add: [ <name> <url>... ]\n" +
                                            "            show: [ <remote> ]\n" +
                                            "\n" +
                                            "        * show is the default command\n" +
                                            "        See 'git help remote <command>' for more information on a specific command.\n" +
                                            "OPTIONS\n" +
                                            "        -v\n" +
                                            "            Verbose mode\n" +
                                            "\n");

        out = new StringBuilder();
        Help.help(gitParser.getMetadata(), ImmutableList.of("remote", "add"), out);
        Assert.assertEquals(out.toString(), "NAME\n" +
                                            "        git remote add - Adds a remote\n" +
                                            "\n" +
                                            "SYNOPSIS\n" +
                                            "        git [ -v ] remote add [ -t <branch> ] [--] [ <name> <url>... ]\n" +
                                            "\n" +
                                            "OPTIONS\n" +
                                            "        -t <branch>\n" +
                                            "            Track only a specific branch\n" +
                                            "\n" +
                                            "        -v\n" +
                                            "            Verbose mode\n" +
                                            "\n" +
                                            "        --\n" +
                                            "            This option can be used to separate command-line options from the\n" +
                                            "            list of argument, (useful when arguments might be mistaken for\n" +
                                            "            command-line options\n" +
                                            "\n" +
                                            "        <name> <url>\n" +
                                            "            Name and URL of remote repository to add\n" +
                                            "\n"
        );
    }

    @SuppressWarnings("unchecked")
    public void testGitMd()
    {
        CliBuilder<Runnable> builder = Cli.<Runnable>builder("git")
                                          .withDescription("the stupid content tracker")
                                          .withDefaultCommand(Help.class)
                                          .withCommands(Help.class, Add.class);

        builder.withGroup("remote")
               .withDescription("Manage set of tracked repositories")
               .withMarkdownDescription("Commands for managing tracked repositories. Read more about [Remote Management](../../git/with-it).")
               .withDefaultCommand(RemoteShow.class)
               .withCommands(RemoteShow.class, RemoteAdd.class);

        Cli<Runnable> gitParser = builder.build();

        Help.USAGE_AS_MD = true;
        try {
            StringBuilder out = new StringBuilder();

            Help.help(gitParser.getMetadata(), ImmutableList.of("git"), out);
            Assert.assertEquals(out.toString(),
                                "---\n" +
                                "layout: default\n" +
                                "title: git\n" +
                                "has_children: true\n" +
                                "has_toc: false\n" +
                                "description: This chapter contains details of all git CLI commands.\n" +
                                "---\n" +
                                "\n" +
                                "# git\n" +
                                "\n" +
                                "the stupid content tracker\n" +
                                "\n" +
                                "Below you'll find all command groups in the `git` client. Select any of the command groups in the table below to view the specific commands in that command group.\n" +
                                "\n" +
                                "| Command Group | Description |\n" +
                                "|---------------|-------------|\n" +
                                "| [`remote`](./remote) | Manage set of tracked repositories |\n");

            out = new StringBuilder();
            Help.help(gitParser.getMetadata(), ImmutableList.of("add"), out);
            Assert.assertEquals(out.toString(),
                                "---\n" +
                                "layout: default\n" +
                                "title: null add\n" +
                                "grand_parent: Stardog Admin CLI Reference\n" +
                                "parent: null\n" +
                                "description: 'Add file contents to the index'\n" +
                                "---\n" +
                                "\n" +
                                "#  `git null add` \n" +
                                "## Description\n" +
                                "Add file contents to the index\n" +
                                "## Usage\n" +
                                "`git [ -v ] add [ -i ] [--] [ <patterns>... ]`\n" +
                                "{: .fs-5}\n" +
                                "## Options\n" +
                                "\n" +
                                "Name, shorthand | Description \n" +
                                "---|---\n" +
                                "`-i` | Add modified contents interactively.\n" +
                                "`-v` | Verbose mode\n" +
                                "`--` | This option can be used to separate command-line options from the list of argument(s). (Useful when an argument might be mistaken for a command-line option)\n" +
                                "`<patterns>` | Patterns of files to be added\n");

            out = new StringBuilder();
            Help.help(gitParser.getMetadata(), ImmutableList.of("remote"), out);
            Assert.assertEquals(out.toString(),
                                "---\n" +
                                "layout: default\n" +
                                "title: remote\n" +
                                "parent: Stardog Admin CLI Reference\n" +
                                "has_children: true\n" +
                                "has_toc: false\n" +
                                "description: This page contains the commands available in the git remote command group.\n" +
                                "---\n" +
                                "\n" +
                                "# `remote`\n" +
                                "\n" +
                                "Commands for managing tracked repositories. Read more about [Remote Management](../../git/with-it).\n" +
                                "\n" +
                                "\n" +
                                "Select any of the commands to view their manual page.\n" +
                                "\n" +
                                "| Command | Description |\n" +
                                "|---------|-------------|\n" +
                                "| [`remote add`](./remote-add) | Adds a remote |\n" +
                                "| [`remote show`](./remote-show) | Gives some information about the remote <name> |\n");

            out = new StringBuilder();
            Help.help(gitParser.getMetadata(), ImmutableList.of("remote", "add"), out);
            Assert.assertEquals(out.toString(),
                                "---\n" +
                                "layout: default\n" +
                                "title: remote add\n" +
                                "grand_parent: Stardog Admin CLI Reference\n" +
                                "parent: remote\n" +
                                "description: 'Adds a remote'\n" +
                                "---\n" +
                                "\n" +
                                "#  `git remote add` \n" +
                                "## Description\n" +
                                "Adds a remote\n" +
                                "## Usage\n" +
                                "`git [ -v ] remote  add [ -t <branch> ] [--] [ <name> <url>... ]`\n" +
                                "{: .fs-5}\n" +
                                "## Options\n" +
                                "\n" +
                                "Name, shorthand | Description \n" +
                                "---|---\n" +
                                "`-t <branch>` | Track only a specific branch\n" +
                                "`-v` | Verbose mode\n" +
                                "`--` | This option can be used to separate command-line options from the list of argument(s). (Useful when an argument might be mistaken for a command-line option)\n" +
                                "`<name> <url>` | Name and URL of remote repository to add\n"
            );
        }
        finally {
            Help.USAGE_AS_MD = false;
        }
    }

    @SuppressWarnings("unchecked")
    public void testGitHtml()
    {
        CliBuilder<Runnable> builder = Cli.<Runnable>builder("git")
                                          .withDescription("the stupid content tracker")
                                          .withDefaultCommand(Help.class)
                                          .withCommands(Help.class, Add.class);

        builder.withGroup("remote")
               .withDescription("Manage set of tracked repositories")
               .withDefaultCommand(RemoteShow.class)
               .withCommands(RemoteShow.class, RemoteAdd.class);

        Cli<Runnable> gitParser = builder.build();

        Help.USAGE_AS_HTML = true;
        try {
            StringBuilder out = new StringBuilder();
            Help.help(gitParser.getMetadata(), ImmutableList.of("add"), out);
            Assert.assertEquals(out.toString(),
                                "<html>\n" +
                                "<head>\n" +
                                "<link href=\"css/bootstrap.min.css\" rel=\"stylesheet\" media=\"screen\">\n" +
                                "</head>\n" +
                                "<style>\n" +
                                "    body { margin: 50px; }\n" +
                                "</style>\n" +
                                "<body>\n" +
                                "<hr/>\n" +
                                "<h1 class=\"text-info\">git null add Manual Page\n" +
                                "<hr/>\n" +
                                "<h1 class=\"text-info\">NAME</h1>\n" +
                                "<br/>\n" +
                                "<div class=\"row\"><div class=\"span8 offset1\">git null add &mdash;Add file contents to the index</div>\n" +
                                "</div>\n" +
                                "<br/>\n" +
                                "<h1 class=\"text-info\">SYNOPSIS</h1>\n" +
                                "<br/>\n" +
                                "<div class=\"row\">\n" +
                                "<div class=\"span8 offset1\">\n" +
                                "git [ -v ] add [ -i ] [--] [ &lt;patterns&gt;... ]</div>\n" +
                                "</div>\n" +
                                "<br/>\n" +
                                "<h1 class=\"text-info\">OPTIONS</h1>\n" +
                                "<br/>\n" +
                                "<div class=\"row\">\n" +
                                "<div class=\"span8 offset1\">\n" +
                                "-i</div>\n" +
                                "</div>\n" +
                                "<div class=\"row\">\n" +
                                "<div class=\"span8 offset2\">\n" +
                                "Add modified contents interactively.</div>\n" +
                                "</div>\n" +
                                "<div class=\"row\">\n" +
                                "<div class=\"span8 offset1\">\n" +
                                "-v</div>\n" +
                                "</div>\n" +
                                "<div class=\"row\">\n" +
                                "<div class=\"span8 offset2\">\n" +
                                "Verbose mode</div>\n" +
                                "</div>\n" +
                                "<div class=\"row\">\n" +
                                "<div class=\"span8 offset1\">\n" +
                                "--\n" +
                                "</div>\n" +
                                "</div>\n" +
                                "<div class=\"row\">\n" +
                                "<div class=\"span8 offset2\">\n" +
                                "This option can be used to separate command-line options from the list of argument, (useful when arguments might be mistaken for command-line options\n" +
                                "</div>\n" +
                                "</div>\n" +
                                "<div class=\"row\">\n" +
                                "<div class=\"span8 offset1\">\n" +
                                "&lt;patterns&gt;</div>\n" +
                                "</div>\n" +
                                "<div class=\"row\">\n" +
                                "<div class=\"span8 offset2\">\n" +
                                "Patterns of files to be added</div>\n" +
                                "</div>\n" +
                                "</body>\n" +
                                "</html>\n");

            out = new StringBuilder();
            Help.help(gitParser.getMetadata(), ImmutableList.of("remote", "add"), out);
            Assert.assertEquals(out.toString(),
                                "<html>\n" +
                                "<head>\n" +
                                "<link href=\"css/bootstrap.min.css\" rel=\"stylesheet\" media=\"screen\">\n" +
                                "</head>\n" +
                                "<style>\n" +
                                "    body { margin: 50px; }\n" +
                                "</style>\n" +
                                "<body>\n" +
                                "<hr/>\n" +
                                "<h1 class=\"text-info\">git remote add Manual Page\n" +
                                "<hr/>\n" +
                                "<h1 class=\"text-info\">NAME</h1>\n" +
                                "<br/>\n" +
                                "<div class=\"row\"><div class=\"span8 offset1\">git remote add &mdash;Adds a remote</div>\n" +
                                "</div>\n" +
                                "<br/>\n" +
                                "<h1 class=\"text-info\">SYNOPSIS</h1>\n" +
                                "<br/>\n" +
                                "<div class=\"row\">\n" +
                                "<div class=\"span8 offset1\">\n" +
                                "git [ -v ] remote  add [ -t &lt;branch&gt; ] [--] [ &lt;name&gt; &lt;url&gt;... ]</div>\n" +
                                "</div>\n" +
                                "<br/>\n" +
                                "<h1 class=\"text-info\">OPTIONS</h1>\n" +
                                "<br/>\n" +
                                "<div class=\"row\">\n" +
                                "<div class=\"span8 offset1\">\n" +
                                "-t &lt;branch&gt;</div>\n" +
                                "</div>\n" +
                                "<div class=\"row\">\n" +
                                "<div class=\"span8 offset2\">\n" +
                                "Track only a specific branch</div>\n" +
                                "</div>\n" +
                                "<div class=\"row\">\n" +
                                "<div class=\"span8 offset1\">\n" +
                                "-v</div>\n" +
                                "</div>\n" +
                                "<div class=\"row\">\n" +
                                "<div class=\"span8 offset2\">\n" +
                                "Verbose mode</div>\n" +
                                "</div>\n" +
                                "<div class=\"row\">\n" +
                                "<div class=\"span8 offset1\">\n" +
                                "--\n" +
                                "</div>\n" +
                                "</div>\n" +
                                "<div class=\"row\">\n" +
                                "<div class=\"span8 offset2\">\n" +
                                "This option can be used to separate command-line options from the list of argument, (useful when arguments might be mistaken for command-line options\n" +
                                "</div>\n" +
                                "</div>\n" +
                                "<div class=\"row\">\n" +
                                "<div class=\"span8 offset1\">\n" +
                                "&lt;name&gt; &lt;url&gt;</div>\n" +
                                "</div>\n" +
                                "<div class=\"row\">\n" +
                                "<div class=\"span8 offset2\">\n" +
                                "Name and URL of remote repository to add</div>\n" +
                                "</div>\n" +
                                "</body>\n" +
                                "</html>\n"
            );
        }
        finally {
            Help.USAGE_AS_HTML = false;
        }
    }

    @Test
    public void testArgs1()
    {
        CliBuilder<Object> builder = Cli.builder("test")
                .withDescription("Test commandline")
                .withDefaultCommand(Help.class)
                .withCommands(Help.class,
                        Args1.class);

        Cli<Object> parser = builder.build();

        StringBuilder out = new StringBuilder();
        Help.help(parser.getMetadata(), ImmutableList.of("Args1"), out);
        Assert.assertEquals(out.toString(), "NAME\n" +
                "        test Args1 - args1 description\n" +
                "\n" +
                "SYNOPSIS\n" +
                "        test Args1 [ -bigdecimal <bigd> ] [ -date <date> ] [ -debug ]\n" +
                "                [ -double <doub> ] [ -float <floa> ] [ -groups <groups> ]\n" +
                "                [ {-log | -verbose} <verbose> ] [ -long <l> ] [--] [ <parameters>... ]\n" +
                "\n" +
                "OPTIONS\n" +
                "        -bigdecimal <bigd>\n" +
                "            A BigDecimal number\n" +
                "\n" +
                "        -date <date>\n" +
                "            An ISO 8601 formatted date.\n" +
                "\n" +
                "        -debug\n" +
                "            Debug mode\n" +
                "\n" +
                "        -double <doub>\n" +
                "            A double number\n" +
                "\n" +
                "        -float <floa>\n" +
                "            A float number\n" +
                "\n" +
                "        -groups <groups>\n" +
                "            Comma-separated list of group names to be run\n" +
                "\n" +
                "        -log <verbose>, -verbose <verbose>\n" +
                "            Level of verbosity\n" +
                "\n" +
                "        -long <l>\n" +
                "            A long number\n" +
                "\n" +
                "        --\n" +
                "            This option can be used to separate command-line options from the\n" +
                "            list of argument, (useful when arguments might be mistaken for\n" +
                "            command-line options\n" +
                "\n" +
                "        <parameters>\n" +
                "\n" +
                "\n");
    }

   @Test
   public void testArgs2()
    {
        CliBuilder<Object> builder = Cli.builder("test")
                .withDescription("Test commandline")
                .withDefaultCommand(Help.class)
                .withCommands(Help.class,
                        Args2.class);

        Cli<Object> parser = builder.build();

        StringBuilder out = new StringBuilder();
        Help.help(parser.getMetadata(), ImmutableList.of("Args2"), out);
        Assert.assertEquals(out.toString(), "NAME\n" +
                "        test Args2 -\n" +
                "\n" +
                "SYNOPSIS\n" +
                "        test Args2 [ -debug ] [ -groups <groups> ] [ -host <hosts>... ]\n" +
                "                [ {-log | -verbose} <verbose> ] [--] [ <parameters>... ]\n" +
                "\n" +
                "OPTIONS\n" +
                "        -debug\n" +
                "            Debug mode\n" +
                "\n" +
                "        -groups <groups>\n" +
                "            Comma-separated list of group names to be run\n" +
                "\n" +
                "        -host <hosts>\n" +
                "            The host\n" +
                "\n" +
                "        -log <verbose>, -verbose <verbose>\n" +
                "            Level of verbosity\n" +
                "\n" +
                "        --\n" +
                "            This option can be used to separate command-line options from the\n" +
                "            list of argument, (useful when arguments might be mistaken for\n" +
                "            command-line options\n" +
                "\n" +
                "        <parameters>\n" +
                "            List of parameters\n" +
                "\n");
    }

    @Test
    public void testArgsAritySting()
    {
        CliBuilder<Object> builder = Cli.builder("test")
                .withDescription("Test commandline")
                .withDefaultCommand(Help.class)
                .withCommands(Help.class,
                        ArgsArityString.class);

        Cli<Object> parser = builder.build();

        StringBuilder out = new StringBuilder();
        Help.help(parser.getMetadata(), ImmutableList.of("ArgsArityString"), out);
        Assert.assertEquals(out.toString(), "NAME\n" +
                "        test ArgsArityString -\n" +
                "\n" +
                "SYNOPSIS\n" +
                "        test ArgsArityString [ -pairs <pairs>... ] [--] [ <rest>... ]\n" +
                "\n" +
                "OPTIONS\n" +
                "        -pairs <pairs>\n" +
                "            Pairs\n" +
                "\n" +
                "        --\n" +
                "            This option can be used to separate command-line options from the\n" +
                "            list of argument, (useful when arguments might be mistaken for\n" +
                "            command-line options\n" +
                "\n" +
                "        <rest>\n" +
                "            Rest\n" +
                "\n");
    }

    @Test
    public void testArgsBooleanArity()
    {
        CliBuilder<Object> builder = Cli.builder("test")
                .withDescription("Test commandline")
                .withDefaultCommand(Help.class)
                .withCommands(Help.class,
                        ArgsBooleanArity.class);

        Cli<Object> parser = builder.build();

        StringBuilder out = new StringBuilder();
        Help.help(parser.getMetadata(), ImmutableList.of("ArgsBooleanArity"), out);
        Assert.assertEquals(out.toString(), "NAME\n" +
                "        test ArgsBooleanArity -\n" +
                "\n" +
                "SYNOPSIS\n" +
                "        test ArgsBooleanArity [ -debug <debug> ]\n" +
                "\n" +
                "OPTIONS\n" +
                "        -debug <debug>\n" +
                "\n" +
                "\n");
    }

    @Test
    public void testArgsInherited()
    {
        CliBuilder<Object> builder = Cli.builder("test")
                .withDescription("Test commandline")
                .withDefaultCommand(Help.class)
                .withCommands(Help.class,
                        ArgsInherited.class);

        Cli<Object> parser = builder.build();

        StringBuilder out = new StringBuilder();
        Help.help(parser.getMetadata(), ImmutableList.of("ArgsInherited"), out);
        Assert.assertEquals(out.toString(), "NAME\n" +
                "        test ArgsInherited -\n" +
                "\n" +
                "SYNOPSIS\n" +
                "        test ArgsInherited [ -child <child> ] [ -debug ] [ -groups <groups> ]\n" +
                "                [ -level <level> ] [ -log <log> ] [--] [ <parameters>... ]\n" +
                "\n" +
                "OPTIONS\n" +
                "        -child <child>\n" +
                "            Child parameter\n" +
                "\n" +
                "        -debug\n" +
                "            Debug mode\n" +
                "\n" +
                "        -groups <groups>\n" +
                "            Comma-separated list of group names to be run\n" +
                "\n" +
                "        -level <level>\n" +
                "            A long number\n" +
                "\n" +
                "        -log <log>\n" +
                "            Level of verbosity\n" +
                "\n" +
                "        --\n" +
                "            This option can be used to separate command-line options from the\n" +
                "            list of argument, (useful when arguments might be mistaken for\n" +
                "            command-line options\n" +
                "\n" +
                "        <parameters>\n" +
                "\n" +
                "\n");
    }

    @Test
    public void testArgsRequired()
    {
        CliBuilder<Object> builder = Cli.builder("test")
                .withDescription("Test commandline")
                .withDefaultCommand(Help.class)
                .withCommands(Help.class,
                        ArgsRequired.class);

        Cli<Object> parser = builder.build();

        StringBuilder out = new StringBuilder();
        Help.help(parser.getMetadata(), ImmutableList.of("ArgsRequired"), out);
        Assert.assertEquals(out.toString(), "NAME\n" +
                "        test ArgsRequired -\n" +
                "\n" +
                "SYNOPSIS\n" +
                "        test ArgsRequired [--] <parameters>...\n" +
                "\n" +
                "OPTIONS\n" +
                "        --\n" +
                "            This option can be used to separate command-line options from the\n" +
                "            list of argument, (useful when arguments might be mistaken for\n" +
                "            command-line options\n" +
                "\n" +
                "        <parameters>\n" +
                "            List of files\n" +
                "\n");
    }

    @Test
    public void testOptionsRequired()
    {
        CliBuilder<Object> builder = Cli.builder("test")
                .withDescription("Test commandline")
                .withDefaultCommand(Help.class)
                .withCommands(Help.class,
                        OptionsRequired.class);

        Cli<Object> parser = builder.build();

        StringBuilder out = new StringBuilder();
        Help.help(parser.getMetadata(), ImmutableList.of("OptionsRequired"), out);
        Assert.assertEquals(out.toString(), "NAME\n" +
                "        test OptionsRequired -\n" +
                "\n" +
                "SYNOPSIS\n" +
                "        test OptionsRequired [ --optional <optionalOption> ]\n" +
                "                --required <requiredOption>\n" +
                "\n" +
                "OPTIONS\n" +
                "        --optional <optionalOption>\n" +
                "\n" +
                "\n" +
                "        --required <requiredOption>\n" +
                "\n" +
                "\n");
    }

    @Test
    public void testOptionsHidden()
    {
        CliBuilder<Object> builder = Cli.builder("test")
                .withDescription("Test commandline")
                .withDefaultCommand(Help.class)
                .withCommands(Help.class,
                        OptionsHidden.class);

        Cli<Object> parser = builder.build();

        StringBuilder out = new StringBuilder();
        Help.help(parser.getMetadata(), ImmutableList.of("OptionsHidden"), out);
        Assert.assertEquals(out.toString(), "NAME\n" +
                "        test OptionsHidden -\n" +
                "\n" +
                "SYNOPSIS\n" +
                "        test OptionsHidden [ --optional <optionalOption> ]\n" +
                "\n" +
                "OPTIONS\n" +
                "        --optional <optionalOption>\n" +
                "\n" +
                "\n");
    }

    @Test
    public void testGlobalOptionsHidden()
    {
        CliBuilder<Object> builder = buildCli("test", Object.class)
                .withDescription("Test commandline")
                .withDefaultCommand(Help.class)
                .withCommands(Help.class, GlobalOptionsHidden.class);

        Cli<Object> parser = builder.build();

        StringBuilder out = new StringBuilder();
        Help.help(parser.getMetadata(), ImmutableList.of("GlobalOptionsHidden"), out);
        Assert.assertEquals(out.toString(), "NAME\n" +
                "        test GlobalOptionsHidden -\n" +
                "\n" +
                "SYNOPSIS\n" +
                "        test [ {-op | --optional} ] GlobalOptionsHidden\n" +
                "\n" +
                "OPTIONS\n" +
                "        -op, --optional\n" +
                "\n" +
                "\n");
    }

    @Test
    public void testCommandHidden()
    {
        CliBuilder<Object> builder = Cli.builder("test")
                .withDescription("Test commandline")
                .withDefaultCommand(Help.class)
                .withCommands(Help.class,
                        ArgsRequired.class, CommandHidden.class);

        Cli<Object> parser = builder.build();

        StringBuilder out = new StringBuilder();
        Help.help(parser.getMetadata(), ImmutableList.of(), out);
        Assert.assertEquals(out.toString(), "usage: test <command> [ <args> ]\n" +
                "\n" +
                "Commands are:\n" +
                "    ArgsRequired\n" +
                "    help           Display help information\n" +
                "\n" +
                "See 'test help <command>' for more information on a specific command.\n");

        out = new StringBuilder();
        Help.help(parser.getMetadata(), ImmutableList.of("CommandHidden"), out);
        Assert.assertEquals(out.toString(), "NAME\n" +
                "        test CommandHidden -\n" +
                "\n" +
                "SYNOPSIS\n" +
                "        test CommandHidden [ --optional <optionalOption> ]\n" +
                "\n" +
                "OPTIONS\n" +
                "        --optional <optionalOption>\n" +
                "\n" +
                "\n");

    }

    @Test
    public void testExamplesAndDiscussion() {
        Cli<?> parser = Cli.builder("git")
            .withCommand(CommandRemove.class)
            .build();

        StringBuilder out = new StringBuilder();
        Help.help(parser.getMetadata(), ImmutableList.of("remove"), out);

        String discussion = "DISCUSSION\n" +
        "        More details about how this removes files from the index.\n" +
        "\n";

        String examples = "EXAMPLES\n" +
        "        * The following is a usage example:\n" +
        "        \t$ git remove -i myfile.java\n";

        Assert.assertTrue(out.toString().contains(discussion), "Expected the discussion section to be present in the help");
        Assert.assertTrue(out.toString().contains(examples), "Expected the examples section to be present in the help");
    }
    
    @Test
    public void testSingleCommandArgs1()
    {
        SingleCommand<Args1> command = singleCommand(Args1.class);

        StringBuilder out = new StringBuilder();
        new CommandUsage().usage(null, null, "test", command.getCommandMetadata(), out);
        Assert.assertEquals(out.toString(), "NAME\n" +
                "        test - args1 description\n" +
                "\n" +
                "SYNOPSIS\n" +
                "        test [ -bigdecimal <bigd> ] [ -date <date> ] [ -debug ]\n" +
                "                [ -double <doub> ] [ -float <floa> ] [ -groups <groups> ]\n" +
                "                [ {-log | -verbose} <verbose> ] [ -long <l> ] [--] [ <parameters>... ]\n" +
                "\n" +
                "OPTIONS\n" +
                "        -bigdecimal <bigd>\n" +
                "            A BigDecimal number\n" +
                "\n" +
                "        -date <date>\n" +
                "            An ISO 8601 formatted date.\n" +
                "\n" +
                "        -debug\n" +
                "            Debug mode\n" +
                "\n" +
                "        -double <doub>\n" +
                "            A double number\n" +
                "\n" +
                "        -float <floa>\n" +
                "            A float number\n" +
                "\n" +
                "        -groups <groups>\n" +
                "            Comma-separated list of group names to be run\n" +
                "\n" +
                "        -log <verbose>, -verbose <verbose>\n" +
                "            Level of verbosity\n" +
                "\n" +
                "        -long <l>\n" +
                "            A long number\n" +
                "\n" +
                "        --\n" +
                "            This option can be used to separate command-line options from the\n" +
                "            list of argument, (useful when arguments might be mistaken for\n" +
                "            command-line options\n" +
                "\n" +
                "        <parameters>\n" +
                "\n" +
                "\n");
    }

    @Test
    public void testUnknownCommand() {
        CliBuilder<Object> builder = Cli.builder("test")
                                             .withDescription("Test commandline")
                                             .withDefaultCommand(Help.class)
                                             .withCommands(Help.class,
                                                     OptionsRequired.class);
        try {
            Help help = (Help) builder.build().parse("asdf");
            help.call();
            Assert.fail("Exception should have been thrown for unknown command");
        }
        catch (UnsupportedOperationException e) {
            Assert.assertEquals(e.getMessage(), "Unknown command asdf");
        }
    }
}
