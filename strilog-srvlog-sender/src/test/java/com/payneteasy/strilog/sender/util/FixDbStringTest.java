package com.payneteasy.strilog.sender.util;

import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static com.payneteasy.strilog.sender.util.FixDbString.fixDbString;
import static org.assertj.core.api.Assertions.assertThat;

public class FixDbStringTest {

    @Test
    public void test() {
        assertThat(fixDbString(null, 4)).isNull();
        assertThat(fixDbString("", 4)).isEqualTo("");
        assertThat(fixDbString(" ", 4)).isEqualTo(" ");
        assertThat(fixDbString("1234", 4)).isEqualTo("1234");
        assertThat(fixDbString("12345", 4)).isEqualTo("1234");
        System.out.println("П".getBytes(StandardCharsets.UTF_8).length);
        assertThat(fixDbString("Привет", 4)).isEqualTo("Пр");

        // 🇫🇷
        assertThat(fixDbString("Flag \uD83C\uDDEB\uD83C\uDDF7 removed", 100)).isEqualTo("Flag  removed");
        assertThat(fixDbString("Flag \uD83C\uDDEB\uD83C\uDDF7 removed", 14)).isEqualTo("Flag  removed");
    }
}
