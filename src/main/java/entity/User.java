package entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;
    
    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;
    
    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;
    
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;
    
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;
    
    @Column(name = "phone", length = 20)
    private String phone;
    
    @Column(name = "address_line1", length = 100)
    private String addressLine1;
    
    @Column(name = "address_line2", length = 100)
    private String addressLine2;
    
    @Column(name = "city", length = 50)
    private String city;
    
    @Column(name = "state", length = 50)
    private String state;
    
    @Column(name = "zip_code", length = 10)
    private String zipCode;
    
    @Column(name = "country", length = 50)
    private String country = "USA";
    
    @Enumerated(EnumType.STRING)
    @Column(name = "user_type")
    private UserType userType = UserType.BOTH;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "account_status")
    private AccountStatus accountStatus = AccountStatus.ACTIVE;
    
    @Column(name = "registration_date")
    private Timestamp registrationDate;
    
    @Column(name = "last_login")
    private Timestamp lastLogin;
    
    @Column(name = "email_verified")
    private Boolean emailVerified = false;
    
    @Column(name = "profile_image_url")
    private String profileImageUrl;
    
    @Column(name = "seller_rating", precision = 3, scale = 2)
    private BigDecimal sellerRating = BigDecimal.ZERO;
    
    @Column(name = "buyer_rating", precision = 3, scale = 2)
    private BigDecimal buyerRating = BigDecimal.ZERO;
    
    @Column(name = "total_sales_count")
    private Integer totalSalesCount = 0;
    
    @Column(name = "total_purchase_count")
    private Integer totalPurchaseCount = 0;
    
    // Relationships
    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Product> sellingProducts;
    
    @OneToMany(mappedBy = "bidder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Bid> bids;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CartItem> cartItems;
    
    @OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> purchases;
    
    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> sales;
    
    // Enums
    public enum UserType {
        BUYER, SELLER, BOTH
    }
    
    public enum AccountStatus {
        ACTIVE, SUSPENDED, PENDING
    }
    
    // Constructors
    public User() {}
    
    public User(String username, String email, String passwordHash, String firstName, String lastName) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.registrationDate = new Timestamp(System.currentTimeMillis());
    }
    
    // Getters and Setters
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getAddressLine1() { return addressLine1; }
    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }
    
    public String getAddressLine2() { return addressLine2; }
    public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    
    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }
    
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    
    public UserType getUserType() { return userType; }
    public void setUserType(UserType userType) { this.userType = userType; }
    
    public AccountStatus getAccountStatus() { return accountStatus; }
    public void setAccountStatus(AccountStatus accountStatus) { this.accountStatus = accountStatus; }
    
    public Timestamp getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(Timestamp registrationDate) { this.registrationDate = registrationDate; }
    
    public Timestamp getLastLogin() { return lastLogin; }
    public void setLastLogin(Timestamp lastLogin) { this.lastLogin = lastLogin; }
    
    public Boolean getEmailVerified() { return emailVerified; }
    public void setEmailVerified(Boolean emailVerified) { this.emailVerified = emailVerified; }
    
    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
    
    public BigDecimal getSellerRating() { return sellerRating; }
    public void setSellerRating(BigDecimal sellerRating) { this.sellerRating = sellerRating; }
    
    public BigDecimal getBuyerRating() { return buyerRating; }
    public void setBuyerRating(BigDecimal buyerRating) { this.buyerRating = buyerRating; }
    
    public Integer getTotalSalesCount() { return totalSalesCount; }
    public void setTotalSalesCount(Integer totalSalesCount) { this.totalSalesCount = totalSalesCount; }
    
    public Integer getTotalPurchaseCount() { return totalPurchaseCount; }
    public void setTotalPurchaseCount(Integer totalPurchaseCount) { this.totalPurchaseCount = totalPurchaseCount; }
    
    // Relationship getters and setters
    public List<Product> getSellingProducts() { return sellingProducts; }
    public void setSellingProducts(List<Product> sellingProducts) { this.sellingProducts = sellingProducts; }
    
    public List<Bid> getBids() { return bids; }
    public void setBids(List<Bid> bids) { this.bids = bids; }
    
    public List<CartItem> getCartItems() { return cartItems; }
    public void setCartItems(List<CartItem> cartItems) { this.cartItems = cartItems; }
    
    public List<Order> getPurchases() { return purchases; }
    public void setPurchases(List<Order> purchases) { this.purchases = purchases; }
    
    public List<Order> getSales() { return sales; }
    public void setSales(List<Order> sales) { this.sales = sales; }
    
    // Utility methods
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public String getFullAddress() {
        StringBuilder address = new StringBuilder();
        if (addressLine1 != null) address.append(addressLine1);
        if (addressLine2 != null) address.append(", ").append(addressLine2);
        if (city != null) address.append(", ").append(city);
        if (state != null) address.append(", ").append(state);
        if (zipCode != null) address.append(" ").append(zipCode);
        if (country != null) address.append(", ").append(country);
        return address.toString();
    }
}