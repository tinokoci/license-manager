package dev.strongtino.soteria.command;

import dev.strongtino.soteria.Soteria;
import dev.strongtino.soteria.software.Software;
import dev.strongtino.soteria.software.SoftwareService;
import dev.strongtino.soteria.util.JDAUtil;
import dev.strongtino.soteria.util.command.Command;
import dev.strongtino.soteria.util.command.CommandType;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.Color;
import java.util.Collection;
import java.util.stream.Collectors;

public class SoftwareCommand extends Command {

    private final SoftwareService service = Soteria.INSTANCE.getSoftwareService();;

    public SoftwareCommand() {
        super("software", Permission.ADMINISTRATOR, CommandType.GUILD);

        setAsync(true);
    }

    @Override
    public void execute(Member member, TextChannel channel, Message message, String[] args) {
        if (args.length == 0 || args.length > 2) {
            sendUsage(channel);
            return;
        }
        switch (args[0].toLowerCase()) {
            case "create":
                if (args.length != 2) {
                    sendUsage(channel);
                    return;
                }
                Software software = service.createSoftware(args[1]);

                if (software == null) {
                    channel.sendMessage(JDAUtil.createEmbed(Color.RED, "Software Error", "Software with the name `" + args[1] + "` already exists.")).queue();
                    return;
                }
                channel.sendMessage(JDAUtil.createEmbed(Color.ORANGE, "New Software", "A new software with the name `" + args[1] + "` has been created.")).queue();
                break;
            case "delete":
                if (args.length != 2) {
                    sendUsage(channel);
                    return;
                }
                if (service.deleteSoftware(args[1])) {
                    channel.sendMessage(JDAUtil.createEmbed(Color.ORANGE, "Software Deleted", "Software with the name `" + args[1] + "` has been deleted.")).queue();
                } else {
                    channel.sendMessage(JDAUtil.createEmbed(Color.RED, "Software Error", "Software with the name `" + args[1] + "` doesn't exists.")).queue();
                }
                break;
            case "list":
                Collection<Software> availableSoftware = service.getSoftware();

                if (availableSoftware.isEmpty()) {
                    channel.sendMessage(JDAUtil.createEmbed(Color.RED, "Software Error", "There is no software created yet.")).queue();
                    return;
                }
                channel.sendMessage(JDAUtil.createEmbed(Color.ORANGE, "Available Software", availableSoftware.stream().map(s -> " - `" + s.getName() + "`\n").collect(Collectors.joining()))).queue();
                break;
            default:
                sendUsage(channel);
        }
    }

    private void sendUsage(TextChannel channel) {
        channel.sendMessage(JDAUtil.createEmbed(Color.RED, "Invalid usage", "Usage: /software <create|delete|list> [name]")).queue();
    }
}
