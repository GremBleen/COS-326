package prac1;

import javax.persistence.*;

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
        emf = Persistence.createEntityManagerFactory("./db/transactions.odb");
        em = emf.createEntityManager();
        return em;
    }

    public void closeEM() {
        em.close();
        emf.close();
    }
}
