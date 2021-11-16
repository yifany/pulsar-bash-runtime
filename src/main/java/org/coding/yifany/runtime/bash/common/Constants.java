package org.coding.yifany.runtime.bash.common;

import org.apache.commons.lang3.StringUtils;
import org.apache.pulsar.functions.api.Context;

public class Constants {

    public static final String NEW_LINE_UNIX = "\n";

    public static final String SCRIPT_INPUT_PATH = "scriptInputPath";
    public static final String SCRIPT_OUTPUT_PATH = "scriptOutputPath";

    public static String getEnvOrElseException(String envKey, Context context) {
        String getOrElse = getEnvOrElse(envKey, null, context);
        if (StringUtils.isEmpty(getOrElse)) {
            throw new IllegalArgumentException("Environment variable [" + envKey + "] expected, found NULL(empty) value instead");
        }
        return getOrElse;
    }

    public static String getEnvOrElse(String envKey, String els, Context context) {
        return (String) context.getUserConfigValueOrDefault(envKey, els);
    }
}
