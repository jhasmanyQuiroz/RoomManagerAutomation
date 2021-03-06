package ui.pages;

import api.APIMethodsResource;
import entities.Resource;
import api.APIManager;
import database.DBQuery;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ui.BaseMainPageObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BrayanRosas
 * Date: 11/10/15
 * Time: 6:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class ResourcePage extends BaseMainPageObject{
    private boolean exitsResource=false;
    private Actions action = new Actions(driver);
    ArrayList<Resource> resources=new ArrayList<Resource>()  ;


    @FindBy(xpath= "//button[@id='btnRemove']/preceding-sibling::button")
    private WebElement buttonAddResource;

    @FindBy(id= "btnRemove")
    private WebElement buttonRemoveResource;

    @FindBy(xpath= "//input[@ng-model='resourceNameFilter']")
    private WebElement inputFilterResource;

    @FindBy(xpath= "//div[@class='ngCell centeredColumn col1 colt1']")
    private WebElement columnIconResource;

    @FindBy(xpath= "//div[@class='ngCell centeredColumn col2 colt2']")
    private WebElement columnNameResource;

    @FindBy(xpath= "//div[@class='ngCell centeredColumn col3 colt3']")
    private WebElement columnCustomNameResource;

    public ResourcePage() {
        PageFactory.initElements(driver, this);
        waitUntilPageObjectIsLoaded();
    }

    @Override
    public void waitUntilPageObjectIsLoaded() {
        wait.until(ExpectedConditions.visibilityOf(buttonAddResource));
    }

    public boolean existInColumnName(String resourceName){

        if(isPresent(By.xpath("//div[@class='ngCell centeredColumn col2 colt2']//span[text()='"+resourceName+"']"))) {
            exitsResource=true;
        }
        else {
            exitsResource=false;
        }
        return exitsResource;
    }

    public boolean existInColumnCustomName(String resourceCustomName){
        if(isPresent(By.xpath("//div[@class='ngCell centeredColumn col3 colt3']//span[text()='"+resourceCustomName+"']"))) {
            exitsResource=true;
        }
        else {
            exitsResource=false;
        }
        return exitsResource;
    }

    public ResourceInfoPage goToPropertyResource(String resourceName){
        action.moveToElement(columnCustomNameResource.findElement(By.xpath("//span[text()='"+resourceName+"']"))).doubleClick().build().perform();
        columnCustomNameResource.findElement(By.xpath("//span[text()='"+resourceName+"']")).click();
        return new ResourceInfoPage();
    }

    public AddResourcePage clickAddButton(){
        buttonAddResource.click();
        return new AddResourcePage();
    }

    public boolean moreThatTwoResourceSameName(Resource resource1){
        if((driver.findElements(By.xpath("//div[@class='ngCell centeredColumn col2 colt2']//span[text()='"+resource1.getName()+"']"))).size()==1){
                  exitsResource=false;
        }
        else {
            exitsResource=true;
        }
        return exitsResource;
    }

    public void filterResource(String searchCriteria){
        inputFilterResource.sendKeys(searchCriteria);
    }

    public List<WebElement> getResourcesNamesWebElements(){
        return driver.findElements(By.xpath("//div[contains(@class,'ng-scope ngRow')]//div[contains(@class,'col2')]//span"));
    }

    public void CheckOutResource(String resourceName){
        driver.findElement(By.xpath("//div[contains(@class,'ng-scope ngRow')]/div[contains(@class,'col2')]//span[text()='"+resourceName+"']/parent::div/parent::div/parent::div/preceding-sibling::div//input[@type='checkbox']")).click();
    }

    public ResourceAssociationsPage clickDeleteResource(String resourceName){
        CheckOutResource(resourceName);
        buttonRemoveResource.click();
        return new ResourceAssociationsPage();
    }
    /**
     * Get all resources name using locator by UI
     * @return An array list with the names of the resources
     */
    public ArrayList<String> getResourcesNameByUI(){
        ArrayList<String> resourcesName = new ArrayList<String>();
        List<WebElement> resourcesList = getResourcesNamesWebElements();
        /*
        Iterating the list of Web elements founded by UI that contain the resouces names
         */
        for (WebElement temp : resourcesList) {
             resourcesName.add(temp.getText());
        }
        return resourcesName;
    }

    /**
     * Get all resources that make match with search criteria by DB
     * @return  An array list with the names of the resources
     */
    public ArrayList<String> getResourcesNameByDB(String searchCriteria){
        ArrayList<Resource> resourcesByDB=DBQuery.getInstance().getResourcesBySearchCriteria(searchCriteria);
        ArrayList<String> resourcesNameByDB= new ArrayList<String>();
        /*
        Get the names of the  array list of resouces getting by DB and fill it in array list of strings
         */
        for (Resource resourceTemp : resourcesByDB){
            resourcesNameByDB.add(resourceTemp.getName());
        }
        return resourcesNameByDB;
    }
    public ArrayList<String> getResourcesNameByApi(){
        ArrayList<Resource> resources = APIMethodsResource.getResources();
        ArrayList<String> names=new ArrayList<>();
        for (Resource resource : resources){
            names.add(resource.getName());
        }
        return names;
    }

    public ArrayList<String> searchResourceName(ArrayList<String> resourcesNames,String nameSearch){
        ArrayList<String> namesFounded=new ArrayList<>();
        for (String resourcesName :resourcesNames){
            if(nameSearch.equals(resourcesName)){
                namesFounded.add(resourcesName);
            }
        }
        return namesFounded;
    }
}