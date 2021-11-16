package org.coding.yifany.runtime.bash.function;

import org.apache.pulsar.functions.api.SerDe;
import org.coding.yifany.runtime.bash.common.Constants;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BashRuntimeSerDe implements SerDe<List<String>> {

    public List<String> deserialize(byte[] input) {
        return Arrays.stream(new String(input).split(Constants.NEW_LINE_UNIX)).collect(Collectors.toList());
    }


    public byte[] serialize(List<String> input) {
        return String.join(Constants.NEW_LINE_UNIX, input).getBytes();
    }
}
