package org.coding.yifany.runtime.bash.function;

import org.coding.yifany.runtime.bash.common.Constants;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(JUnit4.class)
public class BashRuntimeSerDeTest {

    @Test
    public void serialize() {
        String resp1 = "command-response-1";
        String resp2 = "command-response-2";

        List<String> input =
                new ArrayList<>(Arrays.asList(resp1, resp2));
        BashRuntimeSerDe
                serDe = new BashRuntimeSerDe();
        byte[] actual = serDe.serialize(input);

        Assert.assertArrayEquals(String.format("%s%s%s", resp1, Constants.NEW_LINE_UNIX, resp2).getBytes(), actual);
    }

    @Test
    public void deserialize() {
        String resp1 = "command-response-1";
        String resp2 = "command-response-2";

        byte[] input = String.format("%s%s%s", resp1, Constants.NEW_LINE_UNIX, resp2).getBytes();

        BashRuntimeSerDe
                serDe = new BashRuntimeSerDe();
        List<String> actual = serDe.deserialize(input);

        Assert.assertEquals(Arrays.asList(resp1, resp2), actual);
    }
}