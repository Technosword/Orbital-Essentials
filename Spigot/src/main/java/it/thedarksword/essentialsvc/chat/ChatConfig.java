package it.thedarksword.essentialsvc.chat;

import lombok.Getter;

import java.util.Map;

@Getter
public class ChatConfig {

    private final String global;
    private final int chatDistance;
    private final Map<String, String> groups;

    public ChatConfig(String global, int chatDistance, Map<String, String> groups) {
        this.global = global;
        this.chatDistance = chatDistance;
        this.groups = groups;
    }

    public String getByGroup(String group) {
        return groups.get(group);
    }
}
