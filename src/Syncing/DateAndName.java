package src.syncing;

import java.util.List;

public class DateAndName {
    private String name;
    private Long date;

    public DateAndName(String name, Long date){
        this.name = name;
        this.date = date;
    }

    public String getName(){
        return this.name;
    }

    public Long getDate(){
        return this.date;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setDate(Long date){
        this.date = date;
    }

    public Boolean equals(DateAndName document) {
        if (this.name.equals(document.name) ) {
            if (this.date.equals(document.date)) {
                return true;
            }
            else {
                System.out.println("Not equal, different date.");
                return false;
            }
        }

        else {
            System.out.println("Not equal, different name.");
                return false;
        }
    }

    public static Boolean equalLists(List<DateAndName> list1, List<DateAndName> list2) {
        if (list1.size() != list2.size()) {
            System.out.println("Different size !");
            return false;
        }

        for (int i = 0; i < list1.size(); i++) {
            if(!list1.get(i).equals(list2.get(i))) {
                System.out.println("member " + Integer.toString(i)+" different !");
                return false;
            }
        }

        return true;
    }

    public String toString(){
        return "Name: " + this.name+" (Date: " + Long.toString(this.date) + ")";
    }
}
