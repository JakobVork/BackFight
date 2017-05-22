package com.studio.jarn.backfight.Rules;

import java.util.ArrayList;

class Rules {
    String mRulesName;
    ArrayList<String> mRulesDescription;

    Rules(String rulesName, ArrayList<String> description) {
        this.mRulesName = rulesName;
        this.mRulesDescription = description;
    }

    String getmRulesName() {
        return mRulesName;
    }
}
