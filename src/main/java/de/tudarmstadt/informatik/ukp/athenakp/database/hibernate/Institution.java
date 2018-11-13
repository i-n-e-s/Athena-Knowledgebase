package de.tudarmstadt.informatik.ukp.athenakp.database.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="institutions")
public class Institution {
    private String name;


    /**
     * Gets the name of this Institution
     *
     * @return The name of this Institution
     */
    @Column(name = "name", nullable = false)
    public String getName() {
        return name;
    }

    /**
     * Sets this institution's name
     *
     * @param name The new name
     */
    public void setName(String name) {
        this.name = name;
    }

}