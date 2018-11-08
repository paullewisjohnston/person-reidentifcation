package WebScraper;

// ==============================================================================
// Author:          PaulLewisJohnston
// University:      Queen's University Belfast
// Email:           pjohnston36@qub.ac.uk
// Description:     This Person object class is used to model
//                  the images as objects with a title and URL
// ==============================================================================

public class Person {
    private String title ;
    private String url ;

    public Person(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
}
