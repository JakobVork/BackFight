package com.studio.jarn.backfight.Rules;

import java.util.ArrayList;

class Rules {
    final String mRulesName;
    final ArrayList<String> mRulesDescription;

    Rules(String rulesName, ArrayList<String> description) {
        this.mRulesName = rulesName;
        this.mRulesDescription = description;
    }

    String getmRulesName() {
        return mRulesName;
    }
}
