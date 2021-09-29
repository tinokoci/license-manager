package dev.strongtino.soteria.util.command;


import dev.strongtino.soteria.util.Task;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class Command extends ListenerAdapter {

    public final static char PREFIX = '/';

    private final String command;
    private final List<String> aliases;
    private final Permission permission;
    private final CommandType commandType;

    public Guild guild;

    @Setter
    private boolean async;

    public Command(CommandType commandType) {
        this(null, new ArrayList<>(), null, commandType);
    }

    public Command(Permission permission, CommandType commandType) {
        this(null, new ArrayList<>(), permission, commandType);
    }

    public Command(String command, CommandType commandType) {
        this(command, new ArrayList<>(), null, commandType);
    }

    public Command(String command, List<String> aliases, CommandType commandType) {
        this(command, aliases, null, commandType);
    }

    public Command(String command, Permission permission, CommandType commandType) {
        this(command, new ArrayList<>(), permission, commandType);
    }

    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (commandType != CommandType.GUILD) return;

        Message message = event.getMessage();
        TextChannel channel = event.getChannel();
        Member member = message.getMember();

        if (member == null
                || member.getUser().isBot()
                || (permission != null && !member.hasPermission(permission)))
            return;

        String[] args = getArguments(message);

        if (args == null) return;

        guild = message.getGuild();

        if (async) Task.async(() -> execute(member, channel, message, args));
        else execute(member, channel, message, args);
    }

    public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {
        if (commandType != CommandType.PRIVATE) return;

        Message message = event.getMessage();
        PrivateChannel channel = event.getChannel();
        User user = event.getAuthor();

        if (user.isBot()) return;

        String[] args = getArguments(message);

        if (args == null) return;

        if (async) Task.async(() -> execute(user, channel, message, args));
        else execute(user, channel, message, args);
    }

    private String[] getArguments(Message message) {
        String[] originalArray = message.getContentRaw().split(" ");
        String[] newArray = Arrays.copyOfRange(originalArray, command == null ? 0 : 1, originalArray.length);

        if (command == null
                || originalArray[0].equalsIgnoreCase(PREFIX + command)
                || (!aliases.isEmpty() && aliases.stream().anyMatch(alias -> originalArray[0].equalsIgnoreCase(PREFIX + alias)))) {
            return newArray;
        }
        return null;
    }

    public void execute(Member member, TextChannel channel, Message message, String[] args) {}

    public void execute(User user, PrivateChannel channel, Message message, String[] args) {}
}