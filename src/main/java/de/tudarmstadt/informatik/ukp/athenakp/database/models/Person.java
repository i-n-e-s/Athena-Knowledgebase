package de.tudarmstadt.informatik.ukp.athenakp.database.models;

import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Tristan Wettich
 */
@Entity(name = "person")
@Table(name = "person")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue(value = "person")
public class Person {

    /*Unique id*/
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "personID", updatable = false, nullable = false)
    private long personID;

    /*Prefixes like academic titles*/
    @Column(name = "prefix")
    private String prefix;
    /*First name*/
    @Column(name = "firstName", nullable = false)
    private String firstName;
    /*Middle name/s*/
    @Column(name = "middleName")
    private String middleName;
    /*Last name*/
    @Column(name = "lastName", nullable = false)
    private String lastName;

    /*Birthday and day of death*/
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "birthday")
    private Date birthdate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "obit")
    private Date obit;


    /*The person's institution, eg. an university or a company*/
    //@Column(name = "institution")
    @ManyToOne
    @JoinColumn(name = "institutionID")
    private Institution institution;


    /**
     * Gets the unique id of the person.
     * @return The unique id of the person
     */
    public long getPersonID()
    {
        return personID;
    }

    /**
     * Sets the person's id
     * @param id The new id
     */
    public void setPersonID(long id)
    {
        this.personID = id;
    }

    /**
     * Gets the person's prefixes as single String.
     * @return The person's prefixes
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Sets the person's prefixes as single String.
     * @param prefix The persons new prefixes
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Gets the person's first name.
     * @return Gets the person's first name.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the person's first name.
     * @param firstName The person's first name.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    /**
     * Gets the person's middle name(s).
     * @return Gets the person's middle name(s).
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * Sets the person's first name(s).
     * @param middleName The person's middle name(s).
     */
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }


    /**
     * Gets the person's last name.
     * @return Gets the person's last name.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the person's last name.
     * @param lastName The person's last name.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the person's birthday.
     * @return The person's birthday
     */
    public Date getBirthdate() {
        return birthdate;
    }

    /**
     * Sets the person's birthday
     * @param birthdate The person's birthday
     */
    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    /**
     * Gets the person's day of death.
     * @return The person's day of death
     */
    public Date getObit() {
        return obit;
    }

    /**
     * Sets the person's day of death.
     * @param obit The person's day of death
     */
    public void setObit(Date obit) {
        this.obit = obit;
    }

    /**
     * Gets the person's institution .
     * @return The person's institution
     */
    public Institution getInstitution() {
        return institution;
    }

    /**
     * Sets the person's institution.
     * @param institution The person's institution
     */
    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

}
