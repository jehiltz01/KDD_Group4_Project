package com.uncc.kdd;
/**
 * Author: Surabhi Suresh Gurav 
 * 
 * This class handles the logic for the action rules calculation 
 */
import com.uncc.kdd.LERS_Algo;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class ActionRulesLogic extends Observable implements Runnable, Observer 
{
	
	private static final String LINESEPARATOR = System.getProperty("line.separator");
	private List<String> attributes;
	private Map<String, ArrayList<String>> data;
	private String initialDecisionVals;
	private String decisionValues;
	private HashSet<String> stableAttributes;
	private HashSet<String> flexibleAttributes;
	private int minimumSupport;
	private int minimumConfidence;
	private LERS_Algo lers;

        private Map<String, HashSet<String>> distinctAttrVals;

	private Map<String, HashSet<String>> attributeVals; 
        
	private Map<String, HashSet<HashSet<String>>> certainRules = new HashMap<String, HashSet<HashSet<String>>>();

	private Map<ArrayList<String>, ArrayList<String>> actionRules;
	private Map<ArrayList<String>, ArrayList<Integer>> ruleSuppConf;

	
	/**
	 * Initializing Attributes
	 */
	public ActionRulesLogic() {
		attributes = new ArrayList<String>();
		data = new HashMap<String, ArrayList<String>>();
		actionRules = new HashMap<ArrayList<String>, ArrayList<String>>();
		distinctAttrVals = new HashMap<String, HashSet<String>>();
		attributeVals = new HashMap<String, HashSet<String>>();
		ruleSuppConf = new HashMap<ArrayList<String>, ArrayList<Integer>>();
	}

	/**
	 * @param header file of header attribute names
	 * @param inFile file of data in order of the attribute header names
	 */
	public void readFile(File header, File inFile) {
		String line;
		int lineNum = 0;
		String[] lineData;
		String currentValue;
		HashSet<String> tempSet = new HashSet<String>();
		String currentKey;

		try(BufferedReader reader = new BufferedReader((new FileReader(header)))) {
			while((line = reader.readLine()) != null) {
				//Read Head
				for(String partition : line.split(",|\t")) {
					attributes.add(partition);
				}
			}
		}catch(IOException e) {
			System.out.println(e.getMessage());
		}
		
		try(BufferedReader reader = new BufferedReader((new FileReader(inFile)))) {
                        /*
                        Reading Till EOL
                        */
			while((line = reader.readLine()) != null) {
				lineNum++;
				
				lineData = line.split(",|\t");
				data.put("x" + Integer.toString(lineNum), new ArrayList<String>(Arrays.asList(lineData)));
				
				for(int i = 0; i < lineData.length; i++) {
					//If data doesn't exist, skip the entry
					if(lineData[i].equals("?")) {
						continue;
					}
					
					//get distinct attributes
					currentValue = lineData[i];
					
					
					tempSet = distinctAttrVals.get(attributes.get(i));
					
					//Distinct attribute value is not recorded yet
					if(tempSet == null) {
						tempSet = new HashSet<String>();
						tempSet.add(currentValue);
						distinctAttrVals.put(attributes.get(i), tempSet);
					}else {
						tempSet = new HashSet<String>();
						tempSet.addAll(distinctAttrVals.get(attributes.get(i)));
						tempSet.add(currentValue);
						distinctAttrVals.put(attributes.get(i), tempSet);
						
					}
										
					//set data sets to attribute values
					currentKey = attributes.get(i) + currentValue;
					
					if(attributeVals.containsKey(currentKey)) {
						tempSet = attributeVals.get(currentKey);
						tempSet.add("x" + lineNum);
						attributeVals.put(currentKey, tempSet);
					}else { //If this is the first time the value has been seen
						tempSet = new HashSet<String>();
						tempSet.add("x" + lineNum);
						attributeVals.put(currentKey, tempSet);
					}
				}
			}
			
		} catch(IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * @param inFile : Reads file with header included
	 */
	public void readFile(String inFile) {
		String line;
		int lineNum = 0;
		String[] lineData;
		String currentValue;
		HashSet<String> tempSet = new HashSet<String>();
		String currentKey;
		
		
		try(BufferedReader reader = new BufferedReader((new FileReader(inFile)))) {
			//Read Head
			line = reader.readLine();
			for(String partition : line.split(",|\t")){
				attributes.add(partition);
			}
			
			//Read Data until EOF
			while((line = reader.readLine()) != null) {
				lineNum++;
				
				lineData = line.split(",|\t");
				data.put("x" + Integer.toString(lineNum), new ArrayList<String>(Arrays.asList(lineData)));
				
				for(int i = 0; i < lineData.length; i++) {
					//If data doesn't exist, skip the entry
					if(lineData[i].equals("?")) {
						continue;
					}
					
					//get distinct attributes
					currentValue = lineData[i];
					
					
					tempSet = distinctAttrVals.get(attributes.get(i));
					
					//Distinct attribute value is not recorded yet
					if(tempSet == null) {
						tempSet = new HashSet<String>();
						tempSet.add(currentValue);
						distinctAttrVals.put(attributes.get(i), tempSet);
					}else {
						tempSet = new HashSet<String>();
						tempSet.addAll(distinctAttrVals.get(attributes.get(i)));
						tempSet.add(currentValue);
						distinctAttrVals.put(attributes.get(i), tempSet);
						
					}
										
					//set data sets to attribute values
					currentKey = attributes.get(i) + currentValue;
					
					if(attributeVals.containsKey(currentKey)) {
						tempSet = attributeVals.remove(currentKey);
						tempSet.add("x" + lineNum);
						attributeVals.put(currentKey, tempSet);
					}else { //If this is the first time the value has been seen
						tempSet = new HashSet<String>();
						tempSet.add("x" + lineNum);
						attributeVals.put(currentKey, tempSet);
					}
				}
			}
			
		} catch(IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Calculates action rules 
	 */
	public void calculateActionRules() {
		setChanged();
		notifyObservers("Generating Action Rules...." + LINESEPARATOR);
		
		certainRules = lers.getCertainRules();
		HashSet<HashSet<String>> decisionToSets  = certainRules.get(decisionValues);
		HashSet<String> toSet;
		Iterator<HashSet<String>> toIterator;
		ArrayList<String> toActionSet;
		List<String> header;
		List<String> headerSupportSearch;
		List<String> headerOccurrences;
		List<String> headerNames;
		ArrayList<String> attributeTest;
		List<String> attributeTestOccurrences;
		
		if(decisionToSets != null && !decisionToSets.isEmpty()) {
			toIterator = decisionToSets.iterator();
			//Check all certain rule sets for the decision value
			while(toIterator.hasNext()) {
				header = new ArrayList<String>();
				headerOccurrences = new ArrayList<String>();
				toSet = toIterator.next();
				ArrayList<String> toNames = new ArrayList<String>();
				headerNames = new ArrayList<String>();
				
				//TODO What about times when the to rule has no header?
				header = getHeader(toSet);
				for(String attribute : header) {
					headerNames.add(deriveAttributeName(attribute));
				}
				
				for(String attribute : toSet) {
					toNames.add(deriveAttributeName(attribute));
				}
				
				headerSupportSearch = new ArrayList<String>();
				headerSupportSearch.addAll(header);
				headerSupportSearch.add(initialDecisionVals);
				headerOccurrences = findOccurrences(headerSupportSearch);
				
				for(Map.Entry<String, HashSet<String>> entry : distinctAttrVals.entrySet()) {
					if(headerNames.contains(entry.getKey()) || entry.getKey().equals(deriveAttributeName(decisionValues))) 
							continue;
					
					for(String potentialRuleValue : entry.getValue()) {
						if(toSet.contains(entry.getKey() + potentialRuleValue)) 
							continue;
						
						attributeTest = new ArrayList<String>();
						attributeTest.addAll(header);
						attributeTest.add(entry.getKey() + potentialRuleValue);
						attributeTestOccurrences = findOccurrences(attributeTest);
						
						if(!attributeTestOccurrences.isEmpty() && headerOccurrences.containsAll(attributeTestOccurrences)) {
							if(toNames.contains(entry.getKey())) {
								toActionSet = new ArrayList<String>(toSet);
								addActionRule(attributeTest, toActionSet);
							}else {
								toActionSet = new ArrayList<String>(toSet);
								toActionSet.add(entry.getKey() + potentialRuleValue);
								addActionRule(attributeTest, toActionSet);
							}
						}
					}
				}
			}
		}
	}
	

	/**
	 * @param fromAction original from portion of action rule
	 * @param toAction final portion of action rule
	 */
	private void addActionRule(ArrayList<String> fromAction, ArrayList<String> toAction) {
		ArrayList<String> fromTemp = new ArrayList<String>();
		ArrayList<String> toTemp = new ArrayList<String>();

		fromTemp.addAll(fromAction);
		fromTemp.removeAll(getHeader(fromAction));
		toTemp.addAll(toAction);
		toTemp.removeAll(getHeader(toAction));
		
		boolean add = true;
		
		//Don't accept duplicates
		for(Map.Entry<ArrayList<String>, ArrayList<String>> entry : actionRules.entrySet()) {
			if(entry.getKey().equals(fromAction) && entry.getValue().equals(toAction))
				add = false;
		}
		
		//Only add if minimum supp/conf met
		if(!checkSupportConfidence(fromAction, toAction)) {
			add = false;
		}
		
		if(add)
			actionRules.put(fromAction, toAction);
	}

	/**
	 * Output Generator Function
	 */
	public void printActionRules() {
		ArrayList<String> toAction;
		ArrayList<String> fromAction;
		ArrayList<String> fromNames;
		ArrayList<String> toNames;
		List<String> header;
		NumberFormat formatter = new DecimalFormat("#0.00");
		String result = "Action Rules: " + LINESEPARATOR;
		Path file = Paths.get("output.txt");
		
		try(BufferedWriter writer = Files.newBufferedWriter(file, StandardOpenOption.APPEND))  {
			if(actionRules.isEmpty()) {
				result += "No action rules found";
				setChanged();
				notifyObservers(result);
				
				writer.write(result);
			}
				
			for(Map.Entry<ArrayList<String>, ArrayList<String>> entry : actionRules.entrySet()) {
				fromAction = new ArrayList<String>();
				toAction = new ArrayList<String>();
				fromNames = new ArrayList<String>();
				toNames = new ArrayList<String>();
				
				fromAction.addAll(entry.getKey());
				toAction.addAll(entry.getValue());
				
				header = getHeader(toAction);
				
				for(String attribute : header) {
					if(result.isEmpty() || result.equals("Action Rules: " + LINESEPARATOR))
						result += "[(" + attribute + ")";
					else
						result += "^" + "(" + attribute + ")";
				}
				
				fromAction.removeAll(header);
				toAction.removeAll(header);
				
				//Add flexibleAttributes attributes
				for(String name : fromAction) {
					fromNames.add(deriveAttributeName(name));
				}
				
				for(String name : toAction) {
					toNames.add(deriveAttributeName(name));
				}
				
				for(String name : toNames) {
					if(fromNames.contains(name)) {
						String fromValue = fromAction.get(fromNames.indexOf(name));
						String toValue = toAction.get(toNames.indexOf(name));
						
						if(result.isEmpty()) { 
							if(fromValue.equals(toValue)) //When there is no change to flexibleAttributes attribute
								result += "[(" + toValue + ")";
							else
								result += "[(" + name + ", " + fromValue + "-->" + toValue + ")";
						}else {
							if(fromValue.equals(toValue))
								result += "^(" + toValue + ")";
							else
								result += "^(" + name + ", " + fromValue + "-->" + toValue + ")";
						}
					}else {
						if(result.isEmpty()) {
							result += "[(" + name + ", " + "-->" + toAction.get(toNames.indexOf(name)) + ")";
						}else {
							result += "^(" + name + ", " + "-->" + toAction.get(toNames.indexOf(name)) + ")";
						}
					}
				}
                                
				result += "] --> (" + deriveAttributeName(initialDecisionVals) + ", " + initialDecisionVals + 
						"-->" + decisionValues + ")";
				
				ArrayList<Integer> suppConf = ruleSuppConf.get(entry.getKey());
				result += "\tSupport: " + suppConf.get(0);
				result += "\tConfidence: " + formatter.format((suppConf.get(1))) + "%" + LINESEPARATOR;
				setChanged();
				notifyObservers(result);
				
				
				writer.write(result);
				result = "";
			}
		}catch (IOException error) {
		    System.out.println(error.getStackTrace());
		}
	}
	
	/**
	 * @param support : Minimum Value for Support
	 * @param confidence : Minimum Value for Confidence
	 */
	public void setMinSupportConfidence(int support, int confidence) {
		minimumSupport = support;
		minimumConfidence = confidence;
	}
	
	/**
         * Checking Support Confidence
	 * @param fromAction
	 * @param toAction
	 * @return true if the rule has the min support/confidence
	 */
	public boolean checkSupportConfidence(ArrayList<String> fromAction, ArrayList<String> toAction) {
		boolean add = true;
		int supportFrom = 0;
		int supportFromDec = 0;
		int supportTo = 0;
		int supportToDec = 0;
		int support;
		int confidence;
		HashSet<String> supportSet = new HashSet<String>();
		HashSet<String> temp;
		HashSet<String> remove;
		
		//Find support of from attributes
		for(String attribute : fromAction) {
			temp = attributeVals.get(attribute);
			
			if(supportSet.isEmpty()) {
				supportSet.addAll(temp);
				continue;
			}else {
				remove = new HashSet<String>();
				for(String potentialLine : supportSet) {
					if(!temp.contains(potentialLine))
						remove.add(potentialLine);
				}
				
				supportSet.removeAll(remove);
				
				if(supportSet.isEmpty())
					break;
			}
		}
		supportFrom = supportSet.size();
                
		temp = attributeVals.get(initialDecisionVals);
		
		remove = new HashSet<String>();
		for(String potentialLine : supportSet) {
			if(!temp.contains(potentialLine))
				remove.add(potentialLine);
		}
		
		supportSet.removeAll(remove);
		supportFromDec = supportSet.size();
		
		//Finding Support Set
		supportSet = new HashSet<String>();
		for(String attribute : toAction) {
			temp = attributeVals.get(attribute);
			
			if(supportSet.isEmpty()) {
				supportSet.addAll(temp);
				continue;
			}else {
				remove = new HashSet<String>();
				for(String potentialLine : supportSet) {
					if(!temp.contains(potentialLine))
						remove.add(potentialLine);
				}
				
				supportSet.removeAll(remove);
				
				if(supportSet.isEmpty())
					break;
			}
		}
		supportTo = supportSet.size();
		
		temp = attributeVals.get(decisionValues);
		remove = new HashSet<String>();
		for(String potentialLine : supportSet) {
			if(!temp.contains(potentialLine))
				remove.add(potentialLine);
		}
		
		supportSet.removeAll(remove);
		supportToDec = supportSet.size();
		
		if(supportFromDec < supportToDec)
			support = supportFromDec;
		else
			support = supportToDec;
		
		if(supportFrom == 0 || supportTo == 0) 
			confidence = 0;
		else
			confidence = (supportFromDec/supportFrom) * (supportToDec/supportTo) * 100;
		
		if(support < minimumSupport)
			add = false;
		
		if(confidence < minimumConfidence)
			add = false;
		
		if(add) {
			ArrayList<Integer> suppConf = new ArrayList<Integer>();
			suppConf.add(support);
			suppConf.add(confidence);
			ruleSuppConf.put(fromAction, suppConf);
		}
		
		return add;
	}
	
	/**
	 * @param supportSearch List of attribute values to search for
	 * @return Lines on which the Set occur
	 */
	public List<String> findOccurrences(List<String> supportSearch){
		HashSet<String> temp = new HashSet<String>();
		ArrayList<String> supportSet = new ArrayList<String>();
		HashSet<String> remove = new HashSet<String>();
		
		for(String attribute : supportSearch) {
			temp = attributeVals.get(attribute);
			
			if(supportSet.isEmpty()) { 
				supportSet.addAll(temp);
				continue;
			}else {
				remove = new HashSet<String>();
				for(String potentialLine : supportSet) {
					if(!temp.contains(potentialLine))
						remove.add(potentialLine);
				}
				
				supportSet.removeAll(remove);
				
				if(supportSet.isEmpty())
					break;
			}
		}
		return supportSet;
	}
	
	/**
	 * @param set : Header Set
	 * @return : Header of a Set
	 */
	private List<String> getHeader(ArrayList<String> set) {
		ArrayList<String> header = new ArrayList<String>();
		
		for(String attribute: set) {
			if(stableAttributes.contains(deriveAttributeName(attribute)))
					header.add(attribute);
		}
		
		return header;
	}
	
	/**
	 * @param set : Header Set
	 * @return : Header of a Set
	 */
	private List<String> getHeader(HashSet<String> set) {
		ArrayList<String> header = new ArrayList<String>();
		
		for(String attribute: set) {
			if(stableAttributes.contains(deriveAttributeName(attribute)))
					header.add(attribute);
		}
		
		return header;
	}
	
	/**
	 * @param value : attributename + attributeValue
	 * @return value of the attributeName+attributeValue
	 */
	public String deriveAttributeValue(String value) {
		String aValue = "";
		
		for(String currName : attributes) {
			if(value.startsWith(currName)) {
				aValue = value.substring(currName.length());
				break;
			}
		}
		
		return aValue;
	}
	
	/**
	 * @param value : attribute name + attribute value
	 * @return :attribute name 
	 */
	public String deriveAttributeName(String value) {
		String name = "";
		
		for(String currName : attributes) {
			if(value.startsWith(currName)) {
				name = currName;
				break;
			}
		}
		
		return name;		
	}
	
	/**
	 * @return List of attribute names
	 */
	public List<String> getAttributeNames() {
		return attributes;
	}

	/**
	 * @return Mapping of distinct attribute values. Key = attribute name, value = attribute value
	 */
	public Map<String, HashSet<String>> getDistinctAttributeValues() {
		return distinctAttrVals;
	}
	
	/**
	 * @param key attribute name
	 * @return List of Distinct Attributes Values for attribute Name.
	 */
	public HashSet<String> getDistinctAttributeValues(String key){
		return distinctAttrVals.get(key);
	}

	/**
	 * that are not stableAttributes
	 * @param stableAttributes set of all stableAttributes attributes
	 */
	public void setStableFlexible(HashSet<String> stableAttributes) {
		this.stableAttributes = stableAttributes;
		
		flexibleAttributes = new HashSet<String>();
		for(String name : attributes) {
			if(!stableAttributes.contains(name)) {
				flexibleAttributes.add(name);
			}
		}
	}

	/**
	 * @param initialDecisionVals initial decision
	 * @param decisionValues final decision
	 */
	public void setDecisionAttributes(String initialDecisionVals, String decisionValues) {
		this.initialDecisionVals = initialDecisionVals;
		this.decisionValues = decisionValues;
	}
	
	/**
	 * Runs LERS based on given decision values
	 */
	public void run() {
		lers = new LERS_Algo(initialDecisionVals, decisionValues, attributeVals, 
				distinctAttrVals, attributes);
		lers.addObserver(this); 
		lers.runLERSLogic();
		 
		 calculateActionRules();
		 printActionRules();
	}

	@Override
	public void update(Observable o, Object arg) {
		setChanged();
		notifyObservers(arg);
	}
	
	
}
