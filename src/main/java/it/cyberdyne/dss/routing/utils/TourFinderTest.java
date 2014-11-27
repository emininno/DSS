package it.cyberdyne.dss.routing.utils;

import it.cyberdyne.dss.routing.TourManager;

    
public class TourFinderTest {

    public static void main(String[] args) {
		int call_id = 1;
		start(call_id, null);  // per inputdir di default
		//start(call_id, "C:/path_to_files/input");
    }

    private static void start(int call_id, String inputDir) {
		TourManager manager = new TourManager();

        int esito = TourManager.RESULT_OK;
        if (inputDir != null && !inputDir.trim().isEmpty())
            esito = manager.start(call_id, inputDir);
        else
            esito = manager.start(call_id);

        if (esito == TourManager.RESULT_FAULT) {
            System.err.println("Errore nell'esecuzione del software TourFinder\n");
        }
    }
}



