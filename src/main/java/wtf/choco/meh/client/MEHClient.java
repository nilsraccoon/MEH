package wtf.choco.meh.client;

import java.util.function.Consumer;
import java.util.regex.Pattern;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.multiplayer.ServerData;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.include.com.google.common.base.Preconditions;

import wtf.choco.meh.client.chat.ChatChannelsFeature;
import wtf.choco.meh.client.chat.ManualGGFeature;
import wtf.choco.meh.client.config.MEHConfig;
import wtf.choco.meh.client.pksim.PKSim3Feature;
import wtf.choco.meh.client.scoreboard.HypixelScoreboard;

public final class MEHClient implements ClientModInitializer {

    public static final String MOD_ID = "meh";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // [*.]hypixel.net
    private static final Pattern PATTERN_HYPIXEL_IP = Pattern.compile("^(?:\\w+\\.)?hypixel\\.net$", Pattern.CASE_INSENSITIVE);

    private boolean connectedToHypixel = false;
    private HypixelScoreboard hypixelScoreboard = null;

    private static MEHClient instance;
    private static ConfigHolder<MEHConfig> config;

    private ChatChannelsFeature chatChannelsFeature;
    private PKSim3Feature pkSimFeature;

    @Override
    public void onInitializeClient() {
        instance = this;

        MEHKeybinds.init();

        AutoConfig.register(MEHConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(MEHConfig.class);

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            this.connectedToHypixel = false;
            this.setHypixelScoreboard(null);
        });
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            ServerData server = client.getCurrentServer();
            this.connectedToHypixel = (server != null) && PATTERN_HYPIXEL_IP.matcher(server.ip).matches();
            if (connectedToHypixel) {
                this.setHypixelScoreboard(new HypixelScoreboard(), scoreboard -> scoreboard.setAutoRefreshInterval(20));
            }
        });

        this.chatChannelsFeature = new ChatChannelsFeature(this);
        this.pkSimFeature = new PKSim3Feature(this);

        new ManualGGFeature(this);

        /*
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(ClientCommandManager.literal("testscoreboardlines")
                .executes(context -> {
                    return 1;
                })
            ));
        }
        */
    }

    public boolean isConnectedToHypixel() {
        return connectedToHypixel || FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    private void setHypixelScoreboard(@NotNull HypixelScoreboard scoreboard, @NotNull Consumer<HypixelScoreboard> action) {
        Preconditions.checkArgument(scoreboard != null, "scoreboard must not be null");
        this.setHypixelScoreboard(scoreboard);
        action.accept(scoreboard);
    }

    private void setHypixelScoreboard(@Nullable HypixelScoreboard scoreboard) {
        if (hypixelScoreboard != null) {
            this.hypixelScoreboard.dispose();
        }

        this.hypixelScoreboard = scoreboard;
    }

    @Nullable
    public HypixelScoreboard getHypixelScoreboard() {
        return hypixelScoreboard;
    }

    public ChatChannelsFeature getChatChannelsFeature() {
        return chatChannelsFeature;
    }

    public PKSim3Feature getPKSimFeature() {
        return pkSimFeature;
    }

    public static MEHClient getInstance() {
        return instance;
    }

    public static MEHConfig getConfig() {
        return config.getConfig();
    }

}
