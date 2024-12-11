package wtf.choco.meh.client.game.housing;

import net.minecraft.client.Minecraft;

import org.jetbrains.annotations.Nullable;

import wtf.choco.meh.client.MEHClient;
import wtf.choco.meh.client.config.MEHConfig;
import wtf.choco.meh.client.event.HypixelServerEvents;
import wtf.choco.meh.client.feature.Feature;
import wtf.choco.meh.client.server.HypixelServerType;

public final class AutoDisableHousingFlightFeature extends Feature {

    public AutoDisableHousingFlightFeature(MEHClient mod) {
        super(mod, MEHConfig::getAutoDisableHousingFlightConfig);
    }

    @Override
    protected void registerListeners() {
        HypixelServerEvents.SERVER_LOCATION_CHANGED.register(this::onServerLocationChange);
    }

    @SuppressWarnings("unused")
    private void onServerLocationChange(HypixelServerType serverType, boolean lobby, @Nullable HypixelServerType fromServerType, boolean fromLobby) {
        if (!isEnabled()) {
            return;
        }

        if (serverType != HypixelServerType.HOUSING || lobby) {
            return;
        }

        // Don't turn off flight if the player is already flying
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player.getAbilities().flying) {
            return;
        }

        minecraft.player.connection.sendCommand("fly");
    }

}
