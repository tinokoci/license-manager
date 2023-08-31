package dev.strongtino.soteria.command;

import dev.strongtino.soteria.Soteria;
import dev.strongtino.soteria.license.License;
import dev.strongtino.soteria.license.LicenseService;
import dev.strongtino.soteria.license.request.Request;
import dev.strongtino.soteria.license.request.RequestService;
import dev.strongtino.soteria.util.JDAUtil;
import dev.strongtino.soteria.util.StringUtil;
import dev.strongtino.soteria.util.command.Command;
import dev.strongtino.soteria.util.command.CommandType;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestsCommand extends Command {

    private final RequestService requestService = Soteria.INSTANCE.getRequestService();
    private final LicenseService licenseService = Soteria.INSTANCE.getLicenseService();

    public RequestsCommand() {
        super("requests", Permission.ADMINISTRATOR, CommandType.GUILD);

        setAsync(true);
    }

    @Override
    public void execute(Member member, TextChannel channel, Message message, String[] args) {
        if (args.length == 0 || args.length > 2) {
            sendUsage(channel);
            return;
        }
        if (args.length == 1) {
            License license = licenseService.getLicenseByKey(args[0]);

            if (license != null) {
                handleLicenseRequests(license, channel);
                return;
            }
            List<Request> requests = requestService.getRequestsByAddress(args[0]);

            if (requests.isEmpty()) {
                channel.sendMessage(JDAUtil.createEmbed(Color.RED, "Requests Error", "There are no recorded GET requests on the received IP address.")).queue();
                return;
            }
            EmbedBuilder embed = JDAUtil.embedBuilder(Color.ORANGE, "Requests Lookup");

            embed.addField("IP address", args[0], false);
            embed.addField("Total requests", String.valueOf(requests.size()), true);
            embed.addField("Recent requests", String.valueOf(requestService.getRecentRequests().stream().filter(request -> request.getAddress().equals(args[0])).count()), true);

            embed.addField("Requests per software", Soteria.INSTANCE.getSoftwareService().getSoftware().stream().map(software -> {
                List<Request> softwareRequests = requestService.getRequestsBySoftware(software).stream().filter(request -> request.getAddress().equals(args[0])).collect(Collectors.toList());

                return '`' + software.getName() + "` - " + StringUtil.formatInteger(softwareRequests.size()) + " total, " + StringUtil.formatInteger((int) softwareRequests.stream().filter(Request::isRecent).count()) + " recent\n";
            }).collect(Collectors.joining()), false);

            channel.sendMessage(embed.build()).queue();
            return;
        }
        License license = licenseService.getLicenseByUserAndSoftware(args[0], args[1]);

        if (license == null) {
            channel.sendMessage(JDAUtil.createEmbed(Color.RED, "License Error", "License with the input attributes doesn't exist.")).queue();
            return;
        }
        handleLicenseRequests(license, channel);
    }

    private void handleLicenseRequests(License license, TextChannel channel) {
        List<Request> requests = requestService.getRequestsByKey(license.getKey());
        EmbedBuilder embed = JDAUtil.embedBuilder(Color.ORANGE, "Requests Lookup");

        embed.addField("License", license.getKey(), false);
        embed.addField("Total requests", String.valueOf(requests.size()), true);
        embed.addField("Recent requests", String.valueOf(requestService.getRecentRequests(license.getKey()).size()), true);

        Map<String, Response> responses = new HashMap<>();

        requests.forEach(request -> {
            Response response = responses.get(request.getAddress());

            if (response == null) {
                responses.put(request.getAddress(), response = new Response());
            }
            response.incrementTotal();

            if (request.isRecent()) {
                response.incrementRecent();
            }
        });
        embed.addField("Requests per IP address", responses.isEmpty() ? "None" : responses.entrySet().stream().map(entry -> '`' + entry.getKey() + "` - "
                + StringUtil.formatInteger(entry.getValue().getTotal()) + " total, " + StringUtil.formatInteger(entry.getValue().getRecent()) + " recent\n").collect(Collectors.joining()), false);

        channel.sendMessage(embed.build()).queue();
    }

    private void sendUsage(TextChannel channel) {
        channel.sendMessage(JDAUtil.createEmbed(Color.RED, "Invalid usage", "Usage: /requests <address> or <key> or <user> <software>")).queue();
    }

    @Getter
    private static class Response {

        private int total;
        private int recent;

        public void incrementTotal() {
            total++;
        }

        public void incrementRecent() {
            recent++;
        }
    }
}
