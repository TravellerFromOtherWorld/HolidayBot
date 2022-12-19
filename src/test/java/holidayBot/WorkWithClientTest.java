/*package holidayBot;

import java.io.File;

import org.junit.jupiter.api.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

class WorkWithClientTest {
    private Logic logic;
    private MessageFromBot messageFromBot;
    private WorkWithClient testWorkWithClient;

    public static StringBuilder readFile(String path) {
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
        logic = new Logic();
        messageFromBot = new MessageFromBot();
    }

    @BeforeEach
    void testTryRegister() {
        testWorkWithClient.tryRegister("гарри", "гриффиндор");
        int user1 = readFile("Users.txt").indexOf("гарри:гриффиндор");
        Assertions.assertNotEquals(-1, user1);

    }

    @Test
    public void testTryEnter() {
        testWorkWithClient.tryEnter("кот", "Бонифаций");
        int user1 = readFile("Users.txt").indexOf("кот:Бонифаций");
        Assertions.assertEquals(-1, user1);
        testWorkWithClient.tryEnter("гарри", "гриффиндор");
        int user2 = readFile("Users.txt").indexOf("гарри:гриффиндор");
        Assertions.assertNotEquals(-1, user2);
    }

    @Test
    @DisplayName("Should Not Enter And Register When Login is Null")
    public void shouldNotEnterAndRegisterWhenLoginIsNull() {
        messageFromBot = logic.clientAuthentication("", "грифиндор");
        Assertions.assertEquals("Заполните, пожалуйста, оба поля!",messageFromBot.getMessage());
    }

    @Test
    @DisplayName("Should Not Enter And Register When Password is Null")
    public void shouldNotEnterAndRegisterWhenPassIsNull() {
        messageFromBot = logic.clientAuthentication("гарри", "");
        Assertions.assertEquals("Заполните, пожалуйста, оба поля!",messageFromBot.getMessage());
    }

    @Test
    @DisplayName("Should Not Enter And Register When Password and Login Are Null")
    public void shouldNotEnterAndRegisterWhenPassAndLoginAreNullRegister() {
        messageFromBot = logic.clientAuthentication("", "");
        Assertions.assertEquals("Заполните, пожалуйста, оба поля!",messageFromBot.getMessage());
    }

}
*/