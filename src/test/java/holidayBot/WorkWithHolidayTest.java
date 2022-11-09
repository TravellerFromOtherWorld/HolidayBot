package holidayBot;
import java.io.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class WorkWithHolidayTest {

    private WorkWithHoliday testWorkWithHoliday;
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
    public void setup() {
        testWorkWithHoliday = new WorkWithHoliday();
        logic = new Logic();
    }

    @Test
    public void testAddNewHoliday() {
        testWorkWithHoliday.addNewHoliday("09.11.2002", "День Чая", "кот", "Бонифаций");
        Assertions.assertFalse(readFile("Holidays.txt").length() == 0);
        int index = readFile("Holidays.txt").indexOf("09.11.2002:День Чая:кот:Бонифаций");
        Assertions.assertNotEquals(-1, index);
    }

    @Test
    @DisplayName("Should Not Add New Holiday When Date is Empty")
    public void shouldNotAddNewHolidayWhenDateIsEmpty() {
        logic.clientAuthentication("дмитрий", "лжедмитрий");
        logic.newHoliday("", "Сбор Дани");
        int index = readFile("Holidays.txt").indexOf(":Сбор Дани:дмитрий:лжедмитрий");
        Assertions.assertEquals(-1, index);
    }

    @Test
    @DisplayName("Should Not Add New Holiday When Name is Empty")
    public void shouldNotAddNewHolidayWhenNameIsEmpty() {
        logic.clientAuthentication("дмитрий", "лжедмитрий");
        logic.newHoliday("12.10.1605", "");
        int index = readFile("Holidays.txt").indexOf("12.10.1605::дмитрий:лжедмитрий");
        Assertions.assertEquals(-1, index);
    }

    @Test
    @DisplayName("Should Not Add New Holiday When Name And Date Are Empty")
    public void shouldNotAddNewHolidayWhenNameAndDateAreEmpty() {
        logic.clientAuthentication("дмитрий", "лжедмитрий");
        logic.newHoliday("", "");
        int index = readFile("Holidays.txt").indexOf("::дмитрий:лжедмитрий");
        Assertions.assertEquals(-1, index);
    }

    @Test
    void remindHoliday() {
        testWorkWithHoliday.addNewHoliday("09.11.2022", "Сбор дани", "кот", "Бонифаций");
        testWorkWithHoliday.remindHoliday("кот", "Бонифаций");
        Assertions.assertTrue(readFile("MarkedHolidays.txt").length() != 0);
    }

    @DisplayName("Date should match the required Format True")
    @ParameterizedTest
    @CsvSource({"9.11.2022", "21.12.2000", "15.8.2011"})
    public void shouldTestDateFormatTrue(String date) {
        Assertions.assertTrue(testWorkWithHoliday.correctDate(date));
    }

    @DisplayName("Date should match the required Format False")
    @ParameterizedTest
    @CsvSource({"89.13.2022", "30.2.2000", "15.8.888"})
    public void shouldTestDateFormatFalse(String date) {
        Assertions.assertFalse(testWorkWithHoliday.correctDate(date));
    }
}
