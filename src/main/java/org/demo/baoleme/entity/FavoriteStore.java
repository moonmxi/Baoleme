package org.demo.baoleme.entity;

@Entity
@Table(name = "favorite_stores")
public class FavoriteStore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Store store;

}