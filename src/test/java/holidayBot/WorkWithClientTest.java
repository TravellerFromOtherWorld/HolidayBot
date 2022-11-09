package holidayBot;
import java.io.File;
import org.junit.jupiter.api.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

class WorkWithClientTest {

    private WorkWithClient testWorkWithClient;

    public static StringBuilder readFile(String path)
    {
        File file = new File(path);
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return builder;
    }

    @BeforeEach
    public void setup() {
        testWorkWithClient = new WorkWithClient();
    }

    @Test
    public void testTryEnter() {
        testWorkWithClient.tryEnter("кот","Бонифаций");
        int user1 = readFile("Users.txt").indexOf("кот Бонифаций");
        Assertions.assertNotEquals(-1, user1);
        testWorkWithClient.tryEnter("sdfvtgnhj","sdcvftyghkl");
        int user2 = readFile("Users.txt").indexOf("sdfvtgnhj sdcvftyghkl");
        Assertions.assertEquals(-1, user2);
    }

    @Test
    @DisplayName("Should Not Enter When Login is Null")
    public void shouldThrowRuntimeExceptionWhenLoginIsNullEnter() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            testWorkWithClient.tryEnter(null, "Бонифаций");
        });
    }

    @Test
    @DisplayName("Should Not Enter When Password is Null")
    public void shouldThrowRuntimeExceptionWhenPassIsNullEnter() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            testWorkWithClient.tryEnter("кот", null);
        });
    }

    @Test
    void testTryRegister() {
        testWorkWithClient.tryRegister("кот","Бонифаций");
        int user1 = readFile("Users.txt").indexOf("кот Бонифаций");
        Assertions.assertEquals(0, user1);
        testWorkWithClient.tryRegister("sdfvtgnhj","sdcvftyghkl");
        int user2 = readFile("Users.txt").indexOf("sdfvtgnhj sdcvftyghkl");
        Assertions.assertNotEquals(-1, user2);

    }

    @Test
    @DisplayName("Should Not Register When Login is Null")
    public void shouldThrowRuntimeExceptionWhenLoginIsNullRegister() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            testWorkWithClient.tryRegister(null, "Бонифаций");
        });
    }

    @Test
    @DisplayName("Should Not Register When Password is Null")
    public void shouldThrowRuntimeExceptionWhenPassIsNullRegister() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            testWorkWithClient.tryEnter("кот", null);
        });
    }

    @Test
    @DisplayName("Should Not Register When Password and Login Are Null")
    public void shouldThrowRuntimeExceptionWhenPassAndLoginAreNullRegister() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            testWorkWithClient.tryEnter(null, null);
        });
    }
}
