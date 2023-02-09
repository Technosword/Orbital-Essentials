package it.thedarksword.essentialsvc.objets;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class LocationObject {

    private final String world;
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;

    public LocationObject() {
        world = "";
        x = 0;
        y = 0;
        z = 0;
        yaw = 0;
        pitch = 0;
    }
}
