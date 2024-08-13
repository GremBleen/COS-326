package prac2;

import javax.persistence.*;
import java.util.HashMap;

public class ObjectDBManager {
    public static ObjectDBManager instance = null;
    private EntityManagerFactory emf;
    private EntityManager em;

    private ObjectDBManager() {
    }

    public static ObjectDBManager getInstance() {
        if (instance == null) {
            instance = new ObjectDBManager();
        }
        return instance;
    }

    public EntityManager getEM() {
        emf = Persistence.createEntityManagerFactory("objectdb:db/transactions.odb");
        em = emf.createEntityManager();
        return em;
    }

    public EntityManager getDockerEM() {
        HashMap<String, String> properties = new HashMap<String, String>();
        properties.put("javax.persistence.jdbc.user", "admin");
        properties.put("javax.persistence.jdbc.password", "admin");

        emf = Persistence.createEntityManagerFactory("objectdb://localhost:6136/transactions.odb", properties);
        em = emf.createEntityManager();
        return em;
    }

    public void closeEM() {
        em.close();
        emf.close();
    }
}
