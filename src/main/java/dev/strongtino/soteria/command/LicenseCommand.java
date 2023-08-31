package dev.strongtino.soteria.command;

import dev.strongtino.soteria.Soteria;
import dev.strongtino.soteria.license.License;
import dev.strongtino.soteria.license.LicenseService;
import dev.strongtino.soteria.software.Software;
import dev.strongtino.soteria.software.SoftwareService;
import dev.strongtino.soteria.util.JDAUtil;
import dev.strongtino.soteria.util.StringUtil;
import dev.strongtino.soteria.util.TimeUtil;
import dev.strongtino.soteria.util.command.Command;
import dev.strongtino.soteria.util.command.CommandType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LicenseCommand extends Command {

    private final MessageEmbed usage = JDAUtil.createEmbed(Color.RED, "Available commands",
            "/license create <user> <software> [duration]",
            "/license revoke <license> or <user> <software>",
            "/license check <license> or <user> or <software>",
            "/license list [page]");

    private final LicenseService licenseService = Soteria.INSTANCE.getLicenseService();
    private final SoftwareService softwareService = Soteria.INSTANCE.getSoftwareService();
    
    public LicenseCommand() {
        super("license", Arrays.asList("licence", "licenses", "licences"), Permission.ADMINISTRATOR, CommandType.GUILD);

        setAsync(true);
    }

    @Override
    public void execute(Member member, TextChannel channel, Message message, String[] args) {
        if (args.length == 0 || args.length > 4) {
            channel.sendMessage(usage).queue();
            return;
        }
        switch (args[0].toLowerCase()) {
            case "create":
                if (args.length < 3) {
                    channel.sendMessage(usage).queue();
                    return;
                }
                Software software = softwareService.getSoftware(args[2]);

                if (software == null) {
                    channel.sendMessage(JDAUtil.createEmbed(Color.RED, "License Error", "Software with the name `" + args[2] + "` doesn't exist.")).queue();
                    return;
                }
                License license = licenseService.getLicenseByUserAndSoftware(args[1], software.getName());

                if (license != null) {
                    channel.sendMessage(JDAUtil.createEmbed(Color.RED, "License Error", "License with the user `" + license.getUser() + "` for the software `" + license.getSoftware() + "` already exists.")).queue();
                    return;
                }
                license = licenseService.createLicense(args[1], args[2], args.length == 3 ? Long.MAX_VALUE : TimeUtil.getDuration(args[3]));

                EmbedBuilder embed = JDAUtil.embedBuilder(Color.ORANGE, "New License");

                embed.addField("Key", license.getKey(), false);
                embed.addField("User", license.getUser(), false);
                embed.addField("Software", license.getSoftware(), false);
                embed.addField("Duration", TimeUtil.formatDuration(license.getDuration()), false);
                embed.addField("Created At", TimeUtil.formatDate(license.getCreatedAt()), false);

                channel.sendMessage(embed.build()).queue();
                break;
            case "revoke":
                if (args.length == 1) {
                    channel.sendMessage(usage).queue();
                    return;
                }
                if (args.length == 2) {
                    license = licenseService.getLicenseByKey(args[1]);
                } else {
                    license = licenseService.getLicenseByUserAndSoftware(args[1], args[2]);
                }
                if (license == null) {
                    channel.sendMessage(JDAUtil.createEmbed(Color.RED, "License Error", "License with the input attributes doesn't exist.")).queue();
                    return;
                }
                licenseService.revokeLicense(license);
                licenseService.removeLicenseFromMap(license.getKey());

                channel.sendMessage(JDAUtil.createEmbed(Color.YELLOW, "License Revoked", "License with the key `" + license.getKey() + "` of user `"
                        + license.getUser() + "` for the software `" + license.getSoftware() + "` has been revoked and is no longer usable.")).queue();
                break;
            case "check":
                String input = args[1];

                embed = JDAUtil.embedBuilder(Color.ORANGE, "License Lookup");
                license = licenseService.getLicenseByKey(input);

                if (license != null) {
                    embed.addField("Key", license.getKey(), false);

                    embed.appendDescription("User: `" + license.getUser() + '`'
                            + "\nSoftware: `" + license.getSoftware() + '`'
                            + "\nDuration: `" + TimeUtil.formatDuration(license.getDuration()) + '`'
                            + "\nExpires in: `" + license.getExpirationTime() + '`'
                            + "\nCreated at: `" + TimeUtil.formatDate(license.getCreatedAt()) + '`'
                    );
                    channel.sendMessage(embed.build()).queue();
                    return;
                }
                software = softwareService.getSoftware(input);

                if (software != null) {
                    embed.appendDescription("There is a total of `" + licenseService.getLicensesBySoftware(software).size() + "` active licenses for this software.");
                    channel.sendMessage(embed.build()).queue();
                    return;
                }
                List<License> licenses = licenseService.getLicensesByUser(input);

                if (!licenses.isEmpty()) {
                    embed.addField("User", input, false);
                    embed.addField("Active Licenses", licenses.stream().map(l -> '`' + l.getKey() + "` (" + l.getSoftware() + ")\n").collect(Collectors.joining()), false);

                    channel.sendMessage(embed.build()).queue();
                    return;
                }
                channel.sendMessage(JDAUtil.createEmbed(Color.RED, "License Error", "There is no license information for the received input.")).queue();
                break;
            case "list":
                int page = args.length == 1 || !StringUtil.isInteger(args[1]) ? 1 : Integer.parseInt(args[1]);
                int elementsPerPage = 5;

                List<List<License>> pages = StringUtil.sliceList(licenseService.getActiveLicenses(), elementsPerPage);

                if (page < 1 || page > pages.size()) {
                    channel.sendMessage(JDAUtil.createEmbed(Color.RED, "Invalid page", pages.isEmpty() ? "There are no licenses created yet." : "That page is not found.")).queue();
                    return;
                }
                List<License> elements = pages.get(page - 1);

                embed = JDAUtil.embedBuilder(Color.ORANGE, "Licenses (Page " + page + '/' + pages.size() + ')');

                for (int i = 0; i < elements.size(); i++) {
                    license = elements.get(i);

                    embed.addField((i + 1 + ((page - 1) * elementsPerPage)) + ". license",
                            "Key: `" + license.getKey() + "`"
                                    + "\nUser: `" + license.getUser() + "`"
                                    + "\nSoftware: `" + license.getSoftware() + "`",
                            false
                    );
                }
                channel.sendMessage(embed.build()).queue();
                break;
            default:
                channel.sendMessage(usage).queue();
        }
    }
}
