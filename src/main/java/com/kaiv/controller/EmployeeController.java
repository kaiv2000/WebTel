package com.kaiv.controller;

import com.kaiv.model.Employee;
import com.kaiv.model.EmployeeWithAdditionalInfo;
import com.kaiv.service.EmployeeService;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.poi.util.Beta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLDecoder;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Controller
@Component
@Scope(value = "session")
public class EmployeeController {

    @Lazy
    @Autowired
    EmployeeService employeeService;

    @Lazy
    @Resource(name = "getEmployeeListNewSession")
    Map<Integer, Employee> employeeMap;

    @Resource(name = "getPagableList")
    PagedListHolder pagedList;

    @Autowired
    String currentSessionUserName;

    @Resource(name = "isPermissionIsGranted")
    boolean isPermissionIsGranted;

    @Value("${getinfo.USERS_PHOTO_STRYI}")
    String photoLocationStryi;

    @Value("${getinfo.USERS_PHOTO_KOLOMIYA}")
    String photoLocationKolomyia;

    @Value("${getinfo.noPhotoPicture}")
    String noPhotoPicture;

    @Value("${getinfo.regexCheetSheetPicture}")
    String regexChSeetSheetPicture;

    // main page
    @RequestMapping("/")
    public String index(HttpServletRequest request) {
        checkIfDataLoaded(request);
        return "/list-employees";
    }

    // telList redirect page
    @RequestMapping("/tellist")
    public String oldTelListRedirect(HttpServletRequest request) {
        checkIfDataLoaded(request);
        return "/list-employees";
    }

    private void checkIfDataLoaded(HttpServletRequest request) {

        HttpSession session = request.getSession(true);

        if (!currentSessionUserName.isEmpty()) {
            session.setAttribute("currentSessionUserName", currentSessionUserName);
        }
        session.removeAttribute("searchString");
        addRegexCheetImage(session);

        if (!session.isNew()) {
            processRequest(null, null, request);
        }
    }

    // show tel list
    @RequestMapping("/list")
    public String showTelList(@RequestParam(value = "searchAction", required = false) String searchString,
                              @RequestParam(value = "show", required = false) String neededPage,
                              HttpServletRequest request) {

        processRequest(searchString, neededPage, request);
        return "/list-employees";
    }

    private void addRegexCheetImage(HttpSession session) {
        String regexChSheetPath = getImagePathFromResources(regexChSeetSheetPicture);
        String regexPhotoLink = checkAndFixPhotoPosition(regexChSheetPath);
        session.setAttribute("regexPhotoLink", regexPhotoLink);
    }

    private void processRequest(String searchString,
                                String neededPage,
                                HttpServletRequest request) {

        HttpSession session = request.getSession(true);

        if (employeeMap != null && employeeMap.size() > 0) {
            if (!currentSessionUserName.isEmpty()) {
                session.setAttribute("currentSessionUserName", currentSessionUserName);
            }

            if (searchString != null) {

                EmployeeWithAdditionalInfo employeeWithAdditionalInfo = employeeService.searchEmployeesByName(searchString);
                List<Employee> foundEmployeeList = employeeWithAdditionalInfo.getFoundedEmployeeList();

                pagedList.setSource(foundEmployeeList);
                pagedList.setPage(0);

                session.setAttribute("searchString", searchString);
                session.setAttribute("employeeList", getPageListWithFixedPhotoLinks(pagedList));

            } else if (neededPage == null) {
                addAllUsersToList(session);
            }

            if (neededPage != null) {

                if (pagedList.getSource().isEmpty()) {
                    addAllUsersToList(session);
                }

                switch (neededPage) {
                    case "prevPage":
                        pagedList.previousPage();
                        session.setAttribute("employeeList", getPageListWithFixedPhotoLinks(pagedList));
                        break;

                    case "nextPage":
                        pagedList.nextPage();
                        session.setAttribute("employeeList", getPageListWithFixedPhotoLinks(pagedList));
                        break;
                }
            }

            addRegexCheetImage(session);
            session.setAttribute("allPagesCount", pagedList.getPageCount());
            session.setAttribute("currentPage", pagedList.getPage() + 1);
            session.setAttribute("foundedObjectsCount", pagedList.getNrOfElements());

            session.setAttribute("isPermissionIsGranted", isPermissionIsGranted);

        }
    }

    private void addAllUsersToList(HttpSession session) {
        List<Employee> allEmployeeList = new LinkedList<>();
        for (Map.Entry<Integer, Employee> one : employeeMap.entrySet()) {
            allEmployeeList.add(one.getValue());
        }

        pagedList.setSource(allEmployeeList);
        pagedList.setPage(0);

        session.removeAttribute("searchString");
        session.setAttribute("employeeList", getPageListWithFixedPhotoLinks(pagedList));
    }

