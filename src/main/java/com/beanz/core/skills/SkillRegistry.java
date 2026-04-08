package com.beanz.core.skills;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

public final class SkillRegistry {
    private static final Map<SkillType, SkillDefinition> DEFINITIONS = new EnumMap<>(SkillType.class);

    static {
        register(new SkillDefinition(SkillType.JUMP, "Jump"));
    }

    private SkillRegistry() {
    }

    public static SkillDefinition get(SkillType skillType) {
        SkillDefinition definition = DEFINITIONS.get(skillType);

        if (definition == null) {
            throw new IllegalArgumentException("No skill definition registered for " + skillType);
        }

        return definition;
    }

    public static Collection<SkillDefinition> all() {
        return DEFINITIONS.values();
    }

    private static void register(SkillDefinition definition) {
        DEFINITIONS.put(definition.type(), definition);
    }
}
