import controller.AdminController;
import controller.LoginController;
import controller.StudentController;
import filemanager.ILoginInfoFileManager;
import filemanager.ILoginable;
import filemanager.IStorageManager;
import filemanager.LoginInfoFileManager;
import filemanager.LoginManager;
import filemanager.StorageManager;
import model.AccountType;

public class Main {

    public static void main(String[] args) {
        IStorageManager storageManager = new StorageManager();
        ILoginInfoFileManager loginInfoFileManager = new LoginInfoFileManager();
        ILoginable loginManager = new LoginManager(loginInfoFileManager, storageManager);

        LoginController lc = new LoginController(storageManager, loginManager);
        AccountType accountType = lc.run();
        switch (accountType) {
        case ADMIN:
            AdminController ac = new AdminController(storageManager);
            ac.run();
            break;
        case STUDENT:
            StudentController sc = new StudentController(lc.getUserId(), storageManager, loginManager);
            sc.run();
            break;
        default:
            assert false : "Invalid accountType returned from LoginController!";
        }

    }

}
