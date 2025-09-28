-- Migration: Add BLOB support to product_images table
-- Date: 2025-09-28
-- Description: Add columns to store image data as BLOB for persistent storage on Render

-- Add new columns to product_images table
ALTER TABLE product_images 
ADD COLUMN IF NOT EXISTS image_data BYTEA,
ADD COLUMN IF NOT EXISTS content_type VARCHAR(100),
ADD COLUMN IF NOT EXISTS original_filename VARCHAR(255),
ADD COLUMN IF NOT EXISTS file_size BIGINT;

-- Update comments for documentation
COMMENT ON COLUMN product_images.image_data IS 'Binary image data stored as BLOB';
COMMENT ON COLUMN product_images.content_type IS 'MIME type of the image (e.g., image/jpeg, image/png)';
COMMENT ON COLUMN product_images.original_filename IS 'Original filename when uploaded';
COMMENT ON COLUMN product_images.file_size IS 'File size in bytes';
COMMENT ON COLUMN product_images.image_url IS 'Legacy file URL - kept for backward compatibility';

-- Create index on content_type for faster filtering
CREATE INDEX IF NOT EXISTS idx_product_images_content_type ON product_images(content_type);

-- Create index on file_size for queries
CREATE INDEX IF NOT EXISTS idx_product_images_file_size ON product_images(file_size);