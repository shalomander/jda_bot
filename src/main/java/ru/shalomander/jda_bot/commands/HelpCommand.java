package ru.shalomander.jda_bot.commands;


import ru.shalomander.jda_bot.JDABot;
import ru.shalomander.jda_bot.base.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class HelpCommand extends Command {

    protected static String description = "Список доступных команд",
            argsDescription = "";

    public HelpCommand(JDABot jdaBot, MessageReceivedEvent event, LinkedList<String> args) {
        super(jdaBot, event, args);
    }

    @Override
    protected void buildMessage() {
        HashMap<String, HashMap<String, String>> commands = jdaBot.getCommands();
        HashMap<String, ArrayList<String>> commandsReverse = new HashMap<>();
        responseMessage.append("Доступные команды:\n");
        commands.forEach((alias, command) -> {
            try {
                Class<?> commandInstance = Class.forName(command.get("className"));
                String commandDescription = (String) Class.forName(command.get("className")).getDeclaredField("description").get(commandInstance);
                String commandArgsDescription = (String) Class.forName(command.get("className")).getDeclaredField("argsDescription").get(commandInstance);
                ArrayList<String> aliases = commandsReverse.getOrDefault(commandArgsDescription + " - " + commandDescription, new ArrayList<String>());
                aliases.add(jdaBot.getCommandPrefix() + alias);
                commandsReverse.put(commandArgsDescription + " - " + commandDescription, aliases);
            } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        commandsReverse.forEach((description, aliases) -> {
            responseMessage.append(MessageFormat.format("*{0}* {1}\n", String.join(", ", aliases.toArray(new CharSequence[aliases.size()])), description));
        });
    }
}
