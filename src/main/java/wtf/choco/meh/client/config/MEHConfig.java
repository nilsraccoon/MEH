package wtf.choco.meh.client.config;

import java.util.ArrayList;
import java.util.List;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

import net.minecraft.Util;

import wtf.choco.meh.client.MEHClient;

@Config(name = MEHClient.MOD_ID)
public final class MEHConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.RequiresRestart
    private List<KnownChannel> known_channels = Util.make(new ArrayList<>(), channels -> {
        channels.add(new KnownChannel("party", "Party", 0x84C5DB, "pc"));
        channels.add(new KnownChannel("guild", "Guild", 0xEB3A09, "gc"));
    });

    public List<KnownChannel> getKnownChannels() {
        return known_channels;
    }

    @Override
    public void validatePostLoad() throws ValidationException {
        for (KnownChannel channel : known_channels) {
            if (channel.command_prefix.strip().equals("/")) {
                throw new ValidationException("Command prefix must not be a single slash character!");
            }

            if (channel.command_prefix.isBlank()) {
                throw new ValidationException("Command prefix must not be empty!");
            }
        }
    }

    public static final class KnownChannel {

        private String id;
        private String name;

        @ConfigEntry.ColorPicker
        private int color;

        @ConfigEntry.Gui.Tooltip
        private String command_prefix;

        public KnownChannel(String id, String name, int color, String commandPrefix) {
            this.id = id;
            this.name = name;
            this.color = color;
            this.command_prefix = commandPrefix;
        }

        public KnownChannel() {
            this("unknown", "Unknown", 0, "");
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getColor() {
            return color;
        }

        public String getCommandPrefix() {
            return command_prefix;
        }

    }

}
