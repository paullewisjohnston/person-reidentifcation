package WebScraper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.commons.io.IOUtils;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.List;

// ==============================================================================
// Author:          PaulLewisJohnston
// University:      Queen's University Belfast
// Email:           pjohnston36@qub.ac.uk
// Description:     This WebScraperAmazon Class uses HtmlUnit
//                  framework to crawl through the number of
//                  pages specified in the main and download
//                  the person image pairs from the specified
//                  search URL for Amazon clothing section
// ==============================================================================

public class WebScraperAmazon {

    public static void Run(int maxPageNum) {
        int titleNum = 0;
        final DecimalFormat decimalFormat = new DecimalFormat("00000");
        for (int i = 1; i <= maxPageNum; i++) {
            System.out.println("Page " +i+ " Started");

            //Search string for full body female images
            String URL = "https://www.amazon.com/s/ref=sr_pg_3?fst=as%3Aoff&rh=n%3A7141123011%2Cn%3A7147440011%2Cn%3A1040660%2Cn%3A1045024%2Cp_6%3AATVPDKIKX0DER&" +
                            "page="+i+"&bbn=1045024&ie=UTF8&qid=1485466196";

            //Web client acts as a GUI-less browser
            WebClient client = new WebClient();
            client.getOptions().setCssEnabled(false);
            client.getOptions().setJavaScriptEnabled(false);
            try {
                HtmlPage page = client.getPage(URL);

                //Returns list of html elements containing the first pose of all person images on given webpage
                List<HtmlElement> itemsVisible = (List<HtmlElement>) page.getByXPath("//div[@class='a-row a-gesture a-gesture-horizontal']/div/a/img");
                //Returns list of html elements containing the second pose of all person images on given webpage
                List<HtmlElement> itemsHidden = (List<HtmlElement>) page.getByXPath("//div[@class='a-row a-gesture a-gesture-horizontal']/div[2]/a/div");

                if (itemsHidden.isEmpty()) {
                    System.out.println("No items found !");
                } else {
                    for (int j = 0; j < itemsVisible.size(); j++) {

                        //Maps title and url of images to person objects
                        String titleA = "Person" + decimalFormat.format(titleNum) + "A";
                        String urlA = itemsVisible.get(j).getAttribute("src");
                        Person personA = new Person(titleA, urlA);

                        String titleB = "Person" + decimalFormat.format(titleNum) + "B";
                        String urlB = itemsHidden.get(j).getAttribute("data-search-image-source");
                        Person personB = new Person(titleB, urlB);

                        ObjectMapper mapper = new ObjectMapper();
                        String jsonStringA = mapper.writeValueAsString(personA);
                        System.out.println(jsonStringA);

                        String jsonStringB = mapper.writeValueAsString(personB);
                        System.out.println(jsonStringB);

                        //Gets image content for persons first pose from url via web response and stores in local directory
                        Page personAJpg = client.getPage(personA.getUrl());
                        if (personAJpg.getWebResponse().getContentType().equals("image/jpeg")) {
                            InputStream source = personAJpg.getWebResponse().getContentAsStream();
                            FileOutputStream dest = new FileOutputStream("dataset/person/personA/" + personA.getTitle().trim() + ".png");
                            IOUtils.copy(source, dest);
                        }

                        //Gets image content for persons second pose from url via web response and stores in local directory
                        Page personBJpg = client.getPage(personB.getUrl());
                        if (personBJpg.getWebResponse().getContentType().equals("image/jpeg")) {
                            InputStream source = personBJpg.getWebResponse().getContentAsStream();
                            FileOutputStream dest = new FileOutputStream("dataset/person/personB/" + personB.getTitle().trim() + ".png");
                            IOUtils.copy(source, dest);
                        }
                        titleNum++;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Page " +i+ " Completed");
        }
    }
}