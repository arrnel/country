package com.example.country.data.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.proxy.HibernateProxy;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(schema = "app", name = "country")
public class Country {

    @ToString.Include
    @With
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @ToString.Include
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @ToString.Include
    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @ToString.Include
    @Column(name = "date_created", nullable = false, unique = true)
    private Timestamp dateCreated;

    @ToString.Include
    @Column(name = "date_updated", nullable = false, unique = true)
    private Timestamp dateUpdated;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Country country = (Country) o;
        return getId() != null && Objects.equals(getId(), country.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

}
