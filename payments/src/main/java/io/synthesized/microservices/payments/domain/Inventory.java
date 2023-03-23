package io.synthesized.microservices.payments.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "inventory")
@Getter @Setter @NoArgsConstructor
public class Inventory implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id", nullable = false)
    private Long inventoryId;

    @Column(name = "film_id", nullable = false)
    private Integer filmId;

    @Column(name = "store_id", nullable = false)
    private Integer storeId;

    @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Rental> rentals = new HashSet<>();

    // Add and remove rental methods for managing bi-directional relationship
    public void addRental(Rental rental) {
        rentals.add(rental);
        rental.setInventory(this);
    }

    public void removeRental(Rental rental) {
        rentals.remove(rental);
        rental.setInventory(null);
    }
}
