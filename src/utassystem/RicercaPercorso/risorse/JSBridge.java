package utassystem.RicercaPercorso.risorse;

import javafx.application.Platform;

public class JSBridge {

    public void exit() {
        Platform.exit();
    }

    public void log(String text) {
        System.out.println(text);
    }

}
