package src.syncing;

import java.util.List;

public class DateAndName {
    private String name;
    private String type;
    private Long date;

    public DateAndName(String name, Long date, String type){
        this.name = name;
        this.date = date;
        this.type = type;
    }

    public String getName(){
        return this.name;
    }

    public Long getDate(){
        return this.date;
    }

    public String getType(){
        return this.type;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setDate(Long date){
        this.date = date;
    }

    public void setType(String type){
        this.type = type;
    }

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

    public static Boolean equalLists(List<DateAndName> list1, List<DateAndName> list2) {
        if (list1.size() != list2.size()) {
            // System.out.println("Different size !");
            return false;
        }

        for (int i = 0; i < list1.size(); i++) {
            if(!list1.get(i).equals(list2.get(i))) {
                // System.out.println("member " + Integer.toString(i)+" different !");
                return false;
            }
        }

        return true;
    }

    public String toString(){
        return "Name: " + this.name+" (Date: " + Long.toString(this.date) + ")";
    }
}
