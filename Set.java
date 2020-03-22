import java.util.*;

/**
 * Set object used in the Action Rules Extractactor program
 * A set object has an ID, a list of values (strings), 
 * a variable that keeps track if the set its marked or not, and a boolean saying whether or not it is a decision attribute
 * Example set (a1)* = {x2, x3, x4, x6}  
 * id - In the above example, the id would be "a1" 
 * values - In the above example, the values would be {"x2", "x3", "x4", "x6"}
 * marked - denotes if a set is marked by a decision attribute or not
 * If so then it will be set to the ID of the decision attribue, otherwise it will be null; it defaults to null
 * isDecisionAttribute - True if the Set is a Decision Attribute, false otherwise; it defaults to false
 * isStableAttribute - True if the Set is a Stable Attribute, false otherwise; it defaults to false
 * @author Jane Hiltz (jhiltz3)
 */
class Set {
    
    //Initilizing variables
    String id;
    ArrayList<String> values; 
    String marked = null;
    Boolean isDecisionAttribute = false;
    Boolean isStableAttribute = false;

    /**
     * Constructor for the Set object 
     * @param id - String ID for set object
     * @param values - List of values (strings) associated with the id
     */
    Set(String id, ArrayList<String> values){
        this.id = id;
        this.values = values;
    }

    /// Getters ///
    //#region Getters
    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the values
     */
    public ArrayList<String> getValues() {
        return values;
    }

    /**
     * @return the marked
     */
    public String getMarked() {
        return marked;
    }

    /**
     * @return the isDecisionAttribute
     */
    public Boolean getIsDecisionAttribute() {
        return isDecisionAttribute;
    }

    /**
     * @return the isStableAttribute
     */
    public Boolean getIsStableAttribute() {
        return isStableAttribute;
    }

    //#endregion

    /// Setters ///
    //#region Setters
    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @param values the values to set
     */
    public void setValues(ArrayList<String> values) {
        this.values = values;
    }

    /**
     * @param marked the marked to set
     */
    public void setMarked(String marked) {
        this.marked = marked;
    }

    /**
     * @param isDecisionAttribute the isDecisionAttribute to set
     */
    public void setIsDecisionAttribute(Boolean isDecisionAttribute) {
        this.isDecisionAttribute = isDecisionAttribute;
    }

    /**
     * @param isStableAttribute the isStableAttribute to set
     */
    public void setIsStableAttribute(Boolean isStableAttribute) {
        this.isStableAttribute = isStableAttribute;
    }

    //#endregion

    /// Other Functions ///

    /**
     * Checks if this Set is a subset of another set (typically a decision attribute)
     * Set A is a subset of B if all values in A occur in B
     * @param decisionAttribute
     * @return
     */
    public boolean isSubset(Set decisionAttribute){
        
        //Loop through current set's values and see if they exist in the decisionAttribute's values
        for(String v : this.values){
            if(!decisionAttribute.values.contains(v)){
                return false;
            }
        }
        return true;
    }

    /**
     * Prints the set
     * Used for testing
     */
    public void printSet(){
        String line1 = "Set: (" + this.id + ")* = ";
        String valuesList = "{";
        for(int i = 0; i < this.values.size(); i++){
            if(i == 0){
                valuesList += this.values.get(i);
            } else {
                String temp = ", " + this.values.get(i);
                valuesList += temp;
            }
        }
        valuesList += "}";
        System.out.println(line1 + valuesList);
        if(this.marked == null){
            System.out.println("This set is unmarked");
        } else {
            System.out.println("This set is marked as a subset of " + this.marked);
        }
        if(this.isDecisionAttribute){
            System.out.println("This is a decision attribute");
        } else {
            System.out.println("This is not a decision attribute");
        }
        if(this.isStableAttribute){
            System.out.println("This is a stable attribute");
        } else {
            System.out.println("This is a flexible attribute");
        }
    }



}