/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.client.net.server;

import illarion.client.net.CommandList;
import illarion.client.net.annotations.ReplyMessage;
import illarion.client.world.Weather;
import illarion.client.world.World;
import illarion.common.net.NetCommReader;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Server message: Update of the current weather
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@ReplyMessage(replyId = CommandList.MSG_WEATHER)
public final class WeatherMsg implements ServerReply {
    /**
     * The new value for the clouds.
     */
    private short clouds;

    /**
     * The new value for the fog.
     */
    private short fog;

    /**
     * The new value for the gust strength.
     */
    private short gusts;

    /**
     * The new value for the lightning intensity.
     */
    private short lightning;

    /**
     * The new value for the precipitation strength.
     */
    private short precipitation;

    /**
     * The new value for the precipitation type.
     */
    private short precType;

    /**
     * The new value for the temperature.
     */
    private byte temperature;

    /**
     * The new wind value.
     */
    private byte wind;

    @Override
    public void decode(@NotNull NetCommReader reader) throws IOException {
        clouds = reader.readUByte();
        fog = reader.readUByte();
        wind = reader.readByte();
        gusts = reader.readUByte();
        precipitation = reader.readUByte();
        precType = reader.readUByte();
        lightning = reader.readUByte();
        temperature = reader.readByte();
    }

    @NotNull
    @Override
    public ServerReplyResult execute() {
        Weather weather = World.getWeather();
        weather.setFog(fog);
        weather.setLightning(0); //TODO: lightning);
        weather.setPrecipitation(precType, precipitation);
        weather.setWind(wind, gusts);
        weather.setCloud(clouds);
        return ServerReplyResult.Success;
    }

    @NotNull
    @Override
    @Contract(pure = true)
    public String toString() {
        return Utilities.toString(WeatherMsg.class, "clouds: " + clouds, "fog: " + fog, "wind: " + wind,
                "gusts: " + gusts, "precipitation: " + precipitation, "precType: " + precType,
                "lightning: " + lightning, "temperature: " + temperature);
    }
}
