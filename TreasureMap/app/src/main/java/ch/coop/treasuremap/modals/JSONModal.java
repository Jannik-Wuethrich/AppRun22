package ch.coop.treasuremap.modals;

import java.util.ArrayList;
import java.util.List;

public class JSONModal {
    List<LocationModal> locations = new ArrayList<>();

    public List<LocationModal> getLocations() {
        return locations;
    }

    public void setLocations(List<LocationModal> locations) {
        this.locations = locations;
    }
}
