package src.syncing;

import java.util.List;

/**
 * Class that contains the name and the date of a file or a folder.
 * It also contains the type of the file or folder and its path, which are used in some methods.
 * <br/>
 * Implements {@link java.io.Serializable} to be able to send it through a socket.
 * <br/>
 * Author: Geryes Doumit
 */
public class DateAndName implements java.io.Serializable {

    private static final long serialVersionUID = 1L;
    private String name;
    private String type;
    private Long date;
    private String path;

    /**
     * First constructor of the DateAndName class, if the path isn't needed.
     * 
     * @param name The name of the file or folder.
     * @param date The date of the file or folder.
     * @param type The type of the file or folder.
     */
    public DateAndName(String name, Long date, String type){
        this.name = name;
        this.date = date;
        this.type = type;
    }

    /**
     * Second constructor of the DateAndName class, if the path is needed.
     * 
     * @param name The name of the file or folder.
     * @param date The date of the file or folder.
     * @param type The type of the file or folder.
     * @param path The path of the file or folder.
     */
    public DateAndName(String name, Long date, String type, String path){
        this.name = name;
        this.date = date;
        this.type = type;
        this.path = path;
    }

    //Getters 

    /**
     * Get the name of the file or folder.
     * @return The name of the file or folder.
     */
    public String getName(){
        return this.name;
    }

    /**
     * Get the date of the file or folder.
     * @return The date of the file or folder.
     */
    public Long getDate(){
        return this.date;
    }

    /**
     * Get the type of the file or folder.
     * @return The type of the file or folder.
     */
    public String getType(){
        return this.type;
    }

    /**
     * Get the path of the file or folder.
     * @return The path of the file or folder.
     */
    public String getPath(){
        return this.path;
    }

    //Setters

    /**
     * Set the name of the file or folder.
     * @param name
     */
    public void setName(String name){
        this.name = name;
    }

    /**
     * Set the date of the file or folder.
     * @param date
     */
    public void setDate(Long date){
        this.date = date;
    }

    /**
     * Set the type of the file or folder.
     * @param type
     */
    public void setType(String type){
        this.type = type;
    }

    /**
     * Function to check if two DateAndName objects are equal.
     * <br/>
     * Compares the type first, then the name and the date if it's a file, and only the name if it's a folder.
     * @param document The DateAndName object to compare with.
     * @return True if the two objects are equal, false otherwise.
     */
    public Boolean equals(DateAndName document) {
        if (document.getType() != this.type) {
            return false;
        }

        else if (this.type == "File") {
            return this.name.equals(document.getName()) && this.date.equals(document.getDate());
        }

        else {
            return this.name.equals(document.getName());
        }
    }

    /**
     * Function to check if two lists of DateAndName objects are equal.
     * <br/>
     * Compares the size of the lists first, then compares each element of the lists.
     * @param list1 The first list to compare.
     * @param list2 The second list to compare.
     * @return True if the two lists are equal, false otherwise.
     */
    public static Boolean equalLists(List<DateAndName> list1, List<DateAndName> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }

        for (int i = 0; i < list1.size(); i++) {
            if(!list1.get(i).equals(list2.get(i))) {
                return false;
            }
        }

        return true;
    }

    /**
     * The toString method of the DateAndName class.
     * @return A string that contains the name and the date of the file or folder.
     */
    public String toString(){
        return "Name: " + this.name+" (Date: " + Long.toString(this.date) + ")";
    }
}
