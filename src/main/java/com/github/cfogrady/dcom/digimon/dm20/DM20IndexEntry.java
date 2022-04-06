package com.github.cfogrady.dcom.digimon.dm20;

import com.github.cfogrady.dcom.digimon.Attribute;
import com.github.cfogrady.dcom.digimon.Stage;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DM20IndexEntry {
    private final int index;
    private final Stage stage;
    private final Attribute attribute;
    private final int power;
    private final int hashedLowerCaseName;
}
