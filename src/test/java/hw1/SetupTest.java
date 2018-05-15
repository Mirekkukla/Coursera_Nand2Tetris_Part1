package hw1;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SetupTest {

    @Test
    public void shouldFail() {
        assertEquals(false, true);
    }

    @Test
    public void shouldSucceed() {
        assertEquals(true, true);
    }

}