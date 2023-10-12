package de.sotterbeck.iumetro.dataprovider.postgres.ticketusage;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "metro_stations", schema = "public", catalog = "postgres")
public class MetroStationMapper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "name")
    private String name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetroStationMapper that = (MetroStationMapper) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
