package edu.sdccd.cisc191.Common.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a user in the system, holding information about their
 * name, available money, money currently in bets, and a list of active bets.
 * Provides methods to manage bets and serialize/deserialize the user object.
 *
 * The class is designed to integrate seamlessly with JSON-based systems,
 * enabling data exchange and persistent storage.
 *
 * @author Andy Ly, Julian Garcia
 */

@Configuration
class JacksonConfigUser {
    @Bean
    public JodaModule jodaModule() {
        return new JodaModule();
        // TODO: Consider configuring additional modules if needed for date/time formats
    }
}

@Entity
@Table(name="users")
@Getter
@Setter
@ToString
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private long money;
    private int moneyLine; // Money placed in active bets but not yet resolved
    private int moneyBet; // Money available for future bets

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    //TODO: resolve the IDE warning here...
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private List<Bet> bets = new ArrayList<>();

    @JsonIgnore
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Serializes a User object into a JSON string.
     *
     * @param customer The User object to serialize.
     * @return A JSON string representation of the  User .
     * @throws Exception If serialization fails.
     */
    public static String toJSON(User customer) throws Exception {
        // TODO: Handle serialization exceptions more specifically
        return objectMapper.writeValueAsString(customer);
    }

    /**
     * Deserializes a JSON string into a  User  object.
     *
     * @param input The JSON string to deserialize.
     * @return A User object created from the JSON string.
     * @throws Exception If deserialization fails.
     */
    public static User fromJSON(String input) throws Exception {
        // TODO: Handle deserialization exceptions more specifically
        return objectMapper.readValue(input, User.class);
    }

    /**
     * Default constructor for  User .
     * Required for JSON serialization/deserialization.
     */
    public User() {
        // Default constructor for deserialization purposes
        // TODO: Initialize default values if needed
    }

    /**
     * Creates a new User with the specified name and initial money.
     * Initializes moneyLine to 0 and  moneyBet  equal to the initial money.
     *
     * @param name The name of the user.
     * @param money The initial amount of money the user has.
     */
    public User(String name, long money) {
        this.name = name;
        this.money = money;
        this.moneyLine = 0;
        this.moneyBet = Math.toIntExact(money);
        // TODO: Validate that money fits into int without loss
    }

    /**
     * Checks if the user has an active bet on the specified game.
     *
     * @param game The game to check for active bets.
     * @return true if an active bet exists for the game, otherwise false.
     */
    public boolean checkBet(Game game) {
        for (Bet bet : bets) {
            boolean result = bet.getGame().equals(game);
            // TODO: Consider using a stream or other optimized lookup if bets list grows large
            if (result) {
                return true;
            }
        }
        return false;
    }

    /**
     * Increments the user's total money.
     *
     * @param amt The amount of money to add to the user's balance.
     */
    public void incrMoney(int amt) {
        this.money += amt;
        // TODO: Consider adding validation to prevent overflow
    }

    /**
     * Decrements the user's total money.
     *
     * @param amt The amount of money to add to the user's balance.
     */
    public void decrMoney(int amt) {
        this.money -= amt;
        // TODO: Consider adding validation to prevent negative balance
    }

    /**
     * Adds a new bet to the user's list of active bets and updates the money balance accordingly.
     *
     * @param b The bet to add.
     */
    public void addBet(Bet b) {
        bets.add(b);
        moneyBet -= b.getBetAmt();
        moneyLine += b.getBetAmt();
        // TODO: Add checks to prevent negative moneyBet
    }

    /**
     * Removes a bet from the user's list of active bets.
     *
     * @param b The bet to remove.
     */
    public void removeBet(Bet b) {
        bets.remove(b);
        // TODO: Consider adjusting moneyLine and moneyBet accordingly when a bet is removed
    }

    // IDE auto-generated code
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        User user = (User) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
