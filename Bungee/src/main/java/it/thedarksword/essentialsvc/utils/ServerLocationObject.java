package it.thedarksword.essentialsvc.utils;

import it.thedarksword.essentialsvc.objets.LocationObject;
import lombok.Getter;

@Getter
public class ServerLocationObject extends LocationObject {

    private final String server;

    public ServerLocationObject(String server, String world, double x, double y, double z, float yaw, float pitch) {
        super(world, x, y, z, yaw, pitch);
        this.server = server;
    }

    public ServerLocationObject(String server, LocationObject location) {
        this(server, location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }
}
