package ru.shalomander.jda_bot.base;

import ru.shalomander.jda_bot.JDABot;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.io.UnsupportedEncodingException;
import java.util.*;

public abstract class Command implements Runnable {
    protected MessageReceivedEvent event;
    protected MessageChannel messageChannel;
    protected LinkedList<String> args;
    protected StringBuilder responseMessage;
    public static String description = "",
            argsDescription = "";
    protected JDABot jdaBot;

    public Command(JDABot jdaBot, MessageReceivedEvent event, LinkedList<String> args) {
        this.event = event;
        this.messageChannel = event.getChannel();
        this.args = args;
        this.jdaBot = jdaBot;
        this.responseMessage = new StringBuilder();
    }

    final public void run() {
        buildMessage();
        messageChannel.sendMessage(responseMessage.toString()).queue();
    }

    protected abstract void buildMessage();

    final protected ArrayList<Member> extractOnlineUsers(List<Member> users) {
        String botName = messageChannel.getJDA().getSelfUser().getName();
        ArrayList<Member> result = new ArrayList<>();
        users.forEach(user -> {
            if (!user.getEffectiveName().equals(botName) && user.getOnlineStatus().toString().equals("ONLINE"))
                result.add(user);
        });
        return result;
    }

    final protected String buildUtf8String(String response) {
        String result = "";
        try {
            result = new String(response.getBytes(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
}
