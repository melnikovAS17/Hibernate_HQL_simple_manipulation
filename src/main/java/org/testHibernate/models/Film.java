package org.testHibernate.models;

import javax.persistence.*;

@Entity
@Table(name = "film")
public class Film {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "director_id", referencedColumnName = "id")
    private Director director;

    @Column(name = "name")
    private String name;

    public Film(){}

    public Film(Director director, String name) {
        this.director = director;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Director getDirector() {
        return director;
    }

    public void setDirector(Director director) {
        this.director = director;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Film{" +
                "id=" + id +
                ", director=" + director +
                ", name='" + name + '\'' +
                '}';
    }
}
