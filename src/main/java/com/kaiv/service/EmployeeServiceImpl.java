package com.kaiv.service;

import com.kaiv.dao.EmployeeDao;
import com.kaiv.model.*;
import com.monitorjbl.xlsx.StreamingReader;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@ComponentScan({"com.kaiv.config"})
@PropertySource(value = {"classpath:application.properties"})
@Service("EmployeeService")
@Scope(value = "session")
public class EmployeeServiceImpl implements EmployeeService {

    int id;

    @Autowired
    EmployeeDao employeeDao;

    @Value("${TelefoneListFilePath}")
    private String TelListe_FILE_NAME;

    @Value("${MobilePhonesListFilePath}")
    private String MobilePhones_FILE_NAME;

    @Value("${getinfo.GRANTED_AD_GROUPS}")
    private String GRANTED_AD_GROUP;

    @Value("${getinfo.LDAP_USERNAME}")
    String ldapUsername;

    @Value("${getinfo.LDAP_PASSWORD}")
    String ldapPassword;

    @Value("${getinfo.LDAP_SERVER}")
    String servername;

    @Value("${getinfo.USERS_PHOTO_STRYI}")
    String photoLocationStryi;

    @Value("${getinfo.USERS_PHOTO_KOLOMIYA}")
    String photoLocationKolomyia;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    String currentSessionUserName;

    private final ArrayList adGroupPhotosMembers = new ArrayList();

    private Map<Integer, EmployeeKaba> mapFromKabaDb;
    private Map<Integer, List<EmployeeCodesFile>> mapFromExcel;
    private List<EmployeeMobileFile> listFromMobileNumbersFile;
    public Map<Integer, Employee> employeeMap;

    @Override
    @Bean
    @Scope(value = WebApplicationContext.SCOPE_SESSION)
    public boolean isDataLoaded() {
        return employeeMap != null;
    }

    @Override
    @Bean
    @Scope(value = WebApplicationContext.SCOPE_SESSION)
    public Map<Integer, Employee> getEmployeeListNewSession() {
        // TODO
        // boolean isDbAccessible = false;
        boolean isDbAccessible = readFromKabaDb();
        readXlsFile();
        readMobileNumbersFile();
        createPhoneArrayList(isDbAccessible);
        return employeeMap;
    }

    @Bean
    @Scope(value = WebApplicationContext.SCOPE_SESSION)
    public PagedListHolder getPagableList() {
        PagedListHolder<Employee> pagableList = new PagedListHolder<>();
        pagableList.setPageSize(14);

        return pagableList;
    }

    private boolean readFromKabaDb() {

        mapFromKabaDb = new HashMap<>();
        String query = "SELECT PersonalID, FullName, lower([Windows Login]) as AD_Account, lower([E-Mail]) as Email, CostCenter, Position, Department, Plant, Hired, DateOfBirth, [Phone No_] as privatePhoneNum  FROM Personnel_List WHERE Fired IS NULL";
        jdbcTemplate.setQueryTimeout(4);
        try {
            jdbcTemplate.query(
                    query,
                    (rs, rowNum) -> new EmployeeKaba(rs.getInt("PersonalID"), rs.getString("FullName"), rs.getString("AD_Account"), rs.getString("Email"), rs.getString("CostCenter"), rs.getString("Position"), rs.getString("Department"), rs.getString("Plant"), rs.getString("Hired"), rs.getString("DateOfBirth"), rs.getString("privatePhoneNum")))
                    .forEach(employeeKaba ->
                            mapFromKabaDb.put(employeeKaba.getPersNumber(), employeeKaba));
        } catch (Exception exception) {
            return false;
        }
        return true;
    }

