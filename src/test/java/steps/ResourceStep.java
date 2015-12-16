package steps;

import common.EnumOptions;
import common.SetUpResources;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import entities.ConferenceRooms;
import entities.Resource;
import framework.APIManager;
import framework.DBManager;
import framework.DBQuery;
import org.testng.Assert;
import ui.BaseMainPageObject;
import ui.PageTransporter;
import ui.pages.*;
import ui.pages.tablet.LoginTablePage;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: BrayanRosas
 * Date: 12/8/15
 * Time: 9:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class ResourceStep {

    private BaseMainPageObject mainPage;
    private SidebarMenuPage sidebar;
    private LoginPage loginPage;
    private ResourcePage resourcePage;
    private AddResourcePage addResourcePage;
    private Resource resource1;
    private ResourceAssociationsPage resourceAssociationsPage;
    private SetUpResources setupResources;

//    private ConferenceRooms conferenceRooms;
//    private ConferenceRoomsPage conferenceRoomsPage;

    private ArrayList<Resource> resourcesCreated=new ArrayList<>();
    private ArrayList<Resource> resourcesCreatedByGiven=new ArrayList<>();

    public ResourceStep(Resource resource1){
        this.resource1=resource1;
        mainPage=new BaseMainPageObject();
        sidebar=mainPage.getSideBarMenu();
    }

    @Given("^I create a Resource with: \"([^\\\"]*)\" Name$")
    public void createResources(String resourceNames){
        ArrayList<String> resourcesNameArray = new ArrayList<String>();
        for (String nameResourceTemp : resourceNames.split(",")){
            resourcesNameArray.add(nameResourceTemp);
        }
       resourcesCreatedByGiven= APIManager.getInstance().createResourcesByName(resourcesNameArray);
        PageTransporter.getInstance().refreshPage();
        PageTransporter.getInstance().fixRefreshIsue();
        System.out.println("*********"+resourcesNameArray);
    }
    @And("^I navigate to Resources page$")
    public void goToResourcePage(){
       mainPage.getSideBarMenu().clickOptionResource();
    }

    @When("^I try to create the Resource Name \"([^\\\"]*)\", \"([^\\\"]*)\" in the Resource page$")
    public void tryToCreateResource(String name,String displayName){
        resourcePage=sidebar.clickOptionResource();
        addResourcePage=resourcePage.clickAddButton();
        resource1.setName(name);
        resource1.setDisplayName(displayName);
        addResourcePage.createResource(resource1);
    }

    @Then("^an error text \"([^\\\"]*)\" is showed in the Resource form$")
        public void errorMessageIsShowed(String message){
        boolean expected=true;
        Assert.assertEquals(addResourcePage.isMessageShowed(message), expected);
    }

    @When("^I navigate to Resources page from AddResource$")
    public void navigateToResourcePageFromAddResourcePage(){
        addResourcePage.clickCancelResourceButton();
    }

    @Then("^only one Resource with the same name should be displayed in Resource list$")
    public void existTwoResourceSameName(){
        boolean expected=false;
        Assert.assertEquals(resourcePage.moreThatTwoResourceSameName(resource1), expected);
    }

    @And("^only one Resource with name \"([^\\\"]*)\" should be obtained by API$")
    public void existTwoResourcesSameNameByApi(String resourceName){
        Assert.assertEquals(resourcePage.searchResourceName(resourcePage.getResourcesNameByApi(),resourceName).size(),1);
    }

    @And("^create a meeting$")
    public void createMeeting(){




        String organizer ="jhasmany.quiroz";
        String title ="MetingAPI2";
        String start ="2015-12-16T01:00:00.000Z";
        String end   ="2015-12-16T01:30:00.000Z";
        String location="Floor1Room56";
        String roomEmail="Floor1Room56@forest1.local";
        String resources="Floor1Room56@forest1.local";
        String attendees ="jhasmany.quiroz@forest1.local";
        String idRoom="565f3f459c27d64812f72b28";




        APIManager.getInstance().createMeeting(organizer,title,start,end,location,roomEmail,resources,attendees,idRoom);


    }

     @When("^I search Resources with search criteria \"([^\\\"]*)\"$")
        public void searchResource(String searchCriteria){
            resourcePage=sidebar.clickOptionResource();
            resourcePage.filterResource(searchCriteria);
        }

    @Then("^the Resources that match the search criteria \"([^\\\"]*)\" should be displayed in Resource List$")
    public void numResourcesFilter(String searchCriteria){
        Assert.assertEqualsNoOrder(resourcePage.getResourcesNameByUI().toArray(),resourcePage.getResourcesNameByDB(searchCriteria).toArray());
    }

    @When("^I delete the Resource \"([^\\\"]*)\"$")
    public void deleteResourceByName(String resourceName){
        resourcePage=sidebar.clickOptionResource();
        resourceAssociationsPage=resourcePage.clickDeleteResource(resourceName);
        resourceAssociationsPage.deleteButtonConfirm();
    }

    @Then("^the Resource \"([^\\\"]*)\" should not be displayed in the Resources list$")
    public void isDisplayed(String resourceName){
        Assert.assertEquals(resourcePage.existInColumnName(resourceName),false);

    }

    @And("^the Resource \"([^\\\"]*)\" should not be obtained using the API$")
    public void theResourceIsPresentInAPI(String resourceName){
        String idResource=DBQuery.getInstance().getIdByKey("resourcemodels","name",resourceName);
        Resource res1=APIManager.getInstance().getResourceByID(idResource) ;
        Assert.assertNull(res1.getName());

    }

    @Given("^Delete resource$")
    public void deleteResources(){
       APIManager.getInstance().deleteResourcesById(resourcesCreatedByGiven);
    }
    /**
     * After and before scenario to delete and create resources
     * @return void
     */
    @Before("@Resources")
    public void createResource(){
        setupResources=new SetUpResources();
       resource1= setupResources.beforeResourceFeature("Computer") ;
    }

    @After("@Resources")
    public void deleteResourcesByFeature(){
       resourcesCreated.add(resource1);
        setupResources.afterResourceFeature(resourcesCreated);
    }

    @After("@ResourceFilter")
    public void deleteResourcesByScenario(){
        APIManager.getInstance().deleteResourcesById(resourcesCreatedByGiven);
    }
}
