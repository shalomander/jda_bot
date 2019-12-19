package ru.shalomander.jda_bot.base;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import ru.shalomander.jda_bot.JDABot;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class Command implements Runnable {
    protected MessageReceivedEvent event;
    protected TextChannel textChannel;
    protected LinkedList<String> args;
    public static String description = "",
            argsDescription = "";
    protected JDABot jdaBot;
    protected Message message;

    public Command(JDABot jdaBot, MessageReceivedEvent event, LinkedList<String> args) {
        this.event = event;
        this.textChannel = jdaBot.getJda().getTextChannelById(event.getChannel().getId());
        this.args = args;
        this.jdaBot = jdaBot;
        this.message=new Message(textChannel);
    }

    final public void run() {
        buildMessage();
        message.send();
    }

    protected abstract void buildMessage();

    final protected ArrayList<Member> extractOnlineUsers(List<Member> users) {
        String botName = jdaBot.getJda().getSelfUser().getName();
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