    private void readXlsFile() {
        mapFromExcel = new TreeMap<>();

        FileInputStream excelFile = null;

        try {

            excelFile = new FileInputStream(new File(TelListe_FILE_NAME));
            Workbook workbook = StreamingReader.builder()
                    .rowCacheSize(100)    // number of rows to keep in memory (defaults to 10)
                    .bufferSize(4096)     // buffer size to use when reading InputStream to file (defaults to 1024)
                    .open(excelFile);            // InputStream or File for XLSX file (required)
            Sheet datatypeSheet = workbook.getSheet("LCOSx & NAMEs");

            for (Row currentRow : datatypeSheet) {

                int numberInt = 0;
                String number = "";
                int persNumberInt = 0;
                String description = "";
                String descriptionNameOnly = "";
                String nameUkr = "";
                String department = "";
                String plantName = "";
                String costCenter = "";

                try {
                    if (currentRow.getCell(0) != null && currentRow.getCell(0).getCellType() != CellType.BLANK & currentRow.getCell(0).getCellType() == CellType.NUMERIC) {
                        numberInt = (int) currentRow.getCell(0).getNumericCellValue();
                        number = String.valueOf(numberInt);
                    }

                    if (currentRow.getCell(1) != null && currentRow.getCell(1).getCellType() != CellType.BLANK & currentRow.getCell(1).getCellType() == CellType.NUMERIC) {
                        persNumberInt = (int) currentRow.getCell(1).getNumericCellValue();
                    } else if (currentRow.getCell(1) != null && currentRow.getCell(1).getCellType() != CellType.BLANK & currentRow.getCell(1).getCellType() == CellType.STRING) {
                        if (!currentRow.getCell(1).getStringCellValue().contains("Pers"))
                            persNumberInt = Integer.valueOf(currentRow.getCell(1).getStringCellValue());
                    }

                    if (currentRow.getCell(2) != null && currentRow.getCell(2).getCellType() != CellType.BLANK) {
                        nameUkr = currentRow.getCell(2).getStringCellValue().replace("\"", "");
                    }

                    if (currentRow.getCell(3) != null && currentRow.getCell(3).getCellType() != CellType.BLANK) {
                        description = currentRow.getCell(3).getStringCellValue();
                    }

                    if (description.contains(" ")) {
                        descriptionNameOnly = description.replace(description.substring(0, description.indexOf(" ")), "").trim();
                    } else {
                        descriptionNameOnly = description;
                    }

                    if (currentRow.getCell(5) != null && currentRow.getCell(5).getCellType() != CellType.BLANK) {
                        department = currentRow.getCell(5).getStringCellValue();
                    }

                    if (number.startsWith("2")) {
                        plantName = "Stryi";
                    } else {
                        plantName = "Kolomyia";
                    }

                    if (currentRow.getCell(7) != null && currentRow.getCell(7).getCellType() != CellType.BLANK) {
                        costCenter = currentRow.getCell(7).getStringCellValue().replace("\"", "");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


                if (!number.equals("") && (!nameUkr.equals(""))) {
                    if (mapFromExcel.containsKey(persNumberInt)) {
                        List<EmployeeCodesFile> curentList = mapFromExcel.get(persNumberInt);
                        curentList.add(new EmployeeCodesFile(number, persNumberInt, description, descriptionNameOnly, nameUkr, department, plantName, costCenter));
                        mapFromExcel.put(persNumberInt, curentList);

                    } else {
                        List<EmployeeCodesFile> newList = new ArrayList<>();
                        newList.add(new EmployeeCodesFile(number, persNumberInt, description, descriptionNameOnly, nameUkr, department, plantName, costCenter));
                        mapFromExcel.put(persNumberInt, newList);
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                excelFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void readMobileNumbersFile() {

        listFromMobileNumbersFile = new ArrayList<>();
        FileInputStream excelFileWithMobileNumbers = null;

        try {

            excelFileWithMobileNumbers = new FileInputStream(new File(MobilePhones_FILE_NAME));
            Workbook workbook = StreamingReader.builder()
                    .rowCacheSize(100)    // number of rows to keep in memory (defaults to 10)
                    .bufferSize(2048)     // buffer size to use when reading InputStream to file (defaults to 1024)
                    .open(excelFileWithMobileNumbers);            // InputStream or File for XLSX file (required)
            Sheet datatypeSheet = workbook.getSheet("KS_Numbers");

            for (Row currentRow : datatypeSheet) {

                String name = "";
                String number = "";
                String perNumber = "";

                try {
                    if (currentRow.getCell(1) != null && currentRow.getCell(1).getCellType() != CellType.BLANK) {
                        number = currentRow.getCell(1).getStringCellValue();
                    }
                    if (currentRow.getCell(2) != null && currentRow.getCell(2).getCellType() != CellType.BLANK) {
                        name = currentRow.getCell(2).getStringCellValue().replace("\"", "");
                        if (name.contains("[")) {
                            name = name.split("] ")[1];
                        }
                        if (name.contains("-")) {
                            name = name.replaceAll("-", "").trim();
                        }
                    }
                    if (currentRow.getCell(3) != null && currentRow.getCell(3).getCellType() != CellType.BLANK) {
                        perNumber = currentRow.getCell(3).getStringCellValue();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!name.equals("Name")) {
                    listFromMobileNumbersFile.add(new EmployeeMobileFile(perNumber, name, number));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                excelFileWithMobileNumbers.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void createPhoneArrayList(boolean isDbAccessible) {

        employeeMap = new LinkedHashMap<>();

        List<Employee> list = new LinkedList<>();

        if (isDbAccessible) {
            createPhoneArrayListWithInfoFromDb(isDbAccessible);
            list.addAll(employeeMap.values());
            Collections.sort(list, Employee.getListByPerId());
        } else {
            createPhoneArrayListWithoutInfoFromDb(isDbAccessible);
            list.addAll(employeeMap.values());
            Collections.sort(list, Employee.getListByPerId());
        }

        employeeMap.clear();

        for (Employee currentEployee : list) {
            employeeMap.put(currentEployee.getId(), currentEployee);
        }
    }

    private void createPhoneArrayListWithInfoFromDb(boolean isDbAccessible) {
        for (EmployeeKaba oneEmployeeKaba : mapFromKabaDb.values()) {

            String telNumber = "";
            String name = "";
            String department = "";
            String description = "";
            String descriptionNameOnly = "";
            String persNumber = "";
            String plantName = "";
            String login = "";
            String email = "";
            String costCenter = "";
            String position = "";
            String plantFromKaba = "";
            String hiredDate = "";
            String dateOfBirth = "";
            String privatePhoneNum = "";
            int currentEmployeeIdKaba = 0;

            if (oneEmployeeKaba.getPersNumber() != 0) {
                currentEmployeeIdKaba = oneEmployeeKaba.getPersNumber();
            }

            if (oneEmployeeKaba.getFullName() != null) {
                name = oneEmployeeKaba.getFullName();
            }
            if (oneEmployeeKaba.getDepartment() != null) {
                department = oneEmployeeKaba.getDepartment();
            }
            persNumber = String.valueOf(currentEmployeeIdKaba);

            if (oneEmployeeKaba.getLogin() != null) {
                login = oneEmployeeKaba.getLogin();
            }

            if (oneEmployeeKaba.getEmail() != null) {
                email = oneEmployeeKaba.getEmail();
            }
            if (oneEmployeeKaba.getCostCenter() != null) {
                costCenter = oneEmployeeKaba.getCostCenter();
            }
            if (oneEmployeeKaba.getPosition() != null) {
                position = oneEmployeeKaba.getPosition();
            }
            if (oneEmployeeKaba.getPlantName() != null) {
                plantFromKaba = oneEmployeeKaba.getPlantName();
            }

            if (oneEmployeeKaba.getHiredDate() != null) {
                hiredDate = oneEmployeeKaba.getHiredDate().split(" ")[0];
                if (!hiredDate.isEmpty()) {
                    hiredDate = parseDate(hiredDate);
                }
            }

            if (oneEmployeeKaba.getDateOfBirth() != null) {
                dateOfBirth = oneEmployeeKaba.getDateOfBirth().split(" ")[0];
                if (!dateOfBirth.isEmpty()) {
                    dateOfBirth = parseDate(dateOfBirth);
                }
            }

            if (oneEmployeeKaba.getPrivatePhoneNum() != null) {
                privatePhoneNum = oneEmployeeKaba.getPrivatePhoneNum().split(" ")[0];
                if (privatePhoneNum.length() == 10) {
                    privatePhoneNum = "+38" + privatePhoneNum.substring(0, 1) + " " + privatePhoneNum.substring(1, 3) + " " + privatePhoneNum.substring(3);
                }
            }

            if (plantFromKaba.contains("WUAST")) {
                plantName = "Stryi";
            } else if (plantFromKaba.contains("WUAKM")) {
                plantName = "Kolomyia";
            }

            if (mapFromExcel.containsKey(currentEmployeeIdKaba)) {

                for (EmployeeCodesFile oneEmployeeFromCodes : mapFromExcel.get(currentEmployeeIdKaba)) {

                    int currentEmployeeIdExcel = oneEmployeeFromCodes.getPersNumber();

                    if (currentEmployeeIdKaba == currentEmployeeIdExcel) {
                        telNumber = oneEmployeeFromCodes.getNumber();
                        description = oneEmployeeFromCodes.getDescription();
                        descriptionNameOnly = oneEmployeeFromCodes.getDescriptionNameOnly();
                    }
                    addToList(currentEmployeeIdKaba, description, telNumber, name, department, persNumber, plantName, login, email, costCenter, position, descriptionNameOnly, hiredDate, dateOfBirth, privatePhoneNum, isDbAccessible);
                }
            } else {
                addToList(currentEmployeeIdKaba, description, telNumber, name, department, persNumber, plantName, login, email, costCenter, position, descriptionNameOnly, hiredDate, dateOfBirth, privatePhoneNum, isDbAccessible);
            }
        }
    }

    String parseDate(String inputDate) {
        String result = inputDate;

        SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat myFormat = new SimpleDateFormat("dd.MM.yyyy");

        try {
            result = myFormat.format(fromUser.parse(inputDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }

    private void createPhoneArrayListWithoutInfoFromDb(boolean isDbAccessible) {

        String telNumber = "";
        String name = "";
        String department = "";
        String description = "";
        String descriptionNameOnly = "";
        String persNumber = "";
        String plantName = "";
        String login = "";
        String email = "";
        String costCenter = "";
        String position = "";
        String hiredDate = "";
        String dateOfBirth = "";
        String privatePhoneNum = "";
        int currentEmployeeIdKaba = 0;

        for (List<EmployeeCodesFile> oneElement : mapFromExcel.values()) {
            for (EmployeeCodesFile oneLine : oneElement) {

                telNumber = oneLine.getNumber();
                persNumber = String.valueOf(oneLine.getPersNumber());
                descriptionNameOnly = oneLine.getDescriptionNameOnly();
                description = oneLine.getDescription();
                name = oneLine.getNameUkr();
                department = oneLine.getDepartment();
                plantName = oneLine.getPlantName();
                costCenter = oneLine.getCostCenter();

                addToList(currentEmployeeIdKaba, description, telNumber, name, department, persNumber, plantName, login, email, costCenter, position, descriptionNameOnly, hiredDate, dateOfBirth, privatePhoneNum, isDbAccessible);
            }
        }
    }

    private void addToList(int currentEmployeeIdKaba, String description, String telNumber, String name, String department, String persNumber, String plantName, String login, String email, String costCenter, String position, String descriptionNameOnly, String hiredDate, String dateOfBirth, String privatePhoneNum, boolean isDbAccessible) {
        List<String> mobileTelNumber = new LinkedList<>();
        List<String> additionalMobileTelNumbers = new LinkedList<>();
        List<String> allMobileTelNumbers = new LinkedList<>();
        String photoLink = "";

        String currentPersNumber = "";
        if (isDbAccessible) {
            currentPersNumber = String.valueOf(currentEmployeeIdKaba);
        } else {
            currentPersNumber = persNumber;
        }

        try {

            for (EmployeeMobileFile oneLineWord : listFromMobileNumbersFile) {

                String nameFromWord = oneLineWord.getNameFromWord();
                String mobileNumberFromWord = oneLineWord.getMobileNumberFromWord();
                String persNumberFromWord = oneLineWord.getPersNumberFromWord();

                if (persNumberFromWord.equals(currentPersNumber)) {

                    if (nameFromWord.equalsIgnoreCase(descriptionNameOnly) || descriptionNameOnly.isEmpty()) {
                        mobileTelNumber.add(mobileNumberFromWord);
                    } else {
                        if (additionalMobileTelNumbers.isEmpty()) {
                            allMobileTelNumbers.addAll(additionalMobileTelNumbers);
                        }
                        additionalMobileTelNumbers.add(mobileNumberFromWord + " (" + nameFromWord + ")");
                    }


                }
            }

            if (!mobileTelNumber.isEmpty()) {
                allMobileTelNumbers.addAll(mobileTelNumber);
            }
            if (!additionalMobileTelNumbers.isEmpty()) {
                allMobileTelNumbers.addAll(additionalMobileTelNumbers);
            }

            if (name.split(" ").length == 3) {
                if (plantName.contains("Stryi")) {
                    photoLink = photoLocationStryi + persNumber + " " + name.substring(0, name.lastIndexOf(" ")) + ".jpg";
                } else if (plantName.contains("Kolomyia")) {
                    photoLink = photoLocationKolomyia + persNumber + " " + name.substring(0, name.lastIndexOf(" ")) + ".jpg";
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        id++;
        employeeMap.put(id, new Employee(id, telNumber, name, department, allMobileTelNumbers, description, persNumber, plantName, login, email, costCenter, position, photoLink, hiredDate, dateOfBirth, privatePhoneNum));
    }

    public Map<Integer, Employee> getAllEmployees() {
        return employeeMap;
    }

 /*   public static void main(String[] args) {
        EmployeeServiceImpl test = new EmployeeServiceImpl();
        test.TelListe_FILE_NAME = "//svua1file01/groups/IT/Siemens/Codes.xlsm";
        test.MobilePhones_FILE_NAME = "//svua1file01/infos/IT Info/List of LEONI Kyivstar numbers.xlsx";
        test.getEmployeeListNewSession();

        String regex = "\\A4(\\d){3}.+Kolomyia\\z";
        String regex2 = "П.*\\bІван\\b";

        test.searchEmployeesByName(regex);
    }*/

    public EmployeeWithAdditionalInfo searchEmployeesByName(String inputString) {

        List<Employee> resultList = new ArrayList<>();
        Set<String> matchedRegexFields = new HashSet<>();

        if (inputString.startsWith("&")) {

            int size = inputString.split("&").length;

            if (size > 0) {
                String regex = inputString.split("&")[1];
                resultList = getOneSearchListResultWithRegEx(regex);
            }

        } else if (inputString.contains("&")) {

            inputString = inputString.toLowerCase();
            String[] searchWords = inputString.split("&");

            Map<Integer, Employee> employeeMap = new HashMap<>();

            for (String currentSearchWord : searchWords) {

                List<Employee> oneSearchListResult = getOneSearchListResult(currentSearchWord);

                if (oneSearchListResult != null && !oneSearchListResult.isEmpty()) {
                    for (Employee currentEmployee : oneSearchListResult) {
                        if (objectContainAllSearchWords(currentEmployee, searchWords)) {
                            for (String searchWord : searchWords) {
                                Pattern pattern = getPatternObject(searchWord, false);
                                if (pattern != null) {
                                    String inputField = currentEmployee.toStringForRegex();
                                    if (pattern.matcher(inputField.toLowerCase()).find()) {
                                        currentEmployee = getEmployeeWithHighlightedTag(currentEmployee, pattern, inputField, false);
                                    }
                                }
                            }
                            employeeMap.put(currentEmployee.getId(), currentEmployee);
                        }
                    }
                }
            }

            if (!employeeMap.isEmpty()) {
                resultList.addAll(employeeMap.values());
            }

        } else {
            List<Employee> result = new LinkedList<>();
            List<Employee> searchResultList = getOneSearchListResult(inputString.toLowerCase());
            Pattern pattern = getPatternObject(inputString, false);
            if (pattern != null) {
                for (Employee currentEmployee : searchResultList) {
                    String inputField = currentEmployee.toStringForRegex();
                    if (pattern.matcher(inputField.toLowerCase()).find()) {
                        result.add(getEmployeeWithHighlightedTag(currentEmployee, pattern, inputField, false));
                    }
                }
            }
            resultList = result;
        }

        return new EmployeeWithAdditionalInfo(resultList, matchedRegexFields);
    }

    private Pattern getPatternObject(String regex, boolean isNeedToUseRegex) {

        if (!isNeedToUseRegex && !regex.contains("(") && !regex.contains(")")) {
            regex = regex.toLowerCase();
        }

        if (regex.startsWith("+")) {
            regex = regex.replace("+", "\\+");
        }

        if (!isNeedToUseRegex) {
            if (regex.contains("(")) {
                regex = regex.replace("(", "\\(");
            }
            if (regex.contains(")")) {
                regex = regex.replace(")", "\\)");
            }
        }

        Pattern pattern = null;
        try {
            pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pattern;
    }

    private List<Employee> getOneSearchListResultWithRegEx(String regex) {
        List<Employee> result = new ArrayList<>();

        Pattern pattern = getPatternObject(regex, true);
        if (pattern != null) {
            for (Employee currentEmployee : employeeMap.values()) {
                String inputField = currentEmployee.toStringForRegex();
                if (pattern.matcher(inputField).find()) {
                    System.out.println(currentEmployee.getHiredDate());
                    result.add(getEmployeeWithHighlightedTag(currentEmployee, pattern, inputField, true));
                }
            }
        }
        return result;
    }

    Employee getEmployeeWithHighlightedTag(Employee currentEmployee, Pattern pattern, String inputField, boolean isNeedToUseRegex) {

        String startHighlightTag = "<span class=highLight>";
        String endHighlightTag = "</span>";

        String startHighlight = "<start>";
        String endHighlight = "<end>";

        Matcher matcher;
        if (isNeedToUseRegex) {
            matcher = pattern.matcher(inputField);
        } else {
            matcher = pattern.matcher(inputField.toLowerCase());
        }

        String outputString = "";

        while (matcher.find()) {

            int startIndex = matcher.start();
            int endIndex = matcher.end();

            if (startIndex != endIndex) {
                String foundedString = inputField.substring(startIndex, endIndex);
                String replacedString = startHighlight + foundedString + endHighlight;
                try {
                    String firstPart = inputField.substring(0, startIndex);
                    String endPart = inputField.substring(endIndex);
                    String highlightedResult = firstPart + replacedString + endPart;
                    if (!highlightedResult.isEmpty()) {
                        outputString = highlightedResult;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (outputString.isEmpty()) {
            outputString = inputField;
        }

        String[] currentUserDataInArray = outputString.split("\\|");

        StringBuilder stringBuilder = new StringBuilder();

        boolean iSNeedToMarkNextPart = false;
        for (String currentPart : currentUserDataInArray) {

            if (currentPart.contains(startHighlight)) {
                currentPart = currentPart.replace(startHighlight, startHighlightTag) + endHighlightTag;
                iSNeedToMarkNextPart = true;
            }

            if (currentPart.contains(endHighlight)) {
                currentPart = currentPart.replace(endHighlight, endHighlightTag);
                if (!currentPart.contains(startHighlightTag)) {
                    currentPart = startHighlightTag + currentPart;
                }
                iSNeedToMarkNextPart = false;
            }

            if (iSNeedToMarkNextPart && !currentPart.contains(startHighlightTag) && !currentPart.contains(startHighlightTag)) {
                currentPart = startHighlightTag + currentPart + endHighlightTag;
            }

            stringBuilder.append(currentPart);
            stringBuilder.append("|");
        }

        String resultString = stringBuilder.toString();


        String resultStringWithout = resultString.substring(0, resultString.length() - 1);

        String[] resultObjectArray = resultStringWithout.split("\\|");

        int id = currentEmployee.getId();
        String telNumber = resultObjectArray[0];
        String name = resultObjectArray[3];
        String department = resultObjectArray[4];
        List<String> allMobileTelNumbers = new ArrayList(Arrays.asList(resultObjectArray[9].replace("[", "").replace("]", "").split(",")));
        String description = resultObjectArray[1];
        String persNumber = resultObjectArray[2];
        String plantName = resultObjectArray.length == 11 ? resultObjectArray[10] : "";
        String login = resultObjectArray[7];
        String email = resultObjectArray[8];
        String costCenter = resultObjectArray[6];
        String position = resultObjectArray[5];
        String photoLink = currentEmployee.getPhotoLink();
        String hiredDate = currentEmployee.getHiredDate();
        String dateOfBirth = currentEmployee.getDateOfBirth();
        String privatePhoneNum = currentEmployee.getPrivatePhoneNum();

        return new Employee(id, telNumber, name, department, allMobileTelNumbers, description, persNumber, plantName, login, email, costCenter, position, photoLink, hiredDate, dateOfBirth, privatePhoneNum);
    }

    private boolean objectContainAllSearchWords(Employee employee, String[] items) {

        Map<Integer, Integer> foundedEmployees = new HashMap<>();
        Map<String, Integer> foundedInOneField = new HashMap<>();
        int fieldNumber = 0;
        for (String currentField : employee.getEmployeeAsList()) {
            fieldNumber++;
            int searchWordNumber = 0;
            for (String currentSearchWord : items) {
                searchWordNumber++;
                if (currentField.toLowerCase().contains(currentSearchWord)) {
                    foundedEmployees.put(fieldNumber, searchWordNumber);
                    foundedInOneField.put(currentSearchWord, fieldNumber);
                }
            }
        }

        Set<Integer> allFoundedFieldNumbers = new TreeSet<>(foundedEmployees.keySet());
        Set<Integer> allFoundedSearchWords = new TreeSet<>(foundedEmployees.values());
        int searchWordsCount = items.length;
        int differentFieldsCount = allFoundedFieldNumbers.size();
        int differentSearchWordsCount = allFoundedSearchWords.size();

        return ((differentSearchWordsCount == searchWordsCount && differentFieldsCount >= differentSearchWordsCount) || (foundedInOneField.size() >= searchWordsCount));
    }

    private List<Employee> getOneSearchListResult(String searchString) {

        Comparator<Employee> groupByComparator = Comparator.comparing(Employee::getPersNumber).thenComparing(Employee::getTelNumber).thenComparing(Employee::getDepartment);

        List<Employee> result = employeeMap.values()
                .stream()
                .filter(e -> e.getName().toLowerCase().contains(searchString) || e.getPosition().toLowerCase().contains(searchString) || e.getTelNumber().contains(searchString) || e.getEmail().toLowerCase().contains(searchString) || e.getCostCenter().contains(searchString) || e.getLogin().toLowerCase().contains(searchString) || e.getAllMobileTelNumbersString().toLowerCase().contains(searchString) || e.getPersNumber().contains(searchString) || e.getDepartment().toLowerCase().contains(searchString) || e.getDescription().toLowerCase().contains(searchString) || e.getPlantName().toLowerCase().contains(searchString))
                .sorted(groupByComparator)
                .collect(Collectors.toList());
        return result;
    }

    @Override
    public List<String> processCommand(String getCommand, String searchString) {
        return employeeDao.processCommand(getCommand, searchString);
    }

    @Bean
    @Scope(value = WebApplicationContext.SCOPE_SESSION)
    @Override
    public boolean isPermissionIsGranted() {
        getMembersOfAdAccessGroup(GRANTED_AD_GROUP, getLdapContext());
        return adGroupPhotosMembers.contains(currentSessionUserName);
    }


    private void getMembersOfAdAccessGroup(String adGroupForSearch, LdapContext ctx) {

        NamingEnumeration results;
        String searchbase = "OU=Groups,OU=UA1,DC=leoni,DC=local";

        try {

            SearchControls controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            results = ctx.search(searchbase, adGroupForSearch, controls);

            while (results.hasMore()) {
                SearchResult searchResult = (SearchResult) results.next();
                Attributes attributes = searchResult.getAttributes();
                Attribute attr = attributes.get("member");

                if (attr != null) {

                    for (int i = 0; i < attr.size(); i++) {

                        String result = (String) attr.get(i);
                        if (result.contains(",")) {

                            String[] currentAdObject = result.split(",");

                            if (currentAdObject.length == 6) {
                                String userNamePart = currentAdObject[0];
                                String userDelimiter = "CN=";
                                if (userNamePart.contains(userDelimiter)) {
                                    adGroupPhotosMembers.add(userNamePart.split(userDelimiter)[1]);
                                }
                            } else {
                                String groupNamePart = currentAdObject[0];
                                getMembersOfAdAccessGroup(groupNamePart, ctx);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    LdapContext getLdapContext() {
        LdapContext ctx = null;
        try {
            Hashtable env = new Hashtable();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.SECURITY_AUTHENTICATION, "Simple");
            env.put(Context.SECURITY_PRINCIPAL, ldapUsername);
            env.put(Context.SECURITY_CREDENTIALS, ldapPassword);
            env.put(Context.PROVIDER_URL, servername);
            ctx = new InitialLdapContext(env, null);
        } catch (NamingException nex) {
            System.out.println("LDAP Connection: FAILED");
            nex.printStackTrace();
        }
        return ctx;
    }

    /*private String getUserBasicAttributes(String username, LdapContext ctx) {

        String result = "";

        if (!username.isEmpty()) {
            try {

                SearchControls constraints = new SearchControls();
                constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);

                String[] attrIDs = {"MemberOf"};

                constraints.setReturningAttributes(attrIDs);

                NamingEnumeration answer = ctx.search("DC=leoni,DC=local", "sAMAccountName=" + username, constraints);

                if (answer.hasMore()) {
                    Attributes attrs = ((SearchResult) answer.next()).getAttributes();
                    if (attrs.size() > 0) {
                        result = attrs.get("MemberOf").toString();
                    }
                } else {
                    throw new Exception("Invalid User");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return result;
    }*/
}