package controller;

import datastorage.DAOFactory;
import datastorage.PatientDAO;
import datastorage.TreatmentDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import model.Patient;
import model.Treatment;
import utils.DateConverter;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

/**
 * The <code>MainWindowController</code> contains the entire logic of the MainWindow view.
 */
public class MainWindowController {

    @FXML
    private BorderPane mainBorderPane;

    /**
     * initialize the automatic deletion.
     */
    public void initialize() {
        checkPatientDataForDeletion();
    }

    /**
     * this method shows the window where all patients are visible
     * @param e
     */
    @FXML
    private void handleShowAllPatient(ActionEvent e) {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/AllPatientView.fxml"));
        try {
            mainBorderPane.setCenter(loader.load());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        AllPatientController controller = loader.getController();
    }

    /**
     * this method shows the window where all treatments are visible
     * @param e
     */
    @FXML
    private void handleShowAllTreatments(ActionEvent e) {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/AllTreatmentView.fxml"));
        try {
            mainBorderPane.setCenter(loader.load());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        AllTreatmentController controller = loader.getController();
    }

    /**
     * this method shows the window where all caregivers are visible
     * @param e
     */
    @FXML
    private void handleShowAllCaregivers(ActionEvent e) {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/AllCaregiverView.fxml"));
        try {
            mainBorderPane.setCenter(loader.load());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        AllCaregiverController controller = loader.getController();
    }

    /**
     * methode for deletion after 10 year for treatments and patients
     */
    private void checkPatientDataForDeletion() {
        try {
            TreatmentDAO treatmentDAO = DAOFactory.getDAOFactory().createTreatmentDAO();
            List<Treatment> allTreatments = treatmentDAO.readAll();
            for (Treatment treatment : allTreatments) {
                if (Period.between(DateConverter.convertStringToLocalDate(
                        treatment.getDate()), LocalDate.now()).getYears() >= 10) {
                    System.out.println("automatically delete treatment");
                    treatmentDAO.deleteById(treatment.getTid());
                }
            }
            PatientDAO patientDAO = DAOFactory.getDAOFactory().createPatientDAO();
            List<Patient> allPatients = patientDAO.readAll();
            for (Patient patient : allPatients) {

                allTreatments = DAOFactory.getDAOFactory().createTreatmentDAO().readAll();
                for(Treatment treatment: allTreatments){
                    if (treatment.getPid() == patient.getPid()) {
                        continue;
                    }
                }

                if (Period.between(DateConverter.convertStringToLocalDate(
                        patient.getCreationDate()), LocalDate.now()).getYears() >= 10) {
                    System.out.println("automatically delete patient");
                    patientDAO.deleteById(patient.getPid());
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


    }

}

