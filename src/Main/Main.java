package Main;

import DataAugmentation.PerformDataAugmentation;
import WebScraper.WebScraperAmazon;

// ==============================================================================
// Author:          PaulLewisJohnston
// University:      Queen's University Belfast
// Email:           pjohnston36@qub.ac.uk
// Description:     This Main Class is used to perform a run of the system,
//                  specifying the number of web pages to be scraped
// ==============================================================================

public class Main {
    public static void main(String[] args){
        //Insert MaxNumPages to scrape on Amazon (400 Pages Max / 47x2 persons per page)
        WebScraperAmazon.Run(3);
        PerformDataAugmentation.Run();
    }
}
