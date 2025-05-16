package org.demo.baoleme.entity;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Product product;

    @ManyToOne
    private Store store;

    private LocalDateTime createTime;
    private LocalDateTime predictTime;
    private boolean isCurrent;

    // getters and setters
}