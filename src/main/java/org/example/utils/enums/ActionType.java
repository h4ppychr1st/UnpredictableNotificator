package org.example.utils.enums;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum ActionType {
    BLANK, MESSAGE, BUY_COFFEE, GYM, TICKET;
    private static final Pattern REMINDER_PATTERN = Pattern.compile("^reminder([1-9][0-9]{0,2}|1000)$");
    private static final Map<String, ActionType> types = new HashMap<>();

    static {
        types.put("message", MESSAGE);
        types.put("buy_coffee", BUY_COFFEE);
        types.put("gym", GYM);
        types.put("ticket", TICKET);
    }

    public static ActionType getActionType(String actionType) {
        ActionType result = null;
        if (actionType != null) {
            result = types.get(actionType);
        }
        return (result != null) ? result : BLANK;
    }

    public static int getMinutesIfReminder(String actionType) {
        Matcher m = REMINDER_PATTERN.matcher(actionType);
        if (!m.find())
            return 0;
        else
            return Integer.parseInt(m.group(1));
    }
}
