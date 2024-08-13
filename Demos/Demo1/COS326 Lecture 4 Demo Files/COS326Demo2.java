package cos326demo2;


import java.util.List;
import javax.persistence.*
/* COS326 Demo 2
 Author: Makura S.M
 */
public class COS326Demo2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Open a database connection
        // (create a new database if it doesn't exist yet):
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("$objectdb/db/addressbook.odb");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        // add names to the addressbook
        AddressBook b1 = new AddressBook("Cyril Ramaphosa", "Pretoria");
        AddressBook b2 = new AddressBook("Jacob Zuma", "Johannesburg");
        AddressBook b3 = new AddressBook("Kgalema Motlanthe", "Polokwane");
        AddressBook b4 = new AddressBook("Thabo Mbeki", "Durban");
        AddressBook b5 = new AddressBook("Nelson Mandela", "Eastern Cape");
        em.persist(b1);
        em.persist(b2);
        em.persist(b3);
        em.persist(b4);
        em.persist(b5);

        em.getTransaction().commit();
        // Retrieve all the Addressbook objects from the database:
        TypedQuery < AddressBook > query = em.createQuery("SELECT b FROM AddressBook b", AddressBook.class);
        List < AddressBook > results = query.getResultList();

        for (AddressBook bb: results) {
            System.out.println(bb);
        }
        // Close the database connection:
        em.close();
        emf.close();
    }
    
}
