package com.ericc.the.game.components;

import com.badlogic.ashley.core.Component;
import com.ericc.the.game.agencies.Agency;

public class AgencyComponent implements Component {
    public Agency agency;
    public int initiative;
    public int delay;
    public boolean passive;

    public AgencyComponent(Agency agency, boolean passive) {
        this.agency = agency;
        this.passive = passive;
    }
}
