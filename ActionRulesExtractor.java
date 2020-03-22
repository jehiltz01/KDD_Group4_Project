import java.io.*;
import java.util.*;

/**
 * Program that extracts action rules from data sets by performing the LERS algorithm on the data and extracting rules from the resulting certain rules
 * @author Jane Hiltz (jhiltz3) 
 */
class ActionRulesExtractor {

    //Hastable that holds the dataset 
    //It is comprised of Attributes (keys) and a list of Set objects (values)
    //For example, there may be an Attribute A with Sets a1, a2, and a5. 
    //Those sets would have lists of values of their own. For example, a2 might be a Set with values {"x1", "x5"}
    HashMap<String, ArrayList<Set>> datasetMap = new HashMap<String, ArrayList<Set>>();

    //List of attribute names
    ArrayList<String> attributeNames = new ArrayList<String>();

    /**
     * Parses the user input gathered from the GUI
     * @param dataFile File - contains the data to analyze
     * @param namesFile File - contains the names of the attributes of the data
     * @param delimiter String - the delimiter used to seperate data in the data file
     */
    public void parseInput(File dataFile, File namesFile, String delimiter){

        /// Names file parsing ///

        //Reads the names file line by line, assuming that each attribute name is on its own line
        BufferedReader namesReader;
        try{
            namesReader = new BufferedReader(new FileReader(namesFile));
            String line = namesReader.readLine();
            while(line != null){
                if(!line.isEmpty()){
                    String newAttribute = line.trim();
                    attributeNames.add(newAttribute);
                }
                line = namesReader.readLine();
            }
            namesReader.close();
        } catch(IOException e){
            e.printStackTrace();
        }
        /*
        System.out.println("attribute list length " + attributeNames.size());
        for(String name : attributeNames){
            System.out.println(name);
        }
        */

        /// Data file parsing //

        //Initilize 2D array list to hold data
        ArrayList<ArrayList<String>> dataContainer = new ArrayList<ArrayList<String>>();
        for(int i = 0; i < attributeNames.size(); i++){
            ArrayList<String> curAttribute = new ArrayList<String>();
            dataContainer.add(curAttribute);
        }

        //Parse file and store it in dataContainer
        BufferedReader dataReader;
        try{
            dataReader = new BufferedReader(new FileReader(dataFile));
            String line = dataReader.readLine();

            while(line != null){
                if(!line.isEmpty()){
                    //Parse each line by the delimiter where each value in the line belongs to an attribute
                    String[] lineDataArray;
                    //Split based on delimiter
                    if(delimiter == ","){
                       lineDataArray = line.split(",");
                    } else {
                        lineDataArray = line.split("\\s+");
                    }
                    List<String> lineDataList = Arrays.asList(lineDataArray);
                    
                    //Check if the data contains a null value, if so then discard/skip it
                    if(!lineDataList.contains("?") && !lineDataList.contains(" ") && !lineDataList.contains(null)){
                        for(int x = 0; x < lineDataList.size(); x++){
                            String dataPoint = lineDataList.get(x).trim();
                            dataContainer.get(x).add(dataPoint);
                        }
                    }
                }
                line = dataReader.readLine();
            }
            dataReader.close();
        } catch(IOException e){
            e.printStackTrace();
        }

        /*
        for(ArrayList<String> att : dataContainer){
            for(String val : att){
                System.out.print(val + " ");
            }
            System.out.println();
        }
        */

        /// Create Sets from data (stored in dataContainer) to then add to the hashmap ///

        //Ensure that the length of the dataContainer array is the same as the number of attributeNames
        if(attributeNames.size() != dataContainer.size()){
            System.out.println("Size missmatch between number of attribute names and columns in the data file");
            System.exit(1);
        }
        //Initilize the keys in the hashmap with the attribute names
        for(int i = 0; i < dataContainer.size(); i++){
            ArrayList<Set> setList = new ArrayList<Set>();
            String attName = attributeNames.get(i);
            
            //Populate the set lists
            for(int j = 0; j < dataContainer.get(i).size(); j++){
                //Value to add 
                String xVal = "x" + (j + 1);
                //Create sets
                int index = listContainsSet(setList, dataContainer.get(i).get(j));
                if(index >= 0){
                    setList.get(index).values.add(xVal);
                } else {
                    String newID = dataContainer.get(i).get(j);
                    ArrayList<String> newValues = new ArrayList<String>();
                    Set newSet = new Set(newID, newValues);
                    newSet.values.add(xVal);
                    setList.add(newSet);
                }
            }
            datasetMap.put(attName, setList);
        }

        printDatasetMap();

    }

    /**
     * Checks a list of Sets to see if it contains a Set with a certain id
     * @param list ArrayList<Set> - list of Sets to check through
     * @param id String - id to search for 
     * @return int - index of the found set; -1 if no set with corresponding id exists in the list
     */
    public static int listContainsSet(ArrayList<Set> list, String id){
        for(int i = 0; i < list.size(); i++){
            if(list.get(i).getId().equals(id)){
                return i;
            }
        }
        return -1;
    }

    /**
     * Prints the DatasetMap
     * Used for testing
     */
    public void printDatasetMap(){
        for(String name : attributeNames){
            System.out.println("Attribute: " + name);
            ArrayList<Set> curList = datasetMap.get(name);
            for(Set s : curList){
                s.printSet();
            }
            System.out.println();
        }
    }

}