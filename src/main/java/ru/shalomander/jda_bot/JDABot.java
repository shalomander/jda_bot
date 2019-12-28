package ru.shalomander.jda_bot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import ru.shalomander.jda_bot.base.Command;
import ru.shalomander.jda_bot.base.Task;
import ru.shalomander.jda_bot.commands.HelpCommand;

import javax.security.auth.login.LoginException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Pattern;

public class JDABot extends ListenerAdapter {
    protected static String botName;
    protected String token,
            commandPrefix = "!";
    protected static int commandTimeout = 30;
    protected HashMap<String, HashMap<String, String>> commands = new HashMap<>();
    protected JDA jda;
    protected HashMap<String, Object> storage = new HashMap<>();

    public JDABot(String token) {
        setToken(token);
        registerCommand("help", HelpCommand.class);
        run();
    }

    public void run() {
        try {
            jda = new JDABuilder(getToken())
                    .addEventListeners(this)
                    .build();
            jda.awaitReady();
            botName = jda.getSelfUser().getName();
            System.out.println("Finished Building JDA!");
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (!jda.equals(null)) {
            jda.shutdown();
        }
    }

    public HashMap<String, HashMap<String, String>> getCommands() {
        return commands;
    }

    public JDABot setToken(String token) {
        this.token = token;
        return this;
    }

    public String getToken() {
        return token;
    }

    public void addTask(Class<? extends Task> task, long period) {
        Timer timer = new Timer();
        try {
            timer.schedule(task.getDeclaredConstructor(JDABot.class).newInstance(this), 0, period);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public void setCommandTimeout(int timeout) {
        commandTimeout = timeout;
    }

    public static int getCommandTimeout() {
        return commandTimeout;
    }

    public void setCommandPrefix(String commandPrefix) {
        this.commandPrefix = commandPrefix;
    }

    public String getCommandPrefix() {
        return commandPrefix;
    }

    public void registerCommand(String alias, Class<? extends Command> c, int timeout) {
        HashMap<String, String> command = new HashMap<>();
        command.put("className", c.getCanonicalName());
        command.put("timeout", String.valueOf(timeout));
        command.put("lastCall", String.valueOf(0L));
        commands.put(alias, command);
    }

    public void registerCommand(String alias, Class<? extends Command> c) {
        registerCommand(alias, c, JDABot.getCommandTimeout());
    }

    public void addCommandAlias(String alias, String command) {
        commands.putIfAbsent(alias, commands.get(command));
    }


    public JDA getJda() {
        return jda;
    }

    public void setStorageValue(String key, Object value) {
        storage.put(key, value);
    }


    public <T> Object getStorageValue(String key, T defaultValue) {
        return storage.getOrDefault(key, defaultValue);
    }

    public Object getStorageValue(String key) {
        return getStorageValue(key, new Object());
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message msg = event.getMessage();
        String msgRaw = msg.getContentRaw()
                .toLowerCase();
        if (msgRaw.startsWith(commandPrefix)) {
            LinkedList<String> args = new LinkedList<>();
            args.addAll(Arrays.asList(msgRaw.split("\\s+")));
            System.out.println(args);
            String commandString = args.pollFirst().
                    replaceFirst(Pattern.quote(commandPrefix), "");
            try {
                HashMap<String, String> command = commands.getOrDefault(commandString, commands.get("help"));
                Long callTime = Calendar.getInstance()
                        .getTimeInMillis();
                if (callTime - Long.parseLong(command.getOrDefault("lastCall", "0L")) > Long.parseLong(command.getOrDefault("timeout", "0L")) * 1000) {
                    Command commandInstance = (Command) Class.forName(command.get("className"))
                            .getConstructor(JDABot.class, MessageReceivedEvent.class, LinkedList.class)
                            .newInstance(this, event, args);
                    Thread commandThread = new Thread(commandInstance);
                    commandThread.start();
                    command.put("lastCall", String.valueOf(callTime));
                    commands.put(commandString, command);
                }
            } catch (InstantiationException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