    @RequestMapping(value = "/details-{id}", method = RequestMethod.GET)
    public String showProduct(@PathVariable("id") int persNumber,
                              HttpServletRequest request) {

        HttpSession session = request.getSession(true);

        if (!currentSessionUserName.equals("loggedUserNotFound")) {
            session.setAttribute("currentSessionUserName", currentSessionUserName);
        }
        session.setAttribute("isPermissionIsGranted", isPermissionIsGranted);

        if (employeeMap != null && employeeMap.size() > 0) {

            EmployeeWithAdditionalInfo employeeWithAdditionalInfo = employeeService.searchEmployeesByName(String.valueOf(persNumber));
            List<Employee> foundEmployeeList = employeeWithAdditionalInfo.getFoundedEmployeeList();

            if (!foundEmployeeList.isEmpty()) {

                int id = foundEmployeeList.get(0).getId();
                String photoLink = foundEmployeeList.get(0).getPhotoLink();
                String plantName = foundEmployeeList.get(0).getPlantName();
                String email = foundEmployeeList.get(0).getEmail();
                String login = foundEmployeeList.get(0).getLogin();
                String costCenter = foundEmployeeList.get(0).getCostCenter();
                String position = foundEmployeeList.get(0).getPosition();
                String department = foundEmployeeList.get(0).getDepartment();
                String name = foundEmployeeList.get(0).getName();
                String hiredDate = foundEmployeeList.get(0).getHiredDate();
                String dateOfBirth = foundEmployeeList.get(0).getDateOfBirth();
                String privatePhoneNum = foundEmployeeList.get(0).getPrivatePhoneNum();
                String currentUserPersNumber = String.valueOf(persNumber);
                List<String> allMobileTelNumbers = new CopyOnWriteArrayList<>();
                List<String> phoneNumbersWithDescription = new ArrayList<>();


                for (Employee oneEmployee : foundEmployeeList) {
                    if (!oneEmployee.getTelNumber().trim().isEmpty() && !oneEmployee.getDescription().trim().isEmpty()) {
                        phoneNumbersWithDescription.add(oneEmployee.getTelNumber() + " " + oneEmployee.getDescription());
                    }

                    for (String oneMobileNumber : oneEmployee.getAllMobileTelNumbers()) {
                        if (!oneMobileNumber.isEmpty()) {
                            if (!allMobileTelNumbers.isEmpty()) {
                                for (String currentMobileNumber : allMobileTelNumbers) {
                                    if (!currentMobileNumber.substring(0, 14).equals(oneMobileNumber.substring(0, 14))) {
                                        allMobileTelNumbers.add(oneMobileNumber);
                                    } else {
                                        if (currentMobileNumber.length() > oneMobileNumber.length()) {
                                            allMobileTelNumbers.remove(currentMobileNumber);
                                            allMobileTelNumbers.add(oneMobileNumber);
                                        }
                                    }
                                }
                            } else {
                                if (!oneMobileNumber.isEmpty()) {
                                    allMobileTelNumbers.add(oneMobileNumber);
                                }
                            }
                        }
                    }
                }

                if (!new File(photoLink).exists()) {
                    photoLink = getPhotoLink(plantName, currentUserPersNumber);
                } else {
                    photoLink = checkAndFixPhotoPosition(photoLink);
                }

                Employee currentEmployee = new Employee(id, name, department, allMobileTelNumbers, currentUserPersNumber, plantName, login, email, costCenter, position, photoLink, hiredDate, dateOfBirth, privatePhoneNum, phoneNumbersWithDescription);

                session.setAttribute("currentEmployee", currentEmployee);
            }
        }
        return "/user-details";
    }

    private PagedListHolder<Employee> getPageListWithFixedPhotoLinks(PagedListHolder<Employee> pagedList) {

        PagedListHolder<Employee> checkedPagableList = pagedList;

        if (isPermissionIsGranted) {

            checkedPagableList = new PagedListHolder<>();

            List<Employee> currentList = pagedList.getPageList();

            Map<Integer, Employee> tempMap = new HashMap<>();

            for (Employee currentEployee : pagedList.getSource()) {
                tempMap.put(currentEployee.getId(), currentEployee);
            }

            for (Employee currentEmployee : currentList) {

                tempMap.remove(currentEmployee.getId());

                int id = currentEmployee.getId();
                String telNumber = currentEmployee.getTelNumber();
                String name = currentEmployee.getName();
                String department = currentEmployee.getDepartment();
                String description = currentEmployee.getDescription();
                String persNumber = currentEmployee.getPersNumber();
                String plantName = currentEmployee.getPlantName();
                String login = currentEmployee.getLogin();
                String email = currentEmployee.getEmail();
                String costCenter = currentEmployee.getCostCenter();
                String position = currentEmployee.getPosition();
                List<String> allMobileTelNumbers = currentEmployee.getAllMobileTelNumbers();
                String photoLink = currentEmployee.getPhotoLink();

                if (!new File(photoLink).exists()) {
                    photoLink = getPhotoLink(plantName, persNumber);
                } else {
                    photoLink = getPhotoLinkEncrypted(photoLink);
                }

                tempMap.put(id, new Employee(id, telNumber, name, department, allMobileTelNumbers, description, persNumber, plantName, login, email, costCenter, position, photoLink));
            }

            List<Employee> tempList = new ArrayList<>(tempMap.values());

            Collections.sort(tempList, Employee.getListByPerId());

            checkedPagableList.setSource(tempList);
            checkedPagableList.setPage(pagedList.getPage());
            checkedPagableList.setPageSize(14);
        }

        return checkedPagableList;
    }

