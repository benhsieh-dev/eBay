package service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.logging.Logger;

@Service
public class DatabaseMigrationService {
    
    private static final Logger logger = Logger.getLogger(DatabaseMigrationService.class.getName());
    
    @PersistenceContext
    private EntityManager entityManager;
    
    /**
     * Run database migrations after application startup to avoid prepared statement conflicts
     */
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void runMigrations() {
        try {
            logger.info("Running database migrations...");
            
            // Add missing columns to product_images table if they don't exist
            addProductImageBlobColumns();
            
            // Create bids table if it doesn't exist
            createBidsTable();
            
            logger.info("Database migrations completed successfully");
            
        } catch (Exception e) {
            logger.severe("Database migration failed: " + e.getMessage());
            // Don't throw exception - let app continue with existing schema
        }
    }
    
    private void addProductImageBlobColumns() {
        try {
            // Check if image_data column exists
            String checkQuery = "SELECT column_name FROM information_schema.columns " +
                               "WHERE table_name = 'product_images' AND column_name = 'image_data'";
            
            var result = entityManager.createNativeQuery(checkQuery).getResultList();
            
            if (result.isEmpty()) {
                logger.info("Adding BLOB support columns to product_images table...");
                
                // Add new columns for BLOB storage
                entityManager.createNativeQuery(
                    "ALTER TABLE product_images ADD COLUMN image_data BYTEA"
                ).executeUpdate();
                
                entityManager.createNativeQuery(
                    "ALTER TABLE product_images ADD COLUMN content_type VARCHAR(100)"
                ).executeUpdate();
                
                entityManager.createNativeQuery(
                    "ALTER TABLE product_images ADD COLUMN original_filename VARCHAR(255)"
                ).executeUpdate();
                
                entityManager.createNativeQuery(
                    "ALTER TABLE product_images ADD COLUMN file_size BIGINT"
                ).executeUpdate();
                
                // Create indexes for performance
                entityManager.createNativeQuery(
                    "CREATE INDEX IF NOT EXISTS idx_product_images_content_type ON product_images(content_type)"
                ).executeUpdate();
                
                entityManager.createNativeQuery(
                    "CREATE INDEX IF NOT EXISTS idx_product_images_file_size ON product_images(file_size)"
                ).executeUpdate();
                
                logger.info("Successfully added BLOB support columns to product_images table");
            } else {
                logger.info("BLOB support columns already exist in product_images table");
            }
            
        } catch (Exception e) {
            logger.warning("Failed to add BLOB columns: " + e.getMessage());
            // Continue - existing schema will work for legacy image URLs
        }
    }
    
    private void createBidsTable() {
        try {
            // Check if bids table exists
            String checkTableQuery = "SELECT table_name FROM information_schema.tables " +
                                   "WHERE table_schema = 'public' AND table_name = 'bids'";
            
            var result = entityManager.createNativeQuery(checkTableQuery).getResultList();
            
            if (result.isEmpty()) {
                logger.info("Creating bids table...");
                
                // Create bids table
                String createTableSQL = """
                    CREATE TABLE bids (
                        bid_id SERIAL PRIMARY KEY,
                        product_id INTEGER NOT NULL,
                        bidder_id INTEGER NOT NULL,
                        bid_amount DECIMAL(10,2) NOT NULL,
                        bid_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        bid_status VARCHAR(50) DEFAULT 'ACTIVE',
                        is_winning_bid BOOLEAN DEFAULT FALSE,
                        bid_type VARCHAR(50) DEFAULT 'REGULAR',
                        max_proxy_amount DECIMAL(10,2),
                        FOREIGN KEY (product_id) REFERENCES products(product_id),
                        FOREIGN KEY (bidder_id) REFERENCES users(user_id)
                    )
                    """;
                
                entityManager.createNativeQuery(createTableSQL).executeUpdate();
                
                // Create indexes for performance
                entityManager.createNativeQuery(
                    "CREATE INDEX IF NOT EXISTS idx_bids_product_id ON bids(product_id)"
                ).executeUpdate();
                
                entityManager.createNativeQuery(
                    "CREATE INDEX IF NOT EXISTS idx_bids_bidder_id ON bids(bidder_id)"
                ).executeUpdate();
                
                entityManager.createNativeQuery(
                    "CREATE INDEX IF NOT EXISTS idx_bids_bid_time ON bids(bid_time)"
                ).executeUpdate();
                
                entityManager.createNativeQuery(
                    "CREATE INDEX IF NOT EXISTS idx_bids_winning ON bids(is_winning_bid)"
                ).executeUpdate();
                
                logger.info("Successfully created bids table with indexes");
            } else {
                logger.info("Bids table already exists");
            }
            
        } catch (Exception e) {
            logger.warning("Failed to create bids table: " + e.getMessage());
            e.printStackTrace();
        }
    }
}