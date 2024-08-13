package cos326demo2;

import java.io.Serializable;
import javax.persistence.*
/* AddressBook Class
 Author: Makura S.M
 */

@Entity
public class AddressBook implements Serializable {
    //The serialVersionUID is a universal version identifier for a Serializable class
    private static final long serialVersionUID = 1L;
    //This annotation specifies the primary key of the entity
    @Id
    //generate primary key value automatically
    @GeneratedValue
    private long id;
    private String name;
    private String address;

    public AddressBook() {

    }
    public AddressBook(String n, String a) {
        this.name = n;
        this.address = a;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String n) {
        this.name = n;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String a) {
        this.address = a;
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)",
            this.name, this.address);
    }
}
