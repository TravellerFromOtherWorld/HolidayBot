/*package holidayBot;

import java.io.*;
import java.time.LocalDate;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class WorkWithHolidayTest {

    private WorkWithHoliday testWorkWithHoliday;
    private MessageFromBot messageFromBot;
    private WorkWithClient workWithClient;
    private Logic logic;

    public static StringBuilder readFile(String path) {
        File file = new File(path);
        StringBuilder builder = new StringBuilder();
        if (!file.exists()) {
            throw new RuntimeException("File not found");
        }
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
    public void theDestinedOne(){
        workWithClient.tryRegister("гарри","гриффиндор");
    }

    @BeforeEach
    public void setup() {
        testWorkWithHoliday = new WorkWithHoliday();
        workWithClient = new WorkWithClient();
        logic = new Logic();
        messageFromBot = new MessageFromBot();
    }

    @Test
    public void testAddNewHoliday() {
        testWorkWithHoliday.addNewHoliday("2022-11-11", "кража философского камня", "гарри", "гриффиндор");
        Assertions.assertFalse(readFile("Holidays.txt").length() == 0);
        int index = readFile("Holidays.txt").indexOf("гарри:гриффиндор:2022-11-11:кража философского камня");
        Assertions.assertNotEquals(-1, index);
    }

    @Test
    @DisplayName("Should Not Add New Holiday When Date is Empty")
    public void shouldNotAddNewHolidayWhenDateIsEmpty() {
        logic.clientAuthentication("дмитрий", "лжедмитрий");
        logic.newHoliday("", "Сбор Дани");
        int index = readFile("Holidays.txt").indexOf("дмитрий:лжедмитрий::Cбор Дани");
        Assertions.assertEquals(-1, index);
    }

    @Test
    @DisplayName("Should Not Add New Holiday When Name is Empty")
    public void shouldNotAddNewHolidayWhenNameIsEmpty() {
        logic.clientAuthentication("дмитрий", "лжедмитрий");
        logic.newHoliday("1605-2-14", "");
        int index = readFile("Holidays.txt").indexOf("дмитрий:лжедмитрий:1605-2-14:");
        Assertions.assertEquals(-1, index);
    }

    @Test
    @DisplayName("Should Not Add New Holiday When Name And Date Are Empty")
    public void shouldNotAddNewHolidayWhenNameAndDateAreEmpty() {
        logic.clientAuthentication("дмитрий", "лжедмитрий");
        logic.newHoliday("", "");
        int index = readFile("Holidays.txt").indexOf("дмитрий:лжедмитрий::");
        Assertions.assertEquals(-1, index);
    }

    @Test
    void remindHoliday() {
        testWorkWithHoliday.addNewHoliday(String.valueOf(LocalDate.now()), "сбор команды по квиддичу", "гарри", "гриффиндор");
        messageFromBot= testWorkWithHoliday.remindHoliday("гарри", "гриффиндор", LocalDate.now());
        Assertions.assertFalse(messageFromBot.isEmpty());
    }

    @DisplayName("Date should match the required Format True")
    @ParameterizedTest
    @CsvSource({"2022-11-09", "2000-12-12", "2011-02-28"})
    public void shouldTestDateFormatTrue(String date) {
        Assertions.assertTrue(testWorkWithHoliday.correctDate(date));
    }

    @DisplayName("Date should match the required Format False")
    @ParameterizedTest
    @CsvSource({"2022-14-99", "2000-2-31", "888-2-1"})
    public void shouldTestDateFormatFalse(String date) {
        Assertions.assertFalse(testWorkWithHoliday.correctDate(date));
    }
}
*/