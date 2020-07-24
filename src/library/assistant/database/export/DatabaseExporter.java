package library.assistant.database.export;

import javafx.concurrent.Task;
import library.assistant.alert.AlertMaker;
import library.assistant.database.DatabaseHandler;
import library.assistant.util.LibraryAssistantUtil;

import java.io.File;
import java.sql.CallableStatement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DatabaseExporter extends Task<Boolean> {

    private final File backupDirectory;

    public DatabaseExporter(File backupDirectory) {
        this.backupDirectory = backupDirectory;
    }

    @Override
    protected Boolean call() {
        try {
            creatBackup();
            return true;
        }catch (Exception exp){
            AlertMaker.showErrorMessage(exp);
        }
        return false;
    }

    private void creatBackup() throws Exception{
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy_MM_dd_hh_mm_ss");
        String backUpDirectory = backupDirectory.getAbsolutePath() + File.separator + LocalDateTime.now().format(dateTimeFormat);
        try (CallableStatement cs = DatabaseHandler.getInstance().getConnection().prepareCall("CALL SYSCS_UTIL.SYSCS_BACKUP_DATABASE(?)")){
            cs.setString(1, backUpDirectory);
            cs.execute();
        }
        File file = new File(backUpDirectory);
        LibraryAssistantUtil.openFileWithDesktop(file);
    }
}
