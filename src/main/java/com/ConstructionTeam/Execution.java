package com.ConstructionTeam;

import com.ConstructionTeam.DataModels.ErrorData;
import com.ConstructionTeam.DataModels.User;
import com.ConstructionTeam.DatabaseRepository.MySQL_CRUDOperator;
import com.ConstructionTeam.EmailRepository.EmailBodyCreator;
import com.ConstructionTeam.EmailRepository.MailgunEmailSender;
import com.ConstructionTeam.FileRepository.LastAccessFileReader;
import com.ConstructionTeam.FileRepository.LogFileReader;
import com.ConstructionTeam.UserInterface.UI;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class Execution {
    public void excute() throws IOException {

        String logFilePath;
        UI UI = new UI();
        logFilePath = UI.executeUI();

        // Code Start Here
        String lastAccessDateTime;
        // Last Access file Read
        String lastAccessFilePath = "src/main/java/com/ConstructionTeam/FileRepository/LastAccessDateTime.txt";
        LastAccessFileReader lastAccessFileReader = new LastAccessFileReader();
        lastAccessDateTime = lastAccessFileReader.getLastAccessDateTime(lastAccessFilePath);
        // Log file Read
        LogFileReader logFileReader = new LogFileReader();
        ArrayList<ErrorData> errorList;
        errorList = logFileReader.getData(logFilePath,lastAccessDateTime,lastAccessFilePath);

        // Get Mail List
        ArrayList <User> userDetails = new ArrayList<>();
        try {
            userDetails = new MySQL_CRUDOperator().getUserMailList();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        EmailBodyCreator emailBodyCreater = new EmailBodyCreator();
        StringBuilder emailBody = emailBodyCreater.createMailBody(errorList);
        if(emailBody == null){
            System.out.println("No Errors Found");
        }
        else {
            MailgunEmailSender mailgunEmailSender = new MailgunEmailSender();
            for (User user : userDetails) {
                mailgunEmailSender.sendSimpleMessage(user, emailBody);
            }
        }
    }
}
