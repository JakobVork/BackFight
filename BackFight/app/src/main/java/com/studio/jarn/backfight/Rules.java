package com.studio.jarn.backfight;

import java.util.ArrayList;

class Rules {
    String rulesName;
    ArrayList<String> rulesDescription;

    Rules(String rulesName, ArrayList<String> description) {
        this.rulesName = rulesName;
        this.rulesDescription = description;
    }

    String getRulesName() {
        return rulesName;
    }
}
