package ru.shalomander.jda_bot.base;

import ru.shalomander.jda_bot.JDABot;

import java.util.TimerTask;

public abstract class Task extends TimerTask {
    protected JDABot jdaBot;

    public Task(JDABot jdaBot) {
        this.jdaBot = jdaBot;
    }

    public abstract void run();
}