    String getPhotoLink(String plantName, String persNumber) {

        String startHighlightTag = "<span class=highLight>";
        String endHighlightTag = "</span>";

        if (persNumber.contains(startHighlightTag)) {
            persNumber = persNumber.replace(startHighlightTag, "");
        }
        if (persNumber.contains(endHighlightTag)) {
            persNumber = persNumber.replace(endHighlightTag, "");
        }

        File photosDirectory = null;
        String photoLink = "";

        if (plantName.contains("Stryi")) {
            photosDirectory = new File(photoLocationStryi);
        } else if (plantName.contains("Kolomyia")) {
            photosDirectory = new File(photoLocationKolomyia);
        }

        if (photosDirectory != null && photosDirectory.exists()) {
            Collection<File> listOfFiles = (Collection<File>) FileUtils.listFiles(photosDirectory, new WildcardFileFilter(persNumber + "*"), null);
            if (listOfFiles != null && !listOfFiles.isEmpty()) { // if photo was found
                for (File currentFile : listOfFiles) {
                    if (currentFile.isFile()) {
                        photoLink = currentFile.getPath();
                    }
                }
            } else {    // if no photo found
                photoLink = getImagePathFromResources(noPhotoPicture);
            }
        } else { // if path to photo cannot be found
            photoLink = getImagePathFromResources(noPhotoPicture);
        }

        photoLink = checkAndFixPhotoPosition(photoLink);

        return photoLink;
    }

    private String getImagePathFromResources(String pictureNameInResourcesFolder) {
        String photoLink = "";
        try {
            photoLink = new File(URLDecoder.decode(getClass().getClassLoader().getResource("").getPath() + File.separatorChar + pictureNameInResourcesFolder, "UTF-8")).getPath();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return photoLink;
    }

    private String checkAndFixPhotoPosition(String path) {

        String photoLink = path;

        try {
            BufferedImage bufferedImage = ImageIO.read(new File(path));
            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();

            if (width > height) {

                BufferedImage rotatedImage = rotateImage(bufferedImage, 90);


                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(rotatedImage, "jpg", baos);
                baos.flush();
                byte[] imageInByte = baos.toByteArray();

                photoLink = new String(Base64.encodeBase64(imageInByte), "UTF-8");

                baos.close();

            } else {
                photoLink = getPhotoLinkEncrypted(photoLink);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return photoLink;
    }

    private String getPhotoLinkEncrypted(String photoLink) {

        String encryptedPhotoLink = "";

        try {
            File file = new File(photoLink);
            if (file != null && file.exists()) {
                FileInputStream fileInputStreamReader = new FileInputStream(file);
                byte[] bytes = new byte[(int) file.length()];
                fileInputStreamReader.read(bytes);
                encryptedPhotoLink = new String(Base64.encodeBase64(bytes), "UTF-8");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return encryptedPhotoLink;
    }

    public BufferedImage rotateImage(BufferedImage src, double degrees) {
        double radians = Math.toRadians(degrees);

        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();

        /*
         * Calculate new image dimensions
         */
        double sin = Math.abs(Math.sin(radians));
        double cos = Math.abs(Math.cos(radians));
        int newWidth = (int) Math.floor(srcWidth * cos + srcHeight * sin);
        int newHeight = (int) Math.floor(srcHeight * cos + srcWidth * sin);

        /*
         * Create new image and rotate it
         */
        BufferedImage result = new BufferedImage(newWidth, newHeight,
                src.getType());
        Graphics2D g = result.createGraphics();
        g.translate((newWidth - srcWidth) / 2, (newHeight - srcHeight) / 2);
        g.rotate(radians, srcWidth / 2, srcHeight / 2);
        g.drawRenderedImage(src, null);

        return result;
    }
}